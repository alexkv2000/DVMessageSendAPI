package kvo.separat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageMSSQLRepository extends JpaRepository<MessageBinaryMSSQL, Long> {
    @Query("SELECT m FROM MessageBinaryMSSQL m WHERE m.uuid = :uuid")
    List<MessageBinaryMSSQL> findByUuid(@Param("uuid") UUID uuid);
    // Остальные методы остаются без изменений
}