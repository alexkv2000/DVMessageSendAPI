package kvo.separat.model;

import lombok.Getter;

import java.sql.Timestamp;
@Getter
public class MessageUpdateDto {
    // Getters and setters
    private String status;
    private Timestamp dateEnd;
    private String server;
    private int numAttempt;
}
