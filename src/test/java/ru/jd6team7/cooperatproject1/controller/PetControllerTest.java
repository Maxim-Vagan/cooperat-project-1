package ru.jd6team7.cooperatproject1.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.model.PetState;

import java.io.FileInputStream;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PetControllerTest {

    @LocalServerPort
    public int locPort;

    @Value("${pets.photos.dir.path}")
    private String photoDir;

    @Autowired
    private TestRestTemplate testRestTemp;

    @Autowired
    private PetController petTestController;

    private final Pet dummyTestExistedPet = new Pet();

    @BeforeEach
    void setUp() {
        dummyTestExistedPet.setId(1L);
        dummyTestExistedPet.setPetName("Бетти");
        dummyTestExistedPet.setAnimalKind("собака");
        dummyTestExistedPet.setAnimalGender("девочка");
        dummyTestExistedPet.setAge(2);
        dummyTestExistedPet.setCurrentState(PetState.AT_SHELTER.getCode());
    }

    /**
     * Тест запуска контроллера
     * @return Проверяет что Контроллер не Пустой
     */
    @Test
    void runControllerTest(){
        Assertions.assertNotNull(petTestController);
    }

    /**
     * Тест получения сведений о Питомце по его ИД номеру
     * @return Проверяет что Эндпоинт возвращает ожидаемый объект типа Питомец
     */
    @Test
    void getPetTestSuccess() throws Exception{
        Assertions.assertEquals(dummyTestExistedPet,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/1", Pet.class)
        );
    }

    /**
     * Тест ситуации когда не находит сведений о Питомце по его ИД номеру
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPetTestFail() throws Exception{
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
            testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/10", Pet.class).getStatusCode()
        );
    }

    /**
     * Тест получения фото существующего Питомца
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPictureOfPetFromFileStoreTestSuccess() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/1/photoFromFileStore", String.class).getStatusCode()
        );
    }

    /**
     * Тест ситуации когда не находит сведений о Питомце по его ИД номеру
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPictureOfPetFromFileStoreTestInternalServerError() throws Exception {
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/10/photoFromFileStore", String.class).getStatusCode()
        );
    }

    /**
     * Тест ситуации когда не находит данных о фото существующего Питомца
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPictureOfPetFromFileStoreTestNotFound() throws Exception {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/4/photoFromFileStore", String.class).getStatusCode()
        );
    }

    /**
     * Тест создания записи данных о Питомце
     * @return Проверяет что Эндпоинт возвращает ожидаемый экземпляр типа Питомец
     */
    @Test
    void createPetTestSuccess() throws Exception{
        dummyTestExistedPet.setId(0L);
        dummyTestExistedPet.setPetName("Лайла");
        Pet actualPet = testRestTemp.postForObject("http://localhost:" + locPort + "/shelter/pet",
                dummyTestExistedPet, Pet.class);
        Assertions.assertEquals(dummyTestExistedPet, actualPet);
    }

    /**
     * Тест загрузки фото Питомца
     * @return Проверяет что Эндпоинт возвращает ожидаемый экземпляр типа Питомец
     */
    @Test
    void uploadPetPictureTest() throws Exception {
        FileInputStream dummyInpStream = new FileInputStream(photoDir + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.postForEntity("http://localhost:" + locPort + "/shelter/pet/4/setPhoto",
                        uploadFile,
                        String.class).getStatusCode()
        );
    }

    /**
     * Тест обновления данных о Питомце
     * @return Проверяет что Эндпоинт возвращает ожидаемый экземпляр типа Питомец после внесения изменений
     */
    @Test
    void updatePetTest() throws Exception {
        dummyTestExistedPet.setAge(5);
        testRestTemp.put("http://localhost:" + locPort + "/shelter/pet",
                dummyTestExistedPet);
        Assertions.assertEquals(dummyTestExistedPet,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/" + dummyTestExistedPet.getId(),
                        Pet.class)
        );
    }

    /**
     * Тест удаления данных о Питомце
     * @return Проверяет что Эндпоинт возвращает Пустоту при попытке получить данные об удалённом Питомце
     */
    @Test
    void deletePetTest() throws Exception {
        testRestTemp.delete("http://localhost:" + locPort + "/shelter/pet?petID=" + dummyTestExistedPet.getId());
        Assertions.assertNull(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/" + dummyTestExistedPet.getId(),
                Pet.class)
        );
    }

    /**
     * Тест изменения статуса Питомца
     * @return Проверяет что Эндпоинт возвращает объект класса Питомец, у которого
     * изменён статус на установленный
     */
    @Test
    void updatePetStateTest() throws Exception {
        dummyTestExistedPet.setCurrentState(PetState.WITH_VISITOR.getCode());
        testRestTemp.put("http://localhost:" + locPort + "/shelter/pet/setPetState?petID="+
                        dummyTestExistedPet.getId() + "&inpState=" + PetState.WITH_VISITOR
                , Pet.class);
        Assertions.assertEquals(PetState.WITH_VISITOR.getCode(),
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/" + dummyTestExistedPet.getId(),
                        Pet.class).getCurrentState()
        );
    }

    /**
     * Тест получения списка всех Питомцев-малышей
     * @return Проверяет что Эндпоинт возвращает статус успешно выполненного запроса 200
     * Т.е. список Питомцев выведен
     */
    @Test
    void getAllKidsPetTest() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/showAllKidsPet",
                        List.class).getStatusCode()
        );
    }

    /**
     * Тест получения списка всех взрослых Питомцев
     * @return Проверяет что Эндпоинт возвращает статус успешно выполненного запроса 200
     * Т.е. список Питомцев выведен
     */
    @Test
    void getAllAdultPetsTest() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/showAllAdultPets",
                        List.class).getStatusCode()
        );
    }

    /**
     * Тест получения списка всех взрослых Питомцев со статусом <b><i>"в приюте"</i></b>
     * @return Проверяет что Эндпоинт возвращает статус успешно выполненного запроса 200
     * Т.е. список Питомцев выведен
     */
    @Test
    void getAdultPetsForVisitorTest() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/showAdultPetsForVisitor",
                        List.class).getStatusCode()
        );
    }

    /**
     * Тест получения списка всех Питомцев-малышей со статусом <b><i>"в приюте"</i></b>
     * @return Проверяет что Эндпоинт возвращает статус успешно выполненного запроса 200
     * Т.е. список Питомцев выведен
     */
    @Test
    void getKidsPetsForVisitorTest() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/showKidsPetsForVisitor",
                        List.class).getStatusCode()
        );
    }

    /**
     * Тест получения списка всех Питомцев с статусом <b><i>"в приюте"</i></b>
     * @return Проверяет что Эндпоинт возвращает статус успешно выполненного запроса 200
     * Т.е. список Питомцев выведен
     */
    @Test
    void showAllPetsWithStateTestSuccess() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/showAllPetsWithState?inpState=" + PetState.AT_SHELTER,
                        List.class).getStatusCode()
        );
    }

    /**
     * Тест получения списка всех Питомцев со статусом <b><i>"усыновлён"</i></b>
     * @return Проверяет что Эндпоинт возвращает статус ненайденного параметра запроса 404
     * Т.е. список Питомцев пустой
     */
    @Test
    void showAllPetsWithStateTestNotFound() throws Exception {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/showAllPetsWithState?inpState=" + PetState.ADOPTED,
                        List.class).getStatusCode()
        );
    }
}