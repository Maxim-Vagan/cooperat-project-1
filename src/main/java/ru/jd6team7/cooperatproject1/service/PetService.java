package ru.jd6team7.cooperatproject1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.repository.PetRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class PetService {
    @Value("${pets.photos.dir.path}")
    private String picturePath;

    private final PetRepository petRepo;
    private final Logger logger = LoggerFactory.getLogger("ru.telbot.file");

    public PetService(PetRepository petRepo) {
        this.petRepo = petRepo;
    }

    private String getExtensionOfFile(String inpPath){
        if (inpPath.contains(".")){
            return inpPath.substring(inpPath.lastIndexOf("."), inpPath.length());
        } else {
            return "";
        }
    }

    // Create

    public Pet addPet(Pet inpPet) {
        logger.debug("Вызван метод addPet с inpPet = " + inpPet.getPetName());
        return petRepo.save(inpPet);
    }
    // Read

    public Pet findPet(long inpId) {
        logger.debug("Вызван метод findPet с inpId = " + inpId);
        return petRepo.findById(inpId).orElse(null);
    }
    // Update

    public Pet updatePet(Pet inpUpdatedPet) {
        Pet updatedFaculty = findPet(inpUpdatedPet.getId());
        updatedFaculty.setPetName(inpUpdatedPet.getPetName());
        updatedFaculty.setAge(inpUpdatedPet.getAge());
        logger.debug("Вызван метод updatePet с inpUpdatedPet.getPetid = " + inpUpdatedPet.getId());
        return petRepo.save(inpUpdatedPet);
    }
    // Delete

    public void deletePet(long inpId) {
        logger.debug("Удаление данных о Питомце с ID = " + inpId);
        petRepo.deleteById(inpId);
    }
    // Read

    public List<Pet> getKidsPetsForVisitor() {
        logger.debug("Вызван метод getKidsPetsForVisitor");
        return petRepo.getKidsPetsForVisitor();
    }
    // Read

    public List<Pet> getAdultPetsForVisitor() {
        logger.debug("Вызван метод getAdultPetsForVisitor");
        return petRepo.getAdultPetsForVisitor();
    }
    // Read

    public List<Pet> getAllKidsPets(Integer inpAge) {
        logger.debug("Вызван метод getAllKidsPets с параметром inpAge = " + inpAge);
        return petRepo.getAllByAgeBefore(inpAge);
    }
    // Read

    public List<Pet> getAllAdultPets(Integer inpAge) {
        logger.debug("Вызван метод getAllAdultPets с параметром inpAge = " + inpAge);
        return petRepo.getAllByAgeAfter(inpAge);
    }
    // Read

    public List<Pet> getAllPetsWithState(String inpState) {
        logger.debug("Вызван метод getAllPetsWithState с параметром inpState = " + inpState);
        return petRepo.getAllByCurrentStateLike(inpState);
    }

    public void addPetPhoto(Long petID, MultipartFile inpPicture) throws IOException {
        Pet curPet = petRepo.findById(petID).orElse(null);
        if (curPet == null) {
            throw new PetNotFoundException("К сожалению Питомца с идентификатором (id = " + petID + ") Нет!");
        }
        logger.debug("Вызван метод addPetPhoto");
        Path imagePath = Path.of(picturePath, curPet.getPetName() + curPet.getId() + getExtensionOfFile(inpPicture.getOriginalFilename()));
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
        curPet.setPathFileToPhoto(imagePath.getFileName().toString());
        logger.debug("Сохранение пути к Фото Питомца в репозиторий");
        petRepo.save(curPet);
    }
}
