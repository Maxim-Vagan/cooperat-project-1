package ru.jd6team7.cooperatproject1.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.service.PetService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping(path = "/pet")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("{petID}")
    public ResponseEntity<Pet> getPet(@PathVariable Long petID) {
        Pet resultEntity = petService.findPet(petID);
        if (resultEntity != null) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Получение Фото Питомца из Файла на диске
    @GetMapping("/{petID}/photoFromFileStore")
    public void getPictureOfStudentFromFileStore(@PathVariable Long petID, HttpServletResponse response) throws IOException {
        Pet resultEntity = petService.findPet(petID);
        if (resultEntity == null) {throw new PetNotFoundException("Питомец не найден");
        }
        Path imagePath = Path.of(resultEntity.getPathFileToPhoto());

        try (InputStream inpStream = Files.newInputStream(imagePath);
             OutputStream outStream = response.getOutputStream();
        ){
//            response.setContentType(resultEntity.getMediaType());
//            response.setContentLength((int) resultEntity.getFileSize());
            inpStream.transferTo(outStream);
            response.setStatus(201);
        }
    }

    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet inpPet) {
        Pet resultEntity = petService.addPet(inpPet);
        return ResponseEntity.ok(resultEntity);
    }

    // Добавление Фото Питомца на диск + путь к данному файлу в БД в таблицу сущности Pet
    @PostMapping(path = "{petID}/setPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPetPicture(@PathVariable Long petID, @RequestBody MultipartFile inpPicture
    ) throws IOException {
        //FileTreatmentException("Something bad has happend via treatment of uploading file")
        if (inpPicture.getSize() > 1024 * 1024 * 10) {
            return ResponseEntity.badRequest().body("File great than 10 Mb!");
        }
        petService.addPetPhoto(petID, inpPicture);
        return ResponseEntity.ok().body("File Photo was uploaded successfully");
    }
    @PutMapping
    public ResponseEntity<Pet> updatePet(@RequestBody Pet inpPet) {
        Pet resultEntity = petService.findPet(inpPet.getId());
        if (resultEntity != null) {
            petService.updatePet(inpPet);
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletePet(@RequestParam long studentID) {
        petService.deletePet(studentID);
        return ResponseEntity.ok().build();
    }
}
