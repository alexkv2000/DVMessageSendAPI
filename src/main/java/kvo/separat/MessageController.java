package kvo.separat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/by-date-status")
    public ResponseEntity<List<Message>> getMessagesByDateAndStatus(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String status) {
        List<Message> messages;
        try {
            Timestamp start = Timestamp.valueOf(startDate);
            Timestamp end = Timestamp.valueOf(endDate);
            if (status.isBlank() || status.isEmpty()) {
                messages = messageRepository.findByDateCreateBetween(start, end);
            } else {
                messages = messageRepository.findByDateCreateBetweenAndStatus(start, end, status);
            }
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Message>> getMessageById(@PathVariable Long id) {
        try {
            Optional<Message> message = messageRepository.findById(id);
            if (message != null) {
                return ResponseEntity.ok(message);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMessage(
            @PathVariable Long id,
            @RequestBody MessageUpdateRequest request) throws IOException {
        Connection connectionMSSQL = null;
        Statement statement = null;
        StringBuilder pathFiles = new StringBuilder();

        String currentDir = System.getProperty("user.dir");
        String configPath = currentDir + "\\src\\main\\java\\kvo\\separat\\config\\setting.txt";
//        String configPath = "C:\\Users\\KvochkinAY\\IdeaProjects\\Spring\\Project\\DVMessageSend\\src\\main\\java\\kvo\\separat\\config\\setting.txt";
        ConfigLoader configLoader = new ConfigLoader(configPath);
        ConnectMSSQL connectMSSQL = new ConnectMSSQL(configLoader);
        String uuid = "";

            // найти UUID в сообщении
            Optional<Message> byId = messageRepository.findMessageById(id);
            if (byId.isPresent()) {
                String messageJson = byId.get().getMessage();
                ObjectMapper objectMapper = new ObjectMapper();// Парсинг JSON
                JsonNode jsonNode = objectMapper.readTree(messageJson);
                uuid = jsonNode.get("uuid").asText();// Извлечение uuid
                System.out.println("UUID: " + uuid);
            } else {
                System.out.println("Сообщение с id " + id + " не найдено");
            }
//
            //TODO нужно передовать UUID
        try {
            connectionMSSQL = DriverManager.getConnection(connectMSSQL.getURL(), connectMSSQL.getUSER(), connectMSSQL.getPASSWORD());
            // 1. Начинаем транзакцию MSSQL
            connectionMSSQL.setAutoCommit(false);

            // 2. Обновляем temp_message
            String updateSQL = "UPDATE [dbo].[temp_message] SET [status] = 'new' WHERE CAST([uuid] AS NVARCHAR(MAX)) = ?";
            try (PreparedStatement pstmt = connectionMSSQL.prepareStatement(updateSQL)) {
                pstmt.setString(1, uuid);
                int updatedRows = pstmt.executeUpdate();

                if (updatedRows == 0) {
                    connectionMSSQL.rollback();
                    throw new RuntimeException("Не найдено записей для обновления с UUID: " + uuid);
                }

                // 3. Если обновление прошло успешно - коммитим
                connectionMSSQL.commit();
                System.out.println("MSSQL: успешно обновлено " + updatedRows + " строк");

                // 4. Обновляем репозиторий (после коммита!)
                messageRepository.updateMessage(
                        id,
                        request.getStatus(),
                        request.getDateEnd(),
                        request.getServer(),
                        request.getNumAttempt()
                );
                System.out.println("Repository: статус сообщения обновлен");
            }
        } catch (Exception e) {
            if (connectionMSSQL != null) {
                try {
                    connectionMSSQL.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Ошибка при обновлении статуса", e);
        } finally {
            // Закрытие соединения
            if (connectionMSSQL != null) {
                try {
                    connectionMSSQL.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
//            return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        return ResponseEntity.internalServerError().build();
    }
}

class MessageUpdateRequest {
    private String status;
    private Timestamp dateEnd;
    private String server;
    private int numAttempt;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Timestamp dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getNumAttempt() {
        return numAttempt;
    }

    public void setNumAttempt(int numAttempt) {
        this.numAttempt = numAttempt;
    }
}
