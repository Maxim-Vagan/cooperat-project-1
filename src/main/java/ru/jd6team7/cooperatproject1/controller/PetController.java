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
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.service.DogService;

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
    private final DogService petService;

    public PetController(DogService petService) {
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Питомца с ИД номером не найдено"
                    )}, tags = "Dog"
    )
    @GetMapping("{petID}")
    public ResponseEntity<Dog> getPet(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID) {
        Dog resultEntity = petService.findPet(petID);
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
                                    schema = @Schema(implementation = Dog.class)
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
            }, tags = "Dog"
    )
    @GetMapping("/{petID}/photoFromFileStore")
    public ResponseEntity<String> getPictureOfPetFromFileStore(@Parameter(description = "ИД номер Питомца") @PathVariable Long petID, HttpServletResponse response) throws IOException {
        Dog resultEntity = petService.findPet(petID);
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
                            schema = @Schema(implementation = Dog.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Dog.class)
                            )
                    )},
            tags = "Dog"
    )
    @PostMapping
    public ResponseEntity<Dog> createPet(@RequestBody Dog inpDog) {
        Dog resultEntity = petService.addPet(inpDog);
        return ResponseEntity.ok(resultEntity);
    }

    @Operation(
            summary = "Добавление Фото Питомца на диск + путь к данному файлу в БД в таблицу сущности Dog",
            /*requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.IMAGE_JPEG_VALUE,
                            schema = @Schema(implementation = Dog.class),
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
                                    schema = @Schema(implementation = Dog.class)
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
            tags = "Dog"
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
                            schema = @Schema(implementation = Dog.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    )},
            tags = "Dog"
    )
    @PutMapping
    public ResponseEntity<Dog> updatePet(@RequestBody Dog inpDog) {
        Dog resultEntity = petService.updatePet(inpDog);
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    )}, tags = "Dog"
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
            /*requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Dog.class)
                    )
            ),*/
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Питомца с ИД номером не найдено"
                    )},
            tags = "Dog"
    )
    @PutMapping("/setPetState")
    public ResponseEntity<Dog> updatePetState(@Parameter(description = "ИД номер Питомца") @RequestParam Long petID,
                                              @Parameter(description = "статус Питомца") @RequestParam PetState inpState) {
        Dog resultEntity = petService.findPet(petID);
        if (resultEntity != null) {
            petService.putDogState(petID, inpState);
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Dog.class)
                            )
                    )}, tags = "Dog"
    )
    @GetMapping("/showAllKidsPet")
    public ResponseEntity<List<Dog>> getAllKidsPet() {
        List<Dog> resultEntity = petService.getAllKidsPets(2);
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Dog.class)
                            )
                    )}, tags = "Dog"
    )
    @GetMapping("/showAllAdultPets")
    public ResponseEntity<List<Dog>> getAllAdultPets() {
        List<Dog> resultEntity = petService.getAllAdultPets(1);
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Dog.class)
                            )
                    )}, tags = "Dog"
    )
    @GetMapping("/showAdultPetsForVisitor")
    public ResponseEntity<List<Dog>> getAdultPetsForVisitor() {
        List<Dog> resultEntity = petService.getAdultPetsForVisitor();
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список пуст",
                            content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = Dog.class)
                            )
                    )}, tags = "Dog"
    )
    @GetMapping("/showKidsPetsForVisitor")
    public ResponseEntity<List<Dog>> getKidsPetsForVisitor() {
        List<Dog> resultEntity = petService.getKidsPetsForVisitor();
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
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список пуст",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Dog.class)
                            )
                    )}, tags = "Dog"
    )
    @GetMapping("/showAllPetsWithState")
    public ResponseEntity<List<Dog>> showAllPetsWithState(@Parameter(description = "Статус Питомца") @RequestParam PetState inpState) {
        List<Dog> resultEntity = petService.getAllPetsWithState(inpState.name());
        if (resultEntity.size() > 0) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
