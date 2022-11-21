package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.Shelter;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

}
