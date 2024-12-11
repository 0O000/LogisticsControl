package org.example.logistics.suppliers;

import org.example.logistics.service.CRUDLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SuppliersDAOInterface {
    // READ BY ID : 특정 공급자 데이터를 조회
    SuppliersVO getSuppliersById(int supplierId) throws SQLException;

    // CREATE : 새로운 공급자 데이터를 데이터베이스에 추가.
    int addSupplier(SuppliersVO supplier) throws SQLException;

    // READ ALL : 모든 공급자 데이터를 조회.
    List<SuppliersVO> getAllSuppliers() throws SQLException;

    // UPDATE : 공급자 데이터를 수정.
    void updateSupplier(SuppliersVO supplier) throws SQLException;

    // DELETE : 특정 공급자 데이터를 삭제.
    void deleteSupplier(int supplierId) throws SQLException;

    // 공급자 이름으로 검색
    List<SuppliersVO> getSuppliersByName(String namePart);
}
