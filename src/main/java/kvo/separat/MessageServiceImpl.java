package kvo.separat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public List<Message> getMessagesByPeriodAndStatus(Timestamp start, Timestamp end, String status) {
        return null;
    }

    @Override
    public Message getMessageById(Long id) {
        return null;
    }

    @Override
    public Message updateMessage(Long id, Message message) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByDateRangeAndStatus(
            Timestamp startDate, Timestamp endDate, String status) {
        return messageRepository
                .findByDateCreateBetweenAndStatus(startDate, endDate, status)
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, String status) {

    }


    @Override
    @Transactional
    public void updateMessageFields(
            Long id,
            String status,
            Timestamp dateEnd,
            String server,
            Integer numAttempt) {
        messageRepository.updateMessage(id, status, dateEnd, server, numAttempt);
    }


}
