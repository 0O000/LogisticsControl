package org.example.logistics.orders;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrdersVO {
    private int orderId;
    private int warehouseId; // Foreign Key
    private int branchId; // Foreign Key
    private Timestamp orderDate; // DATETIME
    private String status; // ENUM('pending', 'completed', 'cancelled')
    private int productId;
    private int quantity;
}
