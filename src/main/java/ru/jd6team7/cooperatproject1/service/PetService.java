package ru.jd6team7.cooperatproject1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.repository.PetRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * Класс, описывающий логику работы с сущностью Питомец (Pet)
 * Через интерфейс репозитория {@link PetRepository} осуществляются
 * основные манипуляции с данными по Питомцам. Запись, чтение, удаление,
 * вывод списков Питомцев по критериям запросов
 */
@Service
public class PetService {
    /** Поле хранит значение общей папке с файлами фото питомцев */
    @Value("${pets.photos.dir.path}")
    private String picturePath;
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private final PetRepository petRepo;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger logger = LoggerFactory.getLogger("ru.telbot.file");
    /** Объект перечисляемого словаря статуса Питомца */
    private EnumMap<PetState, String> enumMap;

    public PetService(PetRepository petRepo) {
        this.petRepo = petRepo;
        enumMap = new EnumMap<PetState, String>(PetState.class);
        enumMap.put(PetState.AT_SHELTER, "в приюте");
        enumMap.put(PetState.WITH_VISITOR, "у посетителя");
        enumMap.put(PetState.WITH_GUARDIAN, "у опекуна");
        enumMap.put(PetState.STAY_OUT, "отсутствует");
        enumMap.put(PetState.ADOPTED, "усыновлён");
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
     * @param inpPet
     * @return Возвращает созданный экземпляр Питомца
     */
    public Pet addPet(Pet inpPet) {
        logger.debug("Вызван метод addPet с inpPet = " + inpPet.getPetName());
        return petRepo.save(inpPet);
    }

    /**
     * Метод чтения записи о Питомце из БД. Поиск по идентификатору Питомца
     * @param inpId
     * @return Возвращает найденный экземпляр Питомца, либо Пустоту
     */
    public Pet findPet(long inpId) {
        logger.debug("Вызван метод findPet с inpId = " + inpId);
        return petRepo.findById(inpId).orElse(null);
    }

    /**
     * Метод перезаписи данных о Питомце
     * @param inpUpdatedPet
     * @return Возвращает перезаписанную информацию о Питомце
     */
    public Pet updatePet(Pet inpUpdatedPet) {
        Pet updatedFaculty = findPet(inpUpdatedPet.getId());
        updatedFaculty.setPetName(inpUpdatedPet.getPetName());
        updatedFaculty.setAge(inpUpdatedPet.getAge());
        logger.debug("Вызван метод updatePet с inpUpdatedPet.getPetid = " + inpUpdatedPet.getId());
        return petRepo.save(inpUpdatedPet);
    }

    /**
     * Метод удаления данных о Питомце. Поиск по идентификатору
     * @param inpId
     */
    public void deletePet(long inpId) {
        logger.debug("Удаление данных о Питомце с ID = " + inpId);
        petRepo.deleteById(inpId);
    }

    /**
     * Метод возвращает список питомцев-малышей, которые могут быть доступны Посетителю для общения
     * @return
     */
    public List<Pet> getKidsPetsForVisitor() {
        logger.debug("Вызван метод getKidsPetsForVisitor");
        return petRepo.getKidsPetsForVisitor();
    }

    /**
     * Метод возвращает список взрослых питомцев, которые могут быть доступны Посетителю для общения
     * @return
     */
    public List<Pet> getAdultPetsForVisitor() {
        logger.debug("Вызван метод getAdultPetsForVisitor");
        return petRepo.getAdultPetsForVisitor();
    }

    /**
     * Метод возвращает весь список питомцев-малышей. Питомцы младше возраста
     * @param inpAge
     * @return
     */
    public List<Pet> getAllKidsPets(Integer inpAge) {
        logger.debug("Вызван метод getAllKidsPets с параметром inpAge = " + inpAge);
        return petRepo.getAllByAgeBefore(inpAge);
    }

    /**
     * Метод возвращает весь список взрослых питомцев. Питомцы старше возраста
     * @param inpAge
     * @return
     */
    public List<Pet> getAllAdultPets(Integer inpAge) {
        logger.debug("Вызван метод getAllAdultPets с параметром inpAge = " + inpAge);
        return petRepo.getAllByAgeAfter(inpAge);
    }

    /**
     * Метод возвращает всех Питомцев, имеющих выбранное состояние(в приюте, у посетителя, у усыновителя, отсутствует, усыновлён)
     * @param inpState
     * @return Список Питомцев
     */
    public List<Pet> getAllPetsWithState(String inpState) {
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
     */
    public void addPetPhoto(Long petID, MultipartFile inpPicture) throws IOException {
        Pet curPet = petRepo.findById(petID).orElse(null);
        if (curPet == null) {
            throw new PetNotFoundException("К сожалению Питомца с идентификатором (id = " + petID + ") Нет!");
        }
        logger.debug("Вызван метод addPetPhoto");
        Path imagePath = Path.of(picturePath + '/' + curPet.getPetName() + curPet.getId() + getExtensionOfFile(inpPicture.getOriginalFilename()));
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
        curPet.setPathFileToPhoto(imagePath.toFile().getPath());
        logger.debug("Сохранение пути к Фото Питомца в репозиторий");
        petRepo.save(curPet);
    }

    /**
     * Метод установки статуса Питомца путём выбора из перечисляемого типа мапы
     * @param inpPetState
     * @return Возвращает изменённый экземпляр Питомца
     */
    public Pet putPetState(long petID, PetState inpPetState) {
        Pet curPet = petRepo.findById(petID).orElse(null);
        if (curPet == null) {
            throw new PetNotFoundException("К сожалению Питомца с идентификатором (id = " + petID + ") Нет!");
        }
        logger.debug("Вызван метод putPetState с inpPetState = " + inpPetState.name() + " для Питомца с petID = " + petID);
        curPet.setCurrentState(enumMap.get(inpPetState));
        return petRepo.save(curPet);
    }
}
