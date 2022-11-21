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
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.service.PetService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
            summary = "Вывод фото Питомца по его ИД номеру из Файла на диске",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "фото выведено",
                            content = @Content(
                                    mediaType = MediaType.IMAGE_JPEG_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Питомца с ИД номером не найдено, либо нет его Фото",
                            content = @Content(
                                    mediaType = MediaType.TEXT_HTML_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка Веб приложения",
                            content = @Content(
                                    mediaType = MediaType.TEXT_HTML_VALUE
                            )
                    )
            }, tags = "Pet"
    )
    @GetMapping("/{petID}/photoFromFileStore")
    public ResponseEntity<String> getPictureOfPetFromFileStore(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID, HttpServletResponse response) throws IOException {
        Pet resultEntity = petService.findPet(petID);
        if (resultEntity == null) {
            throw new PetNotFoundException("Питомец не найден");
        } else if (resultEntity.getPathFileToPhoto() == null){
            return ResponseEntity.notFound().build();
        } else {
            Path imagePath = Path.of(resultEntity.getPathFileToPhoto());
            try (InputStream inpStream = Files.newInputStream(imagePath);
                 OutputStream outStream = response.getOutputStream();
            ){
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//            response.setContentLength((int) resultEntity.getFileSize());
                inpStream.transferTo(outStream);
                return ResponseEntity.ok().body("фото выведено");
            }
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
                                    schema = @Schema(implementation = Pet.class)
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
            /*requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.IMAGE_JPEG_VALUE,
                            schema = @Schema(implementation = Pet.class),
                            extensions = {@Extension(name = "*.jpeg", properties = {}),
                                    @Extension(name = "*.jpg", properties = {})
                            }
                    )
            ),*/
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Фото сохранено!",
                            content = @Content(
                                    mediaType = MediaType.TEXT_HTML_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "415",
                            description = "Неподдерживаемый формат данных!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    )
            },
            tags = "Pet"
    )
    @PostMapping(path = "{petID}/setPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPetPicture(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID,
                                                   @Parameter(description = "Путь к файлу") @RequestBody MultipartFile inpPicture
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
        Pet resultEntity = petService.updatePet(inpPet);
        if (resultEntity != null) {
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
    public ResponseEntity<Boolean> deletePet(@Parameter(description = "ИД номер Питомца") @RequestParam long petID) {
        if (petService.deletePet(petID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Обновление статуса Питомца",
/*            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            ),*/
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Питомца с ИД номером не найдено"
                    )},
            tags = "Pet"
    )
    @PutMapping("/setPetState")
    public ResponseEntity<Pet> updatePetState(@Parameter(description = "ИД номер Питомца") @RequestParam Long petID,
                                              @Parameter(description = "статус Питомца") @RequestParam PetState inpState) {
        Pet resultEntity = petService.findPet(petID);
        if (resultEntity != null) {
            petService.putPetState(petID, inpState);
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(
            summary = "Вывод списка маленьких Питомцев (малыши)",
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
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )}, tags = "Pet"
    )
    @GetMapping("/showAllKidsPet")
    public ResponseEntity<List<Pet>> getAllKidsPet() {
        List<Pet> resultEntity = petService.getAllKidsPets(2);
        if (resultEntity.size() > 0) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывод списка взрослых Питомцев",
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
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )}, tags = "Pet"
    )
    @GetMapping("/showAllAdultPets")
    public ResponseEntity<List<Pet>> getAllAdultPets() {
        List<Pet> resultEntity = petService.getAllAdultPets(1);
        if (resultEntity.size() > 0) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывод списка взрослых Питомцев для общения с Посетителем",
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
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )}, tags = "Pet"
    )
    @GetMapping("/showAdultPetsForVisitor")
    public ResponseEntity<List<Pet>> getAdultPetsForVisitor() {
        List<Pet> resultEntity = petService.getAdultPetsForVisitor();
        if (resultEntity.size() > 0) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывод списка маленьких Питомцев для общения с Посетителем",
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
                            description = "Список пуст",
                            content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = Pet.class)
                            )
                    )}, tags = "Pet"
    )
    @GetMapping("/showKidsPetsForVisitor")
    public ResponseEntity<List<Pet>> getKidsPetsForVisitor() {
        List<Pet> resultEntity = petService.getKidsPetsForVisitor();
        if (resultEntity.size() > 0) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывод всех Питомцев с определённым статусом",
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
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )}, tags = "Pet"
    )
    @GetMapping("/showAllPetsWithState")
    public ResponseEntity<List<Pet>> showAllPetsWithState(@Parameter(description = "Статус Питомца") @RequestParam PetState inpState) {
        List<Pet> resultEntity = petService.getAllPetsWithState(inpState.getCode());
        if (resultEntity.size() > 0) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
