package ru.jd6team7.cooperatproject1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Cat;
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.repository.CatRepository;
import ru.jd6team7.cooperatproject1.repository.DogRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * Класс, описывающий логику работы с сущностью Питомец (Dog)
 * Через интерфейс репозитория {@link DogRepository} осуществляются
 * основные манипуляции с данными по Питомцам. Запись, чтение, удаление,
 * вывод списков Питомцев по критериям запросов
 */
@Service
public class PetService {
    /** Поле хранит значение общей папке с файлами фото питомцев */
    @Value("${pets.photos.dir.path}")
    private String picturePath;
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private final DogRepository petRepo;
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private final CatRepository petRepo2;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger logger = LoggerFactory.getLogger("ru.telbot.file");

    public PetService(DogRepository petRepo, CatRepository petRepo2) {
        this.petRepo = petRepo;
        this.petRepo2 = petRepo2;
    }

    /**
     * Метод возвращает расширение файла, полученного через входящее значение переменной
     * @param inpPath
     * @return <i><b>пример:</b></i> jpg, bmp, png
     */
    private String getExtensionOfFile(String inpPath){
        if (inpPath.contains(".")){
            return inpPath.substring(inpPath.lastIndexOf("."), inpPath.length());
        } else {
            return "";
        }
    }

    /**
     * Метод добавления записи о Питомце в БД
     * @param inpDog
     * @return Возвращает созданный экземпляр Питомца
     */
    public Dog addPet(Dog inpDog) {
        logger.debug("Вызван метод addPet с inpDog = " + inpDog.getPetName());
        return petRepo.save(inpDog);
    }

    /**
     * Метод чтения записи о Питомце из БД. Поиск по идентификатору Питомца
     * @param inpId
     * @return Возвращает найденный экземпляр Питомца, либо Пустоту
     */
    public Dog findPet(long inpId) {
        logger.debug("Вызван метод findPet с inpId = " + inpId);
        return petRepo.getPetByPetID(inpId).orElse(null);
    }

    /**
     * Метод перезаписи данных о Питомце
     * @param inpUpdatedDog
     * @return Возвращает перезаписанную информацию о Питомце
     */
    public Dog updatePet(Dog inpUpdatedDog) {
        Dog updatedDog = petRepo.getPetByPetID(inpUpdatedDog.getPetID()).orElse(null);
        if (updatedDog != null){
            updatedDog.setPetName(inpUpdatedDog.getPetName());
            updatedDog.setAge(inpUpdatedDog.getAge());
            updatedDog.setAnimalGender(inpUpdatedDog.getAnimalGender());
            updatedDog.setCurrentState(inpUpdatedDog.getCurrentState());
            updatedDog.setPathFileToPhoto(inpUpdatedDog.getPathFileToPhoto());
            logger.debug("Вызван метод updatePet с inpUpdatedDog.getPetid = " + inpUpdatedDog.getPetID());
            return petRepo.save(updatedDog);
        } else {
            return null;
        }
    }

    /**
     * Метод удаления данных о Питомце. Поиск по идентификатору
     * @param inpId
     * @return
     */
    public Boolean deletePet(long inpId) {
        logger.debug("Удаление данных о Питомце с ID = " + inpId);
        petRepo.deleteById(inpId);
        if (findPet(inpId)==null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Метод возвращает список питомцев-малышей, которые могут быть доступны Посетителю для общения
     * @return Список объектов класса Питомец, может быть возвращена Пустота
     */
    public List<Dog> getKidsPetsForVisitor() {
        logger.debug("Вызван метод getKidsPetsForVisitor");
        return petRepo.getKidsPetsForVisitor();
    }

    /**
     * Метод возвращает список взрослых питомцев, которые могут быть доступны Посетителю для общения
     * @return Список объектов класса Питомец, может быть возвращена Пустота
     */
    public List<Dog> getAdultPetsForVisitor() {
        logger.debug("Вызван метод getAdultPetsForVisitor");
        return petRepo.getAdultPetsForVisitor();
    }

    /**
     * Метод возвращает весь список питомцев-малышей. Питомцы младше возраста
     * @param inpAge
     * @return Список объектов класса Питомец, может быть возвращена Пустота
     */
    public List<Dog> getAllKidsPets(Integer inpAge) {
        logger.debug("Вызван метод getAllKidsPets с параметром inpAge = " + inpAge);
        return petRepo.getAllByAgeBefore(inpAge);
    }

    /**
     * Метод возвращает весь список взрослых питомцев. Питомцы старше возраста
     * @param inpAge
     * @return Список объектов класса Питомец, может быть возвращена Пустота
     */
    public List<Dog> getAllAdultPets(Integer inpAge) {
        logger.debug("Вызван метод getAllAdultPets с параметром inpAge = " + inpAge);
        return petRepo.getAllByAgeAfter(inpAge);
    }

    /**
     * Метод возвращает всех Питомцев, имеющих выбранный статус (в приюте, у посетителя, у усыновителя, отсутствует, усыновлён)
     * @param inpState
     * @return  Список объектов класса Питомец, может быть возвращена Пустота
     */
    public List<Dog> getAllPetsWithState(String inpState) {
        logger.debug("Вызван метод getAllPetsWithState с параметром inpState = " + inpState);
        return petRepo.getAllByCurrentStateLike(inpState);
    }

    /**
     * Метод добавляет картинку Питомца во внутренную папку для фото.
     * Прописывает путь к файлу в качестве Поля сущности Питомец
     * для вывода при запросе
     * @param petID
     * @param inpPicture
     * @throws IOException
     * @return
     */
    public void addPetPhoto(Long petID, MultipartFile inpPicture) throws IOException {
        Dog curDog = petRepo.getPetByPetID(petID).orElse(null);
        if (curDog == null) {
            throw new PetNotFoundException("К сожалению Питомца с идентификатором (id = " + petID + ") Нет!");
        }
        logger.debug("Вызван метод addPetPhoto");
        Path imagePath = Path.of(picturePath + '/' + curDog.getPetName() + curDog.getPetID() + getExtensionOfFile(inpPicture.getOriginalFilename()));
        Files.createDirectories(imagePath.getParent());
        Files.deleteIfExists(imagePath);
        // Создание потоков и вызов метода передачи данных по 1-му килобайту
        logger.debug("Формирование стрима для вывод в файл");
        try (InputStream inpStream = inpPicture.getInputStream();
             OutputStream outStream = Files.newOutputStream(imagePath, CREATE_NEW);
             BufferedInputStream bufInpStream = new BufferedInputStream(inpStream, 1024);
             BufferedOutputStream bufOutStream = new BufferedOutputStream(outStream, 1024);
        ) {
            bufInpStream.transferTo(bufOutStream);
        }
        curDog.setPathFileToPhoto(imagePath.toFile().getPath());
        logger.debug("Сохранение пути к Фото Питомца в репозиторий");
        petRepo.save(curDog);
    }

    /**
     * Метод установки статуса Питомца путём выбора из перечисляемого типа
     * @param inpPetState
     * @return Возвращает изменённый экземпляр Питомца
     */
    public Dog putDogState(long petID, PetState inpPetState) {
        Dog curDog = petRepo.getPetByPetID(petID).orElse(null);
        if (curDog == null) {
            throw new PetNotFoundException("К сожалению Питомца с идентификатором (id = " + petID + ") Нет!");
        }
        logger.debug("Вызван метод putDogState с inpPetState = " + inpPetState.name() + " для Питомца с petID = " + petID);
        curDog.setCurrentState(inpPetState.name());
        return petRepo.save(curDog);
    }
    /**
     * Метод установки статуса Питомца путём выбора из перечисляемого типа
     * @param inpPetState
     * @return Возвращает изменённый экземпляр Питомца
     */
    public Cat putCatState(long petID, PetState inpPetState) {
        Cat curCat = petRepo2.getPetByPetID(petID).orElse(null);
        if (curCat == null) {
            throw new PetNotFoundException("К сожалению Питомца с идентификатором (id = " + petID + ") Нет!");
        }
        logger.debug("Вызван метод putCatState с inpPetState = " + inpPetState.name() + " для Питомца с petID = " + petID);
        curCat.setCurrentState(inpPetState.name());
        return petRepo2.save(curCat);
    }

}
