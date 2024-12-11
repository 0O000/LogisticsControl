package org.example.logistics.suppliers;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 공급자 관리 시스템의 UI 클래스
// 사용자 입력을 받아 DAO와 연동하여 CRUD 기능을 수행함
public class SuppliersUI {
    private SuppliersDAOInterface suppliersDAO; // 공급자 정보를 관리하는 DAO 객체

    // 생성자: SuppliersDAO 객체를 초기화
    public SuppliersUI() throws SQLException {
        this.suppliersDAO = new SuppliersDAO(); // DAO 생성 시 SQLException 발생 가능
    }

    // 프로그램 시작점 - 사용자 입력을 받아 기능 실행
    public void start() {
        //Scanner는 자바에서 콘솔 입력을 받기 위해 주로 사용되는 객체임.
        Scanner sc = new Scanner(System.in); // 사용자 입력을 받기 위한 Scanner 객체

        while(true) { // 프로그램 종료 명령 전까지 무한 루프
            System.out.println("\n=== 공급자 관리 시스템 ===");
            System.out.println("1. 공급자 전체 보기");
            System.out.println("2. 공급자 ID로 조회해서 보기");
            System.out.println("3. 공급자 이름으로 검색");
            System.out.println("4. 공급자 추가");
            System.out.println("5. 공급자 수정");
            System.out.println("6. 공급자 삭제");
            System.out.println("7. 종료");
            System.out.print("선택: ");

            int select = sc.nextInt(); // 사용자 선택 입력 받음
            sc.nextLine(); // 개행 문자 제거

            try {
                // 사용자 입력에 따라 기능 호출
                switch(select) {
                    case 1:
                        getAllSuppliers(sc);
                        break;
                    case 2:
                        getSuppliersById(sc);
                        break;
                    case 3:
                        searchSuppliersByName(sc);
                        break;
                    case 4:
                        addSupplier(sc);
                        break;
                    case 5:
                        updateSupplier(sc);
                        break;
                    case 6:
                        deleteSupplier(sc);
                        break;
                    case 7:
                        System.out.println("종료 중...");
                        return; // 프로그램 종료
                    default:
                        System.out.println("잘못된 선택입니다. 다시 시도하세요.");
                }
            } catch (Exception e) {
                System.out.print("오류 발생: " + e.getMessage());
            }
        }
        // Error 발생한 코드 :
        //sc.close();
        // 주의: Scanner를 여기서 닫으면 system.in이 닫히므로 프로그램 전체에서 입력 불가 상태가 됨
    }

    // 공급자 전체 목록 조회
    private void getAllSuppliers(Scanner sc) throws SQLException {
        List<SuppliersVO> suppliers = suppliersDAO.getAllSuppliers(); // DAO에서 공급자 목록 조회
        if (suppliers.isEmpty()) { // 데이터가 없을 경우 메시지 출력
            System.out.println("데이터가 없습니다.");
        } else { // 데이터를 순회하며 출력
            System.out.println("\n=== 공급자 관리 시스템 ===");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10s | %-18s | %-18s | %-18s |\n", "ID", "Name", "Contact", "Location");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            for (SuppliersVO supplier : suppliers) {
                System.out.printf("| %-10d | %-18s | %-18s | %-18s |\n",
                        supplier.getSupplierId(), supplier.getName(), supplier.getContact(), supplier.getLocation());
            }
            System.out.println("+------------+--------------------+--------------------+--------------------+");
        }
    }

    // 특정 공급자 조회
    private void getSuppliersById(Scanner sc) throws SQLException {
        System.out.print("공급자 ID: ");
        int supplierId = sc.nextInt(); // 사용자로부터 ID 입력받음

        SuppliersVO suppliers = suppliersDAO.getSuppliersById(supplierId); // ID로 공급자 조회
        if (suppliers == null) { // 조회 결과가 없으면 메시지 출력
            System.out.println("해당 공급자 ID를 찾을 수 없습니다.");
        } else { // 조회 결과를 출력
            System.out.println("\n=== 공급자 상세 정보 ===");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10s | %-18s | %-18s | %-18s |\n", "ID", "Name", "Contact", "Location");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10d | %-18s | %-18s | %-18s |\n",
                    suppliers.getSupplierId(), suppliers.getName(), suppliers.getContact(), suppliers.getLocation());
            System.out.println("+------------+--------------------+--------------------+--------------------+");
        }
    }

    // 새 공급자 추가
    private void addSupplier(Scanner sc) throws SQLException {
        System.out.print("공급자 이름: ");
        String name = sc.nextLine(); // 이름 입력받음
        System.out.print("공급자 전화번호: ");
        String contact = sc.nextLine(); // 전화번호 입력받음
        System.out.print("공급자 위치: ");
        String location = sc.nextLine(); // 위치 입력받음

        SuppliersVO supplier = new SuppliersVO(); // VO 객체 생성
        // Error 발생한 코드 :
        //supplier.setSupplierId(Integer.parseInt(sc.nextLine()));
        // ==> supplier_id에 auto_increment가 설정되어 있어서 굳이 값을 입력받을 필요가 없음.
        supplier.setName(name); // 입력값 설정
        supplier.setContact(contact);
        supplier.setLocation(location);

        int supplierId = suppliersDAO.addSupplier(supplier); // DAO를 통해 데이터 추가
        System.out.println("\n=== 추가된 공급자 정보 ===");
        System.out.println("+------------+--------------------+--------------------+--------------------+");
        System.out.printf("| %-10s | %-18s | %-18s | %-18s |\n", "ID", "Name", "Contact", "Location");
        System.out.println("+------------+--------------------+--------------------+--------------------+");
        System.out.printf("| %-10s | %-18s | %-18s | %-18s |\n", supplierId, name, contact, location);
        System.out.println("+------------+--------------------+--------------------+--------------------+");
        System.out.println("공급자가 성공적으로 추가되었습니다.");
    }

    // 공급자 정보 수정
    private void updateSupplier(Scanner sc) throws SQLException {
        System.out.print("수정할 공급자 아이디: ");
        int id = sc.nextInt();
        sc.nextLine();

        SuppliersVO supplier = suppliersDAO.getSuppliersById(id); // 기존 데이터 조회
        if (supplier != null) {
            System.out.println("\n=== 기존 공급자 정보 ===");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10s | %-18s | %-18s | %-18s |\n", "ID", "Name", "Contact", "Location");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10d | %-18s | %-18s | %-18s |\n",
                    supplier.getSupplierId(), supplier.getName(), supplier.getContact(), supplier.getLocation());
            System.out.println("+------------+--------------------+--------------------+--------------------+");

            // 수정할 데이터 입력받기
            System.out.print("새 공급자 이름: ");
            String newName = sc.nextLine();

            System.out.print("새 공급자 전화번호: ");
            String newContact = sc.nextLine();

            System.out.print("새 공급자 위치: ");
            String newLocation = sc.nextLine();

            // VO 객체에 새 값 설정
            supplier.setName(newName);
            supplier.setContact(newContact);
            supplier.setLocation(newLocation);

            suppliersDAO.updateSupplier(supplier); // DAO를 통해 데이터 업데이트

            System.out.println("\n=== 수정된 공급자 정보 ===");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10s | %-18s | %-18s | %-18s |\n", "ID", "Name", "Contact", "Location");
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.printf("| %-10d | %-18s | %-18s | %-18s |\n",
                    supplier.getSupplierId(), supplier.getName(), supplier.getContact(), supplier.getLocation());
            System.out.println("+------------+--------------------+--------------------+--------------------+");
            System.out.println("공급자 정보가 성공적으로 수정되었습니다.");
        } else {
            System.out.println("공급자 정보를 찾을 수가 없습니다.");
        }
    }

    // 공급자 삭제
    private void deleteSupplier(Scanner sc) throws SQLException {
        System.out.print("삭제할 공급자 ID: ");
        int id = sc.nextInt();

        suppliersDAO.deleteSupplier(id); // DAO를 통해 데이터 삭제
        System.out.println("공급자 ID가 성공적으로 삭제되었습니다.");
    }

    // 공급자 이름으로 검색
    // 공급자의 이름 일부를 입력받아 해당 이름을 포함하는 공급자를 검색
    private void searchSuppliersByName(Scanner sc) throws SQLException {
        System.out.print("공급자 이름 일부를 입력하세요: ");
        String namePart = sc.nextLine(); // 사용자가 입력한 이름 부분 문자열 받기

        // DAO 메서드를 통해 이름으로 검색
        List<SuppliersVO> suppliersList = suppliersDAO.getSuppliersByName(namePart);

        // 검색 결과 출력
        if (suppliersList != null && !suppliersList.isEmpty()) {
            System.out.println("\n=== 검색 결과 ===");

            // 테이블 헤더 출력
            System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Contact", "Location");
            System.out.println("------------------------------------------------------------------------------------");

            // 각 행의 데이터 출력
            for (SuppliersVO supplier : suppliersList) {
                System.out.printf("%-10d %-20s %-20s %-30s\n",
                        supplier.getSupplierId(),
                        supplier.getName(),
                        supplier.getContact(),
                        supplier.getLocation());
            }
        } else {
            System.out.println("검색된 공급자가 없습니다.");
        }
    }

    // 메인 메서드: 프로그램 진입점
    public static void function() {
        try {
            SuppliersUI ui = new SuppliersUI(); // UI 객체 생성
            ui.start(); // 프로그램 실행
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        }
    }
}
