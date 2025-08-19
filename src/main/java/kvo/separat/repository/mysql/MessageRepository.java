package kvo.separat.repository.mysql;

import kvo.separat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.dateCreate BETWEEN :startDate AND :endDate AND m.status = :status")
    List<Message> findByDateCreateBetweenAndStatus(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("status") String status);
    @Query("SELECT m FROM Message m WHERE m.dateCreate BETWEEN :startDate AND :endDate")
    List<Message> findByDateCreateBetween(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);
    @Override
    Optional<Message> findById(Long id);
//    Optional<Message> findUUIDById(Long id);
    Optional<Message> findMessageById(Long id);
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.status = :status, m.dateEnd = :dateEnd, m.server = :server, m.numAttempt = :numAttempt WHERE m.id = :id")
    void updateMessage(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("dateEnd") Timestamp dateEnd,
            @Param("server") String server,
            @Param("numAttempt") Integer numAttempt);
}
