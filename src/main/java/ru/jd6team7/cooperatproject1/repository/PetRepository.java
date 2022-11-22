package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.Pet;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    /** Показать всех питомцев с определённым статусом */
    List<Pet> getAllByCurrentStateLike(String petState);
    /** Показать всех маленьких питомцев */
    List<Pet> getAllByAgeBefore(Integer petAge);
    /** Показать всех взрослых питомцев */
    List<Pet> getAllByAgeAfter(Integer petAge);
    /** Показать всех маленьких питомцев, которые в Приюте и свободны для Посетителя */
    @Query(value = "SELECT * FROM pet WHERE current_state = 'в приюте' AND age < 2", nativeQuery = true)
    List<Pet> getKidsPetsForVisitor();
    /** Показать всех взрослых питомцев, которые в Приюте и свободны для Посетителя */
    @Query(value = "SELECT * FROM pet WHERE current_state = 'в приюте' AND age >= 2", nativeQuery = true)
    List<Pet> getAdultPetsForVisitor();
    /** Найти питомца по его ИД номеру */
    Optional<Pet> getPetByPetID(Long petID);
}
