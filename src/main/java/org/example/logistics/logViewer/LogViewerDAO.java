package org.example.logistics.logViewer;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.example.logistics.service.MongoDBConnection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

 // MongoDB와 상호작용하는 DAO 클래스

public class LogViewerDAO implements LogViewerDAOInterface {
    private static final MongoCollection<Document> collection;

    static {
        collection = MongoDBConnection.getDatabase().getCollection("logs");
    }

     // 모든 로그 조회

    @Override
    public List<LogViewerVO> getAllLogs() {
        List<LogViewerVO> logs = new ArrayList<>();
        FindIterable<Document> documents = collection.find();
        for (Document doc : documents) {
            logs.add(mapToVO(doc));
        }
        return logs;
    }

     // 작업 코드로 로그 조회

    @Override
    public List<LogViewerVO> getLogsByOperation(String operation) {
        List<LogViewerVO> logs = new ArrayList<>();
        FindIterable<Document> documents = collection.find(new Document("작업", operation));
        for (Document doc : documents) {
            logs.add(mapToVO(doc));
        }
        return logs;
    }

     // 날짜 범위로 로그 조회

    @Override
    public List<LogViewerVO> getLogsByDateRange(String startDate, String endDate) {
        List<LogViewerVO> logs = new ArrayList<>();
        try {
            // 날짜 변환
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(startDate, inputFormatter);
            LocalDateTime end = LocalDateTime.parse(endDate, inputFormatter);

            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
            String startFormatted = start.format(dbFormatter);
            String endFormatted = end.format(dbFormatter);

            // MongoDB 쿼리 실행
            FindIterable<Document> documents = collection.find(
                    new Document("시간", new Document("$gte", startFormatted).append("$lte", endFormatted))
            );

            for (Document doc : documents) {
                logs.add(mapToVO(doc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

}