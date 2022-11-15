package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.Visitor;

import java.util.Optional;


@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByChatId(long chatId);
    @Modifying
    @Query("update Visitor set messageStatus = ?2 where chatId = ?1")
    void updateMessageStatus(long chatId, Visitor.MessageStatus messageStatus);
}
