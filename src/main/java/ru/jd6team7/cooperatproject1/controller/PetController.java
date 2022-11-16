package ru.jd6team7.cooperatproject1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Вывод данных о Питомце по его ИД номеру",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Питомца с ИД номером не найдено"
                    )}, tags = "Pet"
    )
    @GetMapping("{petID}")
    public ResponseEntity<Pet> getPet(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID) {
        Pet resultEntity = petService.findPet(petID);
        if (resultEntity != null) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Получение Фото Питомца по его ИД номеру из Файла на диске",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.IMAGE_JPEG_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Питомца с ИД номером не найдено"
                    )}, tags = "Pet"
    )
    @GetMapping("/{petID}/photoFromFileStore")
    public void getPictureOfStudentFromFileStore(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID, HttpServletResponse response) throws IOException {
        Pet resultEntity = petService.findPet(petID);
        if (resultEntity == null) {throw new PetNotFoundException("Питомец не найден");
        }
        Path imagePath = Path.of(resultEntity.getPathFileToPhoto());

        try (InputStream inpStream = Files.newInputStream(imagePath);
             OutputStream outStream = response.getOutputStream();
        ){
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//            response.setContentLength((int) resultEntity.getFileSize());
            inpStream.transferTo(outStream);
            response.setStatus(201);
        }
    }

    @Operation(
            summary = "Ввод данных о Питомце",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )},
            tags = "Pet"
    )
    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet inpPet) {
        Pet resultEntity = petService.addPet(inpPet);
        return ResponseEntity.ok(resultEntity);
    }

    @Operation(
            summary = "Добавление Фото Питомца на диск + путь к данному файлу в БД в таблицу сущности Pet",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.IMAGE_JPEG_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            ),
            /*responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )},*/
            tags = "Pet"
    )
    @PostMapping(path = "{petID}/setPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPetPicture(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID, @RequestBody MultipartFile inpPicture
    ) throws IOException {
        //FileTreatmentException("Something bad has happend via treatment of uploading file")
        if (inpPicture.getSize() > 1024 * 1024 * 10) {
            return ResponseEntity.badRequest().body("File great than 10 Mb!");
        }
        petService.addPetPhoto(petID, inpPicture);
        return ResponseEntity.ok().body("File Photo was uploaded successfully");
    }
    @Operation(
            summary = "Обновление данных о Питомце",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )},
            tags = "Pet"
    )
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
    @Operation(
            summary = "Удаление данных о Питомце по его ИД номеру",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные удалены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )}, tags = "Pet"
    )
    @DeleteMapping
    public ResponseEntity<?> deletePet(@Parameter(description = "ИД номер Питомца") @RequestParam long petID) {
        petService.deletePet(petID);
        return ResponseEntity.ok().build();
    }

}
