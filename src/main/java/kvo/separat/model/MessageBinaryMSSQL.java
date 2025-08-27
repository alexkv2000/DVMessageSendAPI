package kvo.separat.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "temp_message")
public class MessageBinaryMSSQL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, columnDefinition = "TEXT")
    private String uuid;

    @Column(name = "namefiles", nullable = false, columnDefinition = "TEXT")
    private String namefiles;

    @Column(name = "type", nullable = true) // В таблице NULL
    private Integer type;

    @Column(name = "bin", nullable = true, length = 255)
    private String bin;

//    @Column(name = "status", nullable = true, length = 10)
//    private String status;
    @Column(name = "status", columnDefinition = "NCHAR(10)")
    private String status;
}

