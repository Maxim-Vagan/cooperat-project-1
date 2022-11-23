package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;

import java.util.Optional;

@Repository
@Transactional
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByChatId(long chatId);
    @Modifying
    @Query("update Visitor set messageStatus = ?2 where chatId = ?1")
    void updateMessageStatus(long chatId, Visitor.MessageStatus messageStatus);

    @Modifying
    @Query("update Visitor set shelterStatus = ?2 where chatId = ?1")
    void updateShelterStatus(long chatId, Visitor.ShelterStatus shelterStatus);
}
