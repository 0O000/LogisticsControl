package org.example.logistics.administratorsWarehouses;

import org.example.logistics.service.CRUDLogger;
import org.example.logistics.service.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO(Data Access Object): 데이터베이스와 상호작용을 담당
// Administrator_Warehouses 테이블의 CRUD 작업을 처리
public class AdministratorsWarehousesDAO implements AdministratorsWarehousesDAOInterface {
    private static Connection conn; // 데이터베이스 연결 객체

    // Constructor: DatabaseConnection에서 Connection 가져오기
    // 이 연결 객체를 통해 데이터베이스 작업(쿼리 실행)을 수행함.
    public AdministratorsWarehousesDAO() throws SQLException {
        // DatabaseConnection 클래스에서 데이터베이스 연결을 받아 초기화
        this.conn = DatabaseConnection.getConnection();
    }

    // CREATE: 새로운 관리자-창고 데이터 추가
    @Override
    public int addAdministrators_Warehouse(AdministratorsWarehousesVO administrators_Warehouse) throws SQLException {
        int admin_warehouse_id = 0;
        // SQL INSERT 쿼리 정의
        String sql = "INSERT INTO Administrator_warehouses(admin_id, warehouse_id) VALUES(?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // PreparedStatement를 사용하여 ? 자리에 값을 안전하게 삽입
            stmt.setInt(1, administrators_Warehouse.getAdmin_id()); // 관리자 ID
            stmt.setInt(2, administrators_Warehouse.getWarehouse_id()); // 창고 ID
            // 쿼리 실행(데이터 삽입)
            stmt.executeUpdate(); // INSERT 쿼리 실행

            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                admin_warehouse_id = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생시 예외 출력
        }
        // 로그 기록
        CRUDLogger.log("CREATE", "창고_관리자", "추가한 관리자 ID: " + administrators_Warehouse.getAdmin_id());
        return admin_warehouse_id;
    }

    // READ ALL : 전체 관리자-창고 데이터 조회
    @Override
    public List<AdministratorsWarehousesVO> getAllAdministrators_Warehouses() throws SQLException {
        List<AdministratorsWarehousesVO> administrators_Warehouses = new ArrayList<>();
        // SQL SELECT 쿼리 정의
        String sql = "SELECT * FROM Administrator_Warehouses";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(); // SELECT 쿼리 실행
            // 결과 집합(ResultSet)에서 데이터를 읽어 VO 객체에 저장
            while(rs.next()) {
                AdministratorsWarehousesVO administrators_Warehouse = new AdministratorsWarehousesVO();
                administrators_Warehouse.setAdmin_warehouse_id(rs.getInt("admin_warehouse_id")); // 관리자-창고 ID
                administrators_Warehouse.setAdmin_id(rs.getInt("admin_id")); // 관리자 ID
                administrators_Warehouse.setWarehouse_id(rs.getInt("warehouse_id")); // 창고 ID
                administrators_Warehouse.setAssigned_at(rs.getTimestamp("assigned_at")); // 할당 시간
                administrators_Warehouses.add(administrators_Warehouse); // 리스트에 추가
                // 로그 기록
                CRUDLogger.log("READ", "창고_관리자", "전체 관리자 - 창고 ID: " + administrators_Warehouse.getAdmin_warehouse_id());
            }
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생시 예외 출력
        }
        return administrators_Warehouses; // 결과 리스트 반환
    }

    // READ BY ID : 특정 관리자-창고 데이터 조회
    @Override
    public AdministratorsWarehousesVO getAdministrators_Warehouse(int adminWarehouseId) {
        String sql = "SELECT * FROM Administrator_warehouses WHERE admin_warehouse_id = ?";// 조건에 맞는 관리자-창고 조회
        try(PreparedStatement stmt = AdministratorsWarehousesDAO.conn.prepareStatement(sql)) {
            stmt.setInt(1, adminWarehouseId); // SQL의 첫번째 ?에 adminWarehouseId 바인딩.
            ResultSet rs = stmt.executeQuery(); // 쿼리 실행 후 결과(ResultSet) 가져옴.
            if(rs.next()) {
                // 데이터가 존재하면 AdministratorsWarehousesVO 객체로 설정.
                AdministratorsWarehousesVO administrators_Warehouse = new AdministratorsWarehousesVO();
                administrators_Warehouse.setAdmin_warehouse_id(rs.getInt("admin_warehouse_id")); // 관리자-창고 ID
                administrators_Warehouse.setAdmin_id(rs.getInt("admin_id")); // 관리자 ID
                administrators_Warehouse.setWarehouse_id(rs.getInt("warehouse_id")); // 창고 ID
                administrators_Warehouse.setAssigned_at(rs.getTimestamp("Assigned_at")); // 할당 시간
                // 로그 기록
                CRUDLogger.log("READ", "창고_관리자", "선택한 관리자 - 창고 ID: " + administrators_Warehouse.getAdmin_warehouse_id());

                // 조회된 관리자 창고 정보를 반환
                return administrators_Warehouse;
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 처리
        }
        // 결과가 없거나 예외가 발생한 경우 null 반환
        return null;
    }

    // UPDATE : 관리자-창고 수정
    @Override
    public void updateAdministrators_Warehouse(AdministratorsWarehousesVO administrators_Warehouse) throws SQLException {
        // SQL UPDATE 쿼리 정의
        String sql = "UPDATE Administrator_Warehouses SET admin_id = ? WHERE admin_warehouse_id = ?";
        System.out.println(administrators_Warehouse); // 특정 ID 설정
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, administrators_Warehouse.getAdmin_id()); // 새로운 관리자 ID 수정
            stmt.setInt(2, administrators_Warehouse.getAdmin_warehouse_id()); // 관리자-창고 ID 수정
            stmt.executeUpdate(); // UPDATE 쿼리 실행
            // 로그 기록
            CRUDLogger.log("UPDATE", "창고_관리자", "수정한 관리자 ID: " + administrators_Warehouse.getAdmin_id());
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생시 예외 출력
        }
    }

    // DELETE : 특정 관리자-창고 데이터 삭제
    @Override
    public void deleteAdministrators_Warehouse(int admin_id) throws SQLException {
        // SQL DELETE 쿼리 정의
        String sql = "DELETE FROM Administrator_Warehouses WHERE admin_id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, admin_id); // 삭제할 관리자 ID 설정
            stmt.executeUpdate(); // DELETE 쿼리 실행
            // 로그 기록
            CRUDLogger.log("DELETE", "창고_관리자", "삭제한 관리자 ID: " + admin_id);
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생시 예외 출력
        }
    }
}
