package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jd6team7.cooperatproject1.model.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
}
