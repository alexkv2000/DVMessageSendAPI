package kvo.separat.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "kafka_topic", nullable = false)
    private String kafkaTopic;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(name = "date_create", nullable = false)
    private Timestamp dateCreate;
    @Column(nullable = true)
    private String status;
    @Column(name = "date_end", nullable = true)
    private Timestamp dateEnd;
    private String server;
    @Column(name = "num_attempt")
    private Integer numAttempt;
    @Column(name = "typemes")
    private String typeMes;
}

