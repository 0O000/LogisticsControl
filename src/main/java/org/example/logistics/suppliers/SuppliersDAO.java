package org.example.logistics.suppliers;

import org.example.logistics.service.CRUDLogger;
import org.example.logistics.service.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// SuppliersDAO 클래스는 데이터베이스와 상호작용하여 공급자 데이터를 관리하는 역할을 함.
// DAO(Data Access Object) 패턴을 사용하여 데이터베이스 작업을 캡슐화함.
public class SuppliersDAO implements SuppliersDAOInterface {
    private static Connection conn;

    // Constructor: DatabaseConnection에서 Connection 가져오기
    // 생성자 : DatabaseConnection에서 Connection 객체를 가져옴.
    // 이를 통해 데이터베이스와 연결된 상태에서 작업 가능.
    public SuppliersDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    // CREATE : 새로운 공급자 데이터를 데이터베이스에 추가.
    @Override
    public int addSupplier(SuppliersVO supplier) throws SQLException {
        int supplierId = 0;
        // INSERT SQL 쿼리 작성: 공급자의 이름, 연락처, 위치 데이터를 삽입.
        String sql = "INSERT INTO Suppliers(name, contact, location) VALUES(?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // SQL의 ?에 데이터를 바인딩.
            stmt.setString(1, supplier.getName()); // 공급자 이름
            stmt.setString(2, supplier.getContact()); // 공급자 연락처
            stmt.setString(3, supplier.getLocation()); // 공급자 위치
            stmt.executeUpdate(); // 쿼리 실행

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                supplierId = rs.getInt(1);
                System.out.println(supplierId + "=============");
            }
        } catch (Exception e) {

            // 예외 발생 시 스택 트레이스를 출력.

            e.printStackTrace();
        }
        // 로그 기록
        CRUDLogger.log("CREATE", "공급자", "추가한 공급자 이름: " + supplier.getName());
        return supplierId;
    }

    // READ ALL : 모든 공급자 데이터를 조회.
    @Override
    public List<SuppliersVO> getAllSuppliers() throws SQLException {
        List<SuppliersVO> suppliers = new ArrayList<>(); // 결과를 저장할 리스트 생성.
        String sql = "SELECT * FROM Suppliers"; // 모든 공급자를 조회하는 SQL 쿼리.
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(); // 쿼리 실행 후 결과(ResultSet) 가져옴.
            while (rs.next()) {
                // ResultSet의 각 행의 데이터를 SuppliersVO 객체로 설정.
                SuppliersVO supplier = new SuppliersVO();
                supplier.setSupplierId(rs.getInt("supplier_id")); // 공급자 ID
                supplier.setName(rs.getString("name")); // 공급자 이름
                supplier.setContact(rs.getString("contact")); // 공급자 연락처
                supplier.setLocation(rs.getString("location")); // 공급자 위치
                suppliers.add(supplier); // 리스트에 추가
                // 로그 기록
                CRUDLogger.log("READ", "공급자", "전체 공급자 ID: " + supplier.getSupplierId());
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 처리
        }
        return suppliers; // 조회 결과 리스트 반환
    }

    // READ BY ID : 특정 공급자 데이터를 조회
    @Override
    public SuppliersVO getSuppliersById(int supplierId) throws SQLException {
        // Error 발생한 코드 :
        //SuppliersVO supplier = new SuppliersVO(); --> 중복자 선언으로 인한 Error가 발생했었음.
        String sql = "SELECT * FROM Suppliers WHERE supplier_id = ?"; // 조건에 맞는 공급자 조회
        try (PreparedStatement stmt = SuppliersDAO.conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId); // SQL의 첫번째 ?에 supplierId 바인딩.
            ResultSet rs = stmt.executeQuery(); // 쿼리 실행 후 결과(ResultSet) 가져옴.
            if (rs.next()) {
                // 데이터가 존재하면 SuppliersVO 객체로 설정.
                SuppliersVO supplier = new SuppliersVO();
                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setName(rs.getString("name"));
                supplier.setContact(rs.getString("contact"));
                supplier.setLocation(rs.getString("location"));
                // 로그 기록
                CRUDLogger.log("READ", "공급자", "선택한 공급자 ID: " + supplier.getSupplierId());
                return supplier; // 매핑된 객체 반환
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 처리
        }
        return null; // 데이터가 없으면 null 반환
    }

    // UPDATE : 공급자 데이터를 수정.
    @Override
    public void updateSupplier(SuppliersVO supplier) throws SQLException {
        // 공급자의 데이터를 업데이트하는 SQL 쿼리.
        String sql = "UPDATE Suppliers SET name = ?, contact = ?, location = ? WHERE supplier_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // SQL의 ?에 데이터를 바인딩.
            stmt.setString(1, supplier.getName()); // 수정할 이름
            stmt.setString(2, supplier.getContact()); // 수정할 연락처
            stmt.setString(3, supplier.getLocation()); // 수정할 위치
            stmt.setInt(4, supplier.getSupplierId()); // 수정 대상 공급자의 ID
            stmt.executeUpdate(); // 쿼리 실행
            // 로그 기록
            CRUDLogger.log("UPDATE", "공급자", "수정한 공급자 ID: " + supplier.getSupplierId());
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 처리
        }
    }

    // DELETE : 특정 공급자 데이터를 삭제.
    @Override
    public void deleteSupplier(int supplierId) throws SQLException {
        String sql = "DELETE FROM Suppliers WHERE supplier_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId); // SQL의 ?에 supplierId 바인딩.
            stmt.executeUpdate(); // 쿼리 실행
            // 로그 기록
            CRUDLogger.log("DELETE", "공급자", "삭제한 공급자 ID: " + supplierId);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 처리
        }
    }

    // 공급자 이름으로 검색
    public List<SuppliersVO> getSuppliersByName(String namePart) {
        // SQL 쿼리: 이름에 입력된 일부 문자열을 포함하는 공급자를 조회 (LIKE를 사용하여 부분 검색)
        String sql = "SELECT * FROM Suppliers WHERE name LIKE ?";

        // 결과를 저장할 리스트
        List<SuppliersVO> suppliersList = new ArrayList<>();

        // try-with-resources로 PreparedStatement를 자동으로 닫도록 처리
        try (PreparedStatement stmt = SuppliersDAO.conn.prepareStatement(sql)) {

            // 이름 부분 입력값을 LIKE 연산자와 함께 설정, %는 와일드카드로 검색 패턴을 의미
            stmt.setString(1, "%" + namePart + "%");

            // 쿼리 실행 후 결과(ResultSet) 받기
            ResultSet rs = stmt.executeQuery();

            // 조회된 데이터를 리스트에 저장
            while (rs.next()) {
                SuppliersVO supplier = new SuppliersVO();

                // 각 컬럼의 값을 VO 객체에 설정
                supplier.setSupplierId(rs.getInt("supplier_id")); // 공급자 ID
                supplier.setName(rs.getString("name")); // 공급자 이름
                supplier.setContact(rs.getString("contact")); // 연락처
                supplier.setLocation(rs.getString("location")); // 위치

                // 리스트에 공급자 객체 추가
                suppliersList.add(supplier);
                // 로그 기록
                CRUDLogger.log("READ", "공급자", "공급자 이름으로 검색: " + namePart);
            }

            // 결과가 존재할 경우 해당 리스트를 반환
            if (!suppliersList.isEmpty()) {
                return suppliersList;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 발생 시 처리
        }

        // 결과가 없으면 null 반환
        return null;
    }
}
