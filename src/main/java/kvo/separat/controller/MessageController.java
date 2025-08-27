package kvo.separat.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kvo.separat.config.ConfigLoader;
import kvo.separat.model.Message;
import kvo.separat.model.MessageUpdateDto;
import kvo.separat.repository.mysql.MessageRepository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger logger = LogManager.getLogger(MessageController.class);
    @Value("${config.path:.\\config\\config.txt}")
    private String configPath; // Поле для хранения пути к конфигурации

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
            if (message.isPresent()) {
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
            @RequestBody MessageUpdateDto request) throws IOException {
//        String configPath = currentDir + "\\src\\main\\java\\kvo\\separat\\config\\setting.txt"; //TODO для ПРОДА скорректировать путь на \\config\\setting.txt
//        String configPath = "C:\\Users\\KvochkinAY\\IdeaProjects\\Spring\\Project\\DVMessageSend\\src\\main\\java\\kvo\\separat\\config\\setting.txt";
        System.out.println("configPath = "+ configPath);
        ConfigLoader configLoader = new ConfigLoader(configPath);
        String URL = configLoader.getProperty("URL_MSSQL");
        String USER = configLoader.getProperty("USER_MSSQL");
        String PASSWORD = configLoader.getProperty("PASSWORD_MSSQL");
        // найти UUID в сообщении
        String uuid = extractUuidFromMessage(id);
        if (uuid == null) {
            logger.error("Сообщение с id {} не найдено", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("UUID: {}", uuid);
        return getVoidResponseEntity(id, request, URL, USER, PASSWORD, uuid);
    }

    private ResponseEntity<Void> getVoidResponseEntity(Long id, MessageUpdateDto request, String URL, String USER, String PASSWORD, String uuid) {
        try (Connection connectionMSSQL = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 1. Обновляем temp_message в БД MSSQL
            connectionMSSQL.setAutoCommit(false);
            String updateSQL = "UPDATE [dbo].[temp_message] SET [status] = 'new' WHERE CAST([uuid] AS NVARCHAR(MAX)) = ?";
            try (PreparedStatement pstmt = connectionMSSQL.prepareStatement(updateSQL)) {
                pstmt.setString(1, uuid);
                int updatedRows = pstmt.executeUpdate();

                if (updatedRows == 0) {
                    connectionMSSQL.rollback();
                    logger.info("Не найдено записей для обновления с UUID: {}", uuid);
//                    return ResponseEntity.notFound().build(); //продолжаем отправку данных без Binary
                }
                // 2. Если обновление прошло успешно - коммитим
                connectionMSSQL.commit();
                logger.info("MSSQL: успешно обновлено {} строк.", updatedRows);

                // 3. Обновляем репозиторий (после коммита!) БД MySQL
                messageRepository.updateMessage(
                        id,
                        request.getStatus(),
                        request.getDateEnd(),
                        request.getServer(),
                        request.getNumAttempt()
                );
                logger.info("Repository: статус сообщения {} обновлен, кол-во вложенных файлов {}", uuid, updatedRows);
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    public String extractUuidFromMessage(Long id) {
        Optional<Message> message = messageRepository.findMessageById(id);
        if (message.isPresent()) {
            String messageJson = message.get().getMessage();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(messageJson);
                return jsonNode.get("uuid").asText();
            } catch (IOException | NullPointerException e) {
                logger.error("Ошибка при извлечении UUID из сообщения с id: {}", id, e);
                return null;
            }
        }
        return null;
    }
}