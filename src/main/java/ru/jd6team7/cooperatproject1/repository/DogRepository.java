package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.Dog;

import java.util.List;
import java.util.Optional;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
    /** Показать всех питомцев с определённым статусом */
    List<Dog> getAllByCurrentStateLike(String petState);
    /** Показать всех маленьких питомцев */
    List<Dog> getAllByAgeBefore(Integer petAge);
    /** Показать всех взрослых питомцев */
    List<Dog> getAllByAgeAfter(Integer petAge);
    /** Показать всех маленьких питомцев, которые в Приюте и свободны для Посетителя */
    @Query(value = "SELECT * FROM dog WHERE current_state = 'AT_SHELTER' AND age < 2", nativeQuery = true)
    List<Dog> getKidsPetsForVisitor();
    /** Показать всех взрослых питомцев, которые в Приюте и свободны для Посетителя */
    @Query(value = "SELECT * FROM dog WHERE current_state = 'AT_SHELTER' AND age >= 2", nativeQuery = true)
    List<Dog> getAdultPetsForVisitor();
    /** Найти питомца по его ИД номеру */
    Optional<Dog> getPetByPetID(Long petID);
}
