package org.example.logistics.administratorsWarehouses;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// UI(User Interface) 클래스: 사용자와의 상호작용을 담당하며 DAO(Data Access Object)를 통해 데이터베이스와 연동
public class AdministratorsWarehousesUI {
    private final AdministratorsWarehousesDAO administrators_warehousesDAO; // DAO 객체 선언

    // 생성자 : DAO(Data Access Object) 초기화
    public AdministratorsWarehousesUI(AdministratorsWarehousesDAO  administrators_warehousesDAO) throws SQLException {
        // 데이터베이스 연동 객체 생성
        this.administrators_warehousesDAO = administrators_warehousesDAO;
    }

    // 프로그램 시작 및 사용자 인터페이스를 제공하는 메서드
    public void start() {
        // Scanner는 자바에서 콘솔 입력을 받기 위해 주로 사용되는 객체임.
        // Scanner 객체를 사용하여 콘솔에서 사용자 입력을 처리
        Scanner sc = new Scanner(System.in);

        while(true) { // 무한 루프로 사용자 입력을 처리
            // 메뉴 출력
            System.out.println("\n=== 관리자 창고 관리 시스템 ===");
            System.out.println("1. 전체 데이터 보기");
            System.out.println("2. ID로 데이터 조회");
            System.out.println("3. 데이터 추가");
            System.out.println("4. 데이터 수정");
            System.out.println("5. 데이터 삭제");
            System.out.println("6. 종료");
            System.out.print("선택: ");

            int select = sc.nextInt(); // 사용자의 메뉴 선택 입력 받기
            sc.nextLine(); // 입력 버퍼 정리 (nextInt 후 개행문자 처리)

            try {
                // 사용자 선택에 따라 메서드 호출
                switch (select) {
                    case 1:
                        getAllAdministrators_Warehouses(sc); // 전체 데이터 조회
                        break;
                    case 2:
                        getAdministrators_Warehouse(sc); // ID로 특정 데이터 조회
                        break;
                    case 3:
                        addAdministrators_Warehouse(sc); // 관리자 추가
                        break;
                    case 4:
                        updateAdministrators_Warehouse(sc); // 데이터 수정
                        break;
                    case 5:
                        deleteAdministrators_Warehouse(sc); // 데이터 삭제
                        break;
                    case 6:
                        System.out.println("Exiting..."); // 프로그램 종료 메시지
                        return; // 프로그램 종료
                    default:
                        System.out.println("Select a valid option!"); // 잘못된 입력 처리
                }
            } catch (Exception e) {
                // 예외 처리 : 입력 오류나 SQL 예외를 처리
                System.out.println("Error : " + e.getMessage());
            }
        }
        // Error 발생한 코드 :
        //sc.close();
        // 주의: Scanner를 여기서 닫으면 system.in이 닫히므로 프로그램 전체에서 입력 불가 상태가 됨
    }

    // 전체 데이터 조회
    private void getAllAdministrators_Warehouses(Scanner sc) throws SQLException {
        // DAO를 사용해 데이터베이스에서 모든 관리자-창고 데이터를 가져옴
        List<AdministratorsWarehousesVO> administrators_warehouses = administrators_warehousesDAO.getAllAdministrators_Warehouses();
        if (administrators_warehouses.isEmpty()) {
            System.out.println("연결된 데이터가 없습니다."); // 데이터가 없을 경우 메시지 출력
        } else { // 데이터가 존재하면 각 데이터를 출력
            // 테이블 헤더 출력
            System.out.println("\n=== 관리자 창고 관리 시스템 ===");
            String format = "| %-20s | %-10s | %-10s | %-25s |\n";
            System.out.println("+----------------------+------------+--------------+-------------------------------------+");
            System.out.printf(format, "관리자-창고 ID", "관리자 ID", "창고 ID", "생성 시간");
            System.out.println("+----------------------+------------+--------------+-------------------------------------+");
            for (AdministratorsWarehousesVO administrators_warehouse : administrators_warehouses) {
                System.out.println("관리자-창고ID: " + administrators_warehouse.getAdmin_warehouse_id() +
                        ", 관리자 ID: " + administrators_warehouse.getAdmin_id() +
                        ", 창고 ID: " + administrators_warehouse.getWarehouse_id() +
                        ", 생성 시간: " + administrators_warehouse.getAssigned_at()
                );
            }
            System.out.println("+----------------------+------------+---------------+-------------------------------------+");
        }
    }

    // ID로 특정 데이터 조회
    private void getAdministrators_Warehouse(Scanner sc) throws SQLException {
        System.out.print("데이터를 입력하세요: ");
        int admin_warehouse_id = sc.nextInt(); // 사용자가 조회할 ID 입력

        // DAO를 통해 해당 ID에 대한 데이터를 가져옴
        AdministratorsWarehousesVO administrators_warehouses = administrators_warehousesDAO.getAdministrators_Warehouse(admin_warehouse_id);
        if (administrators_warehouses == null) {
            System.out.println("연결된 데이터가 없습니다."); // 결과가 없을 경우 메시지 출력
        } else {
            // 테이블 형식으로 데이터 출력
            String format = "| %-20s | %-10s | %-10s | %-25s |\n";
            System.out.println("+----------------------+------------+------------+---------------------------+");
            System.out.printf(format, "창고-관리자 ID", "관리자 ID", "창고 ID", "생성 시간");
            System.out.println("+----------------------+------------+------------+---------------------------+");
            System.out.printf(format,
                    administrators_warehouses.getAdmin_warehouse_id(),
                    administrators_warehouses.getAdmin_id(),
                    administrators_warehouses.getWarehouse_id(),
                    administrators_warehouses.getAssigned_at());
            System.out.println("+----------------------+------------+------------+---------------------------+");

        }
    }

    // 새로운 데이터 추가
    private void addAdministrators_Warehouse(Scanner sc) throws SQLException {
        System.out.print("관리자 ID 추가: ");
        int admin_id = sc.nextInt(); // 추가할 관리자 ID 입력
        System.out.print("창고 ID 추가: ");
        int warehouse_id = sc.nextInt(); // 추가할 창고 ID 입력

        // VO 객체 생성 및 데이터 설정
        AdministratorsWarehousesVO administrators_warehouse = new AdministratorsWarehousesVO();
        administrators_warehouse.setAdmin_id(admin_id);
        administrators_warehouse.setWarehouse_id(warehouse_id);

        // DAO를 통해 데이터베이스에 삽입
        int admin_warehouse_id =  administrators_warehousesDAO.addAdministrators_Warehouse(administrators_warehouse);
        System.out.println("데이터가 성공적으로 추가되었습니다.");
        administrators_warehouse.setAdmin_warehouse_id(admin_warehouse_id);
        // 추가된 데이터를 테이블 형식으로 출력
        System.out.println("\n=== 추가된 데이터 ===");
        String format = "| %-20s | %-15s | %-10s | %-20s |\n";
        System.out.println("+----------------------+-----------------+------------+----------------------+");
        System.out.printf(format, "창고-관리자 ID", "관리자 ID", "창고 ID", "생성 시간");
        System.out.println("+----------------------+-----------------+------------+----------------------+");
        System.out.printf(format,
                administrators_warehouse.getAdmin_warehouse_id(),
                administrators_warehouse.getAdmin_id(),
                administrators_warehouse.getWarehouse_id());
        System.out.println("+----------------------+-----------------+------------+----------------------+");
    }

    // 데이터 수정
    private void updateAdministrators_Warehouse(Scanner sc) throws SQLException {
        System.out.print("수정할 창고 관리자 ID: ");
        int newAdminWarehouseID = sc.nextInt(); // 수정할 데이터의 ID 입력

        // DAO를 통해 기존 데이터를 조회
        AdministratorsWarehousesVO administrators_warehouse = administrators_warehousesDAO.getAdministrators_Warehouse(newAdminWarehouseID);
        if (administrators_warehouse.getAssigned_at() != null) { // 데이터가 존재할 경우
            System.out.println("\n=== 기존 데이터 ===");
            String format = "| %-20s | %-15s | %-10s | %-20s |\n";
            System.out.println("+----------------------+-----------------+------------+----------------------+");
            System.out.printf(format, "창고-관리자 ID", "관리자 ID", "창고 ID", "생성 시간");
            System.out.println("+----------------------+-----------------+------------+----------------------+");
            System.out.printf(format,
                    administrators_warehouse.getAdmin_warehouse_id(),
                    administrators_warehouse.getAdmin_id(),
                    administrators_warehouse.getWarehouse_id(),
                    administrators_warehouse.getAssigned_at());
            System.out.println("+----------------------+-----------------+------------+----------------------+\n");

            System.out.print("수정할 관리자 ID: ");
            int newAdminID = sc.nextInt(); // 새로운 관리자 ID 입력

            // VO 객체 업데이트
            administrators_warehouse.setAdmin_warehouse_id(newAdminWarehouseID);
            administrators_warehouse.setAdmin_id(newAdminID);

            // DAO를 통해 데이터베이스 업데이트
            administrators_warehousesDAO.updateAdministrators_Warehouse(administrators_warehouse);
            System.out.println("데이터가 성공적으로 수정되었습니다.");

            // 수정된 데이터를 테이블 형식으로 출력
            System.out.println("\n=== 수정된 데이터 ===");
            System.out.println("+----------------------+-----------------+------------+----------------------+");
            System.out.printf(format, "창고-관리자 ID", "관리자 ID", "창고 ID", "생성 시간");
            System.out.println("+----------------------+-----------------+------------+----------------------+");
            System.out.printf(format,
                    administrators_warehouse.getAdmin_warehouse_id(),
                    administrators_warehouse.getAdmin_id(),
                    administrators_warehouse.getWarehouse_id(),
                    administrators_warehouse.getAssigned_at());
            System.out.println("+----------------------+-----------------+------------+----------------------+");
        } else {
            System.out.println("데이터를 찾을 수가 없습니다."); // 데이터가 없는 경우 메시지 출력
        }
    }

    // 데이터 삭제
    private void deleteAdministrators_Warehouse(Scanner sc) throws SQLException {
        System.out.print("삭제할 Admin ID를 입력하세요: ");
        int adminID = sc.nextInt(); // 삭제할 관리자 ID 입력

        // DAO를 통해 데이터베이스에서 삭제
        administrators_warehousesDAO.deleteAdministrators_Warehouse(adminID);
        System.out.println("데이터가 성공적으로 삭제되었습니다.");
    }

    // 프로그램 진입점
    public static void function() {
        try {
            // UI 클래스 생성 및 시작
            AdministratorsWarehousesDAO  administrators_warehousesDAO = new AdministratorsWarehousesDAO();
            AdministratorsWarehousesUI ui = new AdministratorsWarehousesUI(administrators_warehousesDAO);
            ui.start(); // 프로그램 실행
        } catch (Exception e) {
            // 프로그램 실행 중 예외 처리
            e.printStackTrace();
        }
    }
}
