package org.example.logistics.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static HikariDataSource dataSource;


    static {
        try {
            // HikariCP Configuration
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/Logistics"); // Database URL
            config.setUsername("root"); // MYSQL 로그인 username
            config.setPassword("1234"); // 비밀번호

            // HikariCP Optimization Settings
            config.setMaximumPoolSize(100); // 최대 커넥션 개수
            config.setMinimumIdle(2); // 최소 유휴 커넥션 개수
            config.setIdleTimeout(300000); // 커넥션 유휴 시간 (30초) 파라미터 숫자는 밀리초 기준입니다.
            config.setMaxLifetime(600000); // 커넥션의 최대 수명 (1분)
            config.setConnectionTimeout(20000); // 커넥션 획득 대기 시간 (20초)
            config.setLeakDetectionThreshold(2000); // 커넥션 누수 탐지 시간 (2초)
            config.addDataSourceProperty("cachePrepStmts", "true"); // PreparedStatement 캐싱
            config.addDataSourceProperty("prepStmtCacheSize", "250"); // 캐싱할 PreparedStatement 최대 개수
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // 캐싱할 SQL의 최대 길이

            // HikariCP 초기화
            dataSource = new HikariDataSource(config);
            // System.out.println("HikariCP 초기화 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("HikariCP 초기화 실패!");
        }
    }

    // Connection 객체 반환
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // DataSource 종료
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

