package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.Cat;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {
    /** Показать всех питомцев с определённым статусом */
    List<Cat> getAllByCurrentStateLike(String petState);
    /** Показать всех маленьких питомцев */
    List<Cat> getAllByAgeBefore(Integer petAge);
    /** Показать всех взрослых питомцев */
    List<Cat> getAllByAgeAfter(Integer petAge);
    /** Показать всех маленьких питомцев, которые в Приюте и свободны для Посетителя */
    @Query(value = "SELECT * FROM cat WHERE current_state = 'AT_SHELTER' AND age < 2", nativeQuery = true)
    List<Cat> getKidsPetsForVisitor();
    /** Показать всех взрослых питомцев, которые в Приюте и свободны для Посетителя */
    @Query(value = "SELECT * FROM cat WHERE current_state = 'AT_SHELTER' AND age >= 2", nativeQuery = true)
    List<Cat> getAdultPetsForVisitor();
    /** Найти питомца по его ИД номеру */
    Optional<Cat> getPetByPetID(Long petID);
}
