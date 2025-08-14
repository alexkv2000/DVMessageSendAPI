package kvo.separat;

import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface MessageService {
    List<Message> getMessagesByPeriodAndStatus(Timestamp start, Timestamp end, String status);
    Message getMessageById(Long id);
    Message updateMessage(Long id, Message message);

    @Transactional(readOnly = true)
    List<Message> getMessagesByDateRangeAndStatus(
            Timestamp startDate, Timestamp endDate, String status);

    @Transactional
    void updateStatus(Long id, String status);

    @Transactional
    void updateMessageFields(
            Long id,
            String status,
            Timestamp dateEnd,
            String server,
            Integer numAttempt);
}
