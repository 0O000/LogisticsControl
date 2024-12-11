-- 1. Categories 테이블
CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE, -- 카테고리 이름
    description VARCHAR(100) -- 카테고리 설명
);

-- 2. Manufacturers 테이블
CREATE TABLE Manufacturers (
    manufacturer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    location VARCHAR(20),
    contact VARCHAR(50) CHECK (
        contact REGEXP '^010-[0-9]{3,4}-[0-9]{4}$' OR
        contact REGEXP '^(02|0[3-6][1-4]|05[1-5]|06[1-4])-[0-9]{3}-[0-9]{4}$'
    )
);

-- 3. Suppliers 테이블
CREATE TABLE Suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50) CHECK (
        contact REGEXP '^010-[0-9]{3,4}-[0-9]{4}$' OR
        contact REGEXP '^(02|0[3-6][1-4]|05[1-5]|06[1-4])-[0-9]{3}-[0-9]{4}$'
    ),
    location VARCHAR(255)
);

-- 4. Warehouses 테이블
CREATE TABLE Warehouses (
    warehouse_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    location VARCHAR(25) NOT NULL, 
    capacity INT NOT NULL CHECK (capacity >= 0) -- 창고 용량은 음수가 될 수 없음
);

-- 5. Branches 테이블
CREATE TABLE Branches (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- 6. Administrators 테이블
CREATE TABLE Administrators (
    admin_id INT AUTO_INCREMENT PRIMARY KEY, -- 관리자 ID (고유)
    user_id VARCHAR(50) UNIQUE NOT NULL, -- 로그인용 사용자 ID (중복 불가)
    password VARCHAR(255) NOT NULL, -- 로그인용 비밀번호 (암호화 필요) --> 주일님 설계
    role ENUM('root', 'general') NOT NULL, -- 관리자 역할 (루트 관리자, 일반 관리자)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 생성일
);

-- 7. Products 테이블
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id INT,
    price DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    manufacturer_id INT,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES Manufacturers(manufacturer_id) ON DELETE SET NULL
);

-- 8. Administrator_Warehouses 테이블
CREATE TABLE Administrator_Warehouses (
    admin_warehouse_id INT AUTO_INCREMENT PRIMARY KEY, -- 관계 ID
    admin_id INT NOT NULL, -- 관리자 ID
    warehouse_id INT NOT NULL, -- 창고 ID
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 할당 시간
    FOREIGN KEY (admin_id) REFERENCES Administrators(admin_id) ON DELETE CASCADE, -- 관리자가 삭제되면 관련 데이터 삭제
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id) ON DELETE CASCADE -- 창고 삭제 시 관련 데이터 삭제
);

-- 9. Warehouse_Inventory 테이블
CREATE TABLE Warehouse_Inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id INT NOT NULL,
    product_id INT, -- NULL 가능으로 변경
    quantity INT NOT NULL CHECK (quantity >= 0), -- 재고 수량은 음수가 될 수 없음
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE SET NULL, -- 제품 삭제 시 NULL 처리
    UNIQUE (warehouse_id, product_id)
);


-- 10. Orders 테이블
CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id INT NOT NULL, -- 배송될 창고
    branch_id INT, -- 주문 발생 지점 (NULL 허용)
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('대기', '완료', '취소') NOT NULL,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id) ON DELETE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE SET NULL -- 지점 삭제 시 주문 기록 보존
);

-- 11. Supplier_Products 테이블
CREATE TABLE Supplier_Products (
    supplier_product_id INT AUTO_INCREMENT PRIMARY KEY, -- PK
    supplier_id INT NOT NULL, -- 공급자 ID FK
    product_id INT NOT NULL, -- 제품 ID FK
    UNIQUE(supplier_id, product_id), -- 중복 방지: 한 공급자-제품 조합은 한 번만 저장
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE CASCADE, -- 공급자 삭제 시 관련 데이터 삭제
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE -- 제품 삭제 시 관련 데이터 삭제
);

-- 12. Incoming 테이블
CREATE TABLE Incoming (
    incoming_id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id INT NOT NULL,
    product_id INT NOT NULL,
    supplier_id INT,
    quantity INT NOT NULL CHECK (quantity > 0), -- 입고 수량은 0보다 커야 함
    incoming_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL
);

-- 13. Outgoing 테이블
CREATE TABLE Outgoing (
    outgoing_id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id INT NOT NULL,
    product_id INT, -- NULL 가능으로 변경
    order_id INT, -- 주문 ID와 연결
    quantity INT NOT NULL CHECK (quantity > 0), -- 출고 수량은 0보다 커야 함
    outgoing_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE SET NULL, -- 제품 삭제 시 NULL 처리
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE SET NULL
);

-- 1
INSERT INTO Categories (name, description) VALUES
('전자제품', '전자기기 관련 제품'),
('생활용품', '일상생활에 필요한 용품'),
('가구', '가구 및 인테리어 제품'),
('의류', '패션 및 의류 관련 제품'),
('서적', '책 및 문구류'),
('화장품', '뷰티 및 스킨케어 제품'),
('스포츠용품', '운동 및 스포츠 관련 제품'),
('완구', '장난감 및 유아용품'),
('주방용품', '주방 관련 도구'),
('반려동물용품', '반려동물 관련 제품');

-- 2
INSERT INTO Manufacturers (name, location, contact) VALUES
('삼성전자', '서울', '010-1234-5678'),
('LG전자', '서울', '02-987-6543'),
('한샘', '경기', '031-876-5432'),
('현대오토에버', '서울', '02-456-7890'),
('지엔씨', '부산', '051-654-9876'),
('애플코리아', '서울', '02-234-5678'),
('샤오미', '서울', '010-9999-8888'),
('소니코리아', '서울', '02-765-4321'),
('파나소닉', '서울', '02-456-7890'),
('필립스', '서울', '02-678-1234');

-- 3
INSERT INTO Suppliers (name, contact, location) VALUES
('삼성물산', '010-1111-2222', '서울'),
('주일무역', '02-123-4567', '부산'),
('가연물류', '032-444-5555', '인천'),
('대우상사', '010-3333-4444', '광주'),
('민호무역', '010-5555-6666', '대전'),
('웅진상사', '02-345-6789', '서울'),
('나래물류', '051-123-4567', '부산'),
('두산무역', '010-7777-8888', '경기'),
('현대무역', '031-876-5432', '경기'),
('SK무역', '032-987-6543', '인천');

-- 4
INSERT INTO Branches (name, location) VALUES
('서울중앙지점', '서울특별시 강남구 삼성동'),
('부산서부지점', '부산광역시 사상구 괘법동'),
('창원중부지점', '창원특례시 성산구 성주동'),
('광주남부지점', '광주광역시 남구 봉선동'),
('대전북부지점', '대전광역시 대덕구 오정동'),
('서울서부지점', '서울특별시 서대문구 신촌동'),
('부산중앙지점', '부산광역시 중구 중앙동'),
('대구동부지점', '대구광역시 동구 동대구역'),
('울산북부지점', '울산광역시 북구 송정동'),
('인천남부지점', '인천광역시 남동구 구월동');

-- 5
INSERT INTO Warehouses (name, location, capacity) VALUES
('서울창고', '서울', 1000),
('부산창고', '부산', 2000),
('인천창고', '인천', 1500),
('제3보급창고', '광주', 1800),
('A보급창고', '대전', 1200);

-- 6
INSERT INTO Administrators (user_id, password, role) VALUES
('admin1', 'password1', 'root'),
('admin2', 'password2', 'general'),
('admin3', 'password3', 'general'),
('admin4', 'password4', 'general'),
('admin5', 'password5', 'root');

-- 7
INSERT INTO Products (name, description, category_id, price, manufacturer_id) VALUES
('아이폰1', '최신 스마트폰', 1, 1000000, 6),
('아이폰2', '최신 스마트폰', 1, 950000, 6),
('아이폰3', '최신 스마트폰', 1, 1100000, 6),
('아이폰4', '최신 스마트폰', 1, 1050000, 6),
('아이폰5', '최신 스마트폰', 1, 990000, 6),
('아이폰6', '최신 스마트폰', 1, 1150000, 6),
('아이폰7', '최신 스마트폰', 1, 1200000, 6),
('갤럭시S2', '최신 스마트폰', 1, 980000, 1),
('갤럭시S3', '최신 스마트폰', 1, 1120000, 1),
('갤럭시S4', '최신 스마트폰', 1, 1200000, 1),
('세탁기1', '고효율 세탁기1', 1, 500000, 1),
('세탁기2', '고효율 세탁기2', 1, 550000, 1),
('세탁기3', '고효율 세탁기3', 1, 520000, 2),
('세탁기4', '고효율 세탁기4', 1, 530000, 2),
('세탁기5', '고효율 세탁기5', 1, 510000, 2),
('에어팟프로', '3인용 소파1', 1, 300000, 6),
('이케아 소파2', '3인용 소파2', 3, 320000, 7),
('이케아 소파3', '3인용 소파3', 3, 350000, 8),
('이케아 소파4', '3인용 소파4', 3, 330000, 9),
('이케아 소파5', '3인용 소파5', 3, 310000, 10),
('칼하트 티셔츠1', '면 소재 티셔츠1', 4, 120000, 1),
('칼하트 티셔츠2', '면 소재 티셔츠2', 4, 130000, 2),
('칼하트 티셔츠3', '면 소재 티셔츠3', 4, 125000, 3),
('칼하트 티셔츠4', '면 소재 티셔츠4', 4, 118000, 4),
('칼하트 티셔츠5', '면 소재 티셔츠5', 4, 122000, 5),
('문구세트1', '종합 문구세트1', 5, 15000, 6),
('문구세트2', '종합 문구세트2', 5, 17000, 7),
('문구세트3', '종합 문구세트3', 5, 16000, 8),
('문구세트4', '종합 문구세트4', 5, 15500, 9),
('문구세트5', '종합 문구세트5', 5, 15000, 10),
('화장품1', '스킨케어 화장품1', 6, 40000, 1),
('화장품2', '메이크업 세트1', 6, 60000, 2),
('화장품3', '스킨케어 화장품2', 6, 45000, 3),
('화장품4', '메이크업 세트2', 6, 62000, 4),
('화장품5', '스킨케어 화장품3', 6, 50000, 5),
('스포츠용품1', '축구공1', 7, 25000, 6),
('스포츠용품2', '농구공1', 7, 27000, 7),
('스포츠용품3', '야구공1', 7, 20000, 8),
('다용도물티슈', '물티슈', 5, 22000, 9),
('패브리즈', '방향제', 5, 18000, 10);

-- 8
INSERT INTO Administrator_Warehouses (admin_id, warehouse_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(1, 2),
(2, 3),
(3, 4),
(4, 5),
(5, 1),
(1, 3),
(2, 4),
(3, 5),
(4, 1),
(5, 2),
(1, 4),
(2, 5),
(3, 1),
(4, 2),
(5, 3);

-- 9
INSERT INTO Warehouse_Inventory (warehouse_id, product_id, quantity) VALUES
(1, 1, 100),
(1, 2, 150),
(1, 3, 200),
(1, 4, 50),
(1, 5, 80),
(2, 6, 200),
(2, 7, 250),
(2, 8, 300),
(2, 9, 150),
(2, 10, 100),
(3, 11, 120),
(3, 12, 130),
(3, 13, 140),
(3, 14, 150),
(3, 15, 160),
(4, 16, 180),
(4, 17, 190),
(4, 18, 200),
(4, 19, 170),
(4, 20, 160),
(5, 21, 150),
(5, 22, 140),
(5, 23, 130),
(5, 24, 120),
(5, 25, 110),
(1, 26, 100),
(2, 27, 90),
(3, 28, 80),
(4, 29, 70),
(5, 30, 60);

-- 10
INSERT INTO Orders (warehouse_id, branch_id, status) VALUES
(1, 1, '대기'),
(1, 2, '완료'),
(1, 3, '취소'),
(1, 4, '대기'),
(1, 5, '완료'),
(2, 6, '취소'),
(2, 7, '대기'),
(2, 8, '완료'),
(2, 9, '취소'),
(2, 10, '대기'),
(3, 1, '완료'),
(3, 2, '취소'),
(3, 3, '대기'),
(3, 4, '완료'),
(3, 5, '취소'),
(4, 6, '대기'),
(4, 7, '완료'),
(4, 8, '취소'),
(4, 9, '대기'),
(4, 10, '완료'),
(5, 1, '취소'),
(5, 2, '대기'),
(5, 3, '완료'),
(5, 4, '취소'),
(5, 5, '대기'),
(1, 6, '완료'),
(1, 7, '취소'),
(1, 8, '대기'),
(1, 9, '완료'),
(1, 10, '취소'),
(2, 1, '대기'),
(2, 2, '완료'),
(2, 3, '취소'),
(2, 4, '대기'),
(2, 5, '완료'),
(3, 6, '취소'),
(3, 7, '대기'),
(3, 8, '완료'),
(3, 9, '취소'),
(3, 10, '대기');

-- 11
INSERT INTO Supplier_Products (supplier_id, product_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(2, 6),
(2, 7),
(2, 8),
(2, 9),
(2, 10),
(3, 11),
(3, 12),
(3, 13),
(3, 14),
(3, 15),
(4, 16),
(4, 17),
(4, 18),
(4, 19),
(4, 20),
(5, 21),
(5, 22),
(5, 23),
(5, 24),
(5, 25),
(6, 26),
(6, 27),
(6, 28),
(6, 29),
(6, 30),
(7, 31),
(7, 32),
(7, 33),
(7, 34),
(7, 35),
(8, 36),
(8, 37),
(8, 38),
(8, 39),
(8, 40),
(9, 1),
(9, 6),
(9, 11),
(9, 16),
(9, 21),
(10, 26),
(10, 31),
(10, 36),
(10, 2),
(10, 7);

-- 12
INSERT INTO Incoming (warehouse_id, product_id, supplier_id, quantity) VALUES
(1, 1, 1, 150), -- 100 = 150 - 50
(1, 2, 2, 200), -- 150 = 200 - 50
(1, 3, 3, 300), -- 200 = 300 - 100
(1, 4, 4, 100), -- 50 = 100 - 50
(1, 5, 5, 120), -- 80 = 120 - 40
(2, 6, 6, 300), -- 200 = 300 - 100
(2, 7, 7, 400), -- 250 = 400 - 150
(2, 8, 8, 500), -- 300 = 500 - 200
(2, 9, 9, 250), -- 150 = 250 - 100
(2, 10, 10, 150), -- 100 = 150 - 50
(3, 11, 1, 180), -- 120 = 180 - 60
(3, 12, 2, 200), -- 130 = 200 - 70
(3, 13, 3, 220), -- 140 = 220 - 80
(3, 14, 4, 250), -- 150 = 250 - 100
(3, 15, 5, 210), -- 160 = 210 - 50
(4, 16, 6, 300), -- 180 = 300 - 120
(4, 17, 7, 290), -- 190 = 290 - 100
(4, 18, 8, 350), -- 200 = 350 - 150
(4, 19, 9, 220), -- 170 = 220 - 50
(4, 20, 10, 200), -- 160 = 200 - 40
(5, 21, 1, 250), -- 150 = 250 - 100
(5, 22, 2, 200), -- 140 = 200 - 60
(5, 23, 3, 170), -- 130 = 170 - 40
(5, 24, 4, 150), -- 120 = 150 - 30
(5, 25, 5, 140), -- 110 = 140 - 30
(1, 26, 6, 200), -- 100 = 200 - 100
(2, 27, 7, 150), -- 90 = 150 - 60
(3, 28, 8, 120), -- 80 = 120 - 40
(4, 29, 9, 110), -- 70 = 110 - 40
(5, 30, 10, 100); -- 60 = 100 - 40

-- 13
INSERT INTO Outgoing (warehouse_id, product_id, order_id, quantity) VALUES
(1, 1, 1, 50), -- 150 - 50 = 100
(1, 2, 2, 50), -- 200 - 50 = 150
(1, 3, 3, 100), -- 300 - 100 = 200
(1, 4, 4, 50), -- 100 - 50 = 50
(1, 5, 5, 40), -- 120 - 40 = 80
(2, 6, 6, 100), -- 300 - 100 = 200
(2, 7, 7, 150), -- 400 - 150 = 250
(2, 8, 8, 200), -- 500 - 200 = 300
(2, 9, 9, 100), -- 250 - 100 = 150
(2, 10, 10, 50), -- 150 - 50 = 100
(3, 11, 11, 60), -- 180 - 60 = 120
(3, 12, 12, 70), -- 200 - 70 = 130
(3, 13, 13, 80), -- 220 - 80 = 140
(3, 14, 14, 100), -- 250 - 100 = 150
(3, 15, 15, 50), -- 210 - 50 = 160
(4, 16, 16, 120), -- 300 - 120 = 180
(4, 17, 17, 100), -- 290 - 100 = 190
(4, 18, 18, 150), -- 350 - 150 = 200
(4, 19, 19, 50), -- 220 - 50 = 170
(4, 20, 20, 40), -- 200 - 40 = 160
(5, 21, 21, 100), -- 250 - 100 = 150
(5, 22, 22, 60), -- 200 - 60 = 140
(5, 23, 23, 40), -- 170 - 40 = 130
(5, 24, 24, 30), -- 150 - 30 = 120
(5, 25, 25, 30), -- 140 - 30 = 110
(1, 26, 26, 100), -- 200 - 100 = 100
(2, 27, 27, 60), -- 150 - 60 = 90
(3, 28, 28, 40), -- 120 - 40 = 80
(4, 29, 29, 40), -- 110 - 40 = 70
(5, 30, 30, 40); -- 100 - 40 = 60


