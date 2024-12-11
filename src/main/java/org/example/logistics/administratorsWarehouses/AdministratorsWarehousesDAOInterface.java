package org.example.logistics.administratorsWarehouses;

import java.sql.SQLException;
import java.util.List;

public interface AdministratorsWarehousesDAOInterface {
    // CREATE: 새로운 관리자-창고 데이터 추가
    int addAdministrators_Warehouse(AdministratorsWarehousesVO administrators_Warehouse) throws SQLException;

    // READ ALL : 전체 관리자-창고 데이터 조회
    List<AdministratorsWarehousesVO> getAllAdministrators_Warehouses() throws SQLException;

    // UPDATE : 관리자-창고 수정
    void updateAdministrators_Warehouse(AdministratorsWarehousesVO administrators_Warehouse) throws SQLException;

    // DELETE : 특정 관리자-창고 데이터 삭제
    void deleteAdministrators_Warehouse(int admin_id) throws SQLException;

    // READ BY ID : 특정 관리자-창고 데이터 조회
    AdministratorsWarehousesVO getAdministrators_Warehouse(int adminWarehouseId);
}
