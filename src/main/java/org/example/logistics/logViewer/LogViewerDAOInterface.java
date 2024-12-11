package org.example.logistics.logViewer;

import org.bson.Document;

import java.util.List;

public interface LogViewerDAOInterface {
    List<LogViewerVO> getAllLogs();

    List<LogViewerVO> getLogsByOperation(String operation);

    List<LogViewerVO> getLogsByDateRange(String startDate, String endDate);

    default LogViewerVO mapToVO(Document document) {
        return new LogViewerVO(
                document.getString("시간"),
                document.getString("작업"),
                document.getString("대상"),
                document.getString("메시지")
        );
    }
}
