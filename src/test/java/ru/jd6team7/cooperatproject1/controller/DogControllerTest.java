package ru.jd6team7.cooperatproject1.controller;

import org.aspectj.lang.annotation.Before;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.repository.DogRepository;
import ru.jd6team7.cooperatproject1.service.PetService;

import java.io.FileInputStream;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DogControllerTest {

    @LocalServerPort
    public int locPort;

    @Value("${pets.photos.dir.path}")
    private String photoDir;

    @Autowired
    private TestRestTemplate testRestTemp;

    @Autowired
    private PetController petTestController;

    private final JSONObject dummyTestPostObj = new JSONObject();

    private final Dog dummyTestExistedDog = new Dog();
    private final Dog dummyTestCreateDeleteDog = new Dog();

    @BeforeEach
    void setUp() throws JSONException {
        dummyTestPostObj.put("petID", 100L);
        dummyTestPostObj.put("petName", "Джек");
        dummyTestPostObj.put("animalGender", "мальчик");
        dummyTestPostObj.put("age", 3);
        dummyTestPostObj.put("currentState", "AT_SHELTER");
        dummyTestPostObj.put("shelterID", 1);

        dummyTestExistedDog.setPetID(4L);
        dummyTestExistedDog.setPetName("Мухтар");
        dummyTestExistedDog.setAnimalGender("мальчик");
        dummyTestExistedDog.setAge(5);
        dummyTestExistedDog.setCurrentState(PetState.WITH_VISITOR.name());
        dummyTestExistedDog.setShelterID(1);
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
        Assertions.assertEquals(dummyTestExistedDog,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/dog/4", Dog.class)
        );
    }

    /**
     * Тест ситуации когда не находит сведений о Питомце по его ИД номеру
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPetTestFail() throws Exception{
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/10", Dog.class).getStatusCode()
        );
    }

    /**
     * Тест получения фото существующего Питомца
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPictureOfPetFromFileStoreTestSuccess() throws Exception {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/2/photoFromFileStore", String.class).getStatusCode()
        );
    }

    /**
     * Тест ситуации когда не находит сведений о Питомце по его ИД номеру
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPictureOfPetFromFileStoreTestInternalServerError() throws Exception {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/99/photoFromFileStore", String.class).getStatusCode()
        );
    }

    /**
     * Тест ситуации когда не находит данных о фото существующего Питомца
     * @return Проверяет что Эндпоинт возвращает ожидаемый статус Http запроса
     */
    @Test
    void getPictureOfPetFromFileStoreTestNotFound() throws Exception {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/10/photoFromFileStore", String.class).getStatusCode()
        );
    }

    /**
     * Тест создания записи данных о Питомце
     * @return Проверяет что Эндпоинт возвращает ожидаемый экземпляр типа Питомец
     */
    @Test
    void createPetTestSuccess() throws Exception{
        /*when(dogRepo.save(any(Dog.class))).thenReturn(dummyTestExistedDog);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/shelter/pet/dog") //send
                        .content(dummyTestPostObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //receive
                .andExpect(jsonPath("$.petID").value(100L))
                .andExpect(jsonPath("$.petName").value("Джек"))
                .andExpect(jsonPath("$.animalGender").value("мальчик"))
                .andExpect(jsonPath("$.age").value(3))
                .andExpect(jsonPath("$.currentState").value("AT_SHELTER"))
                .andExpect(jsonPath("$.shelterID").value(1));*/


        Dog actualDog = testRestTemp.postForObject("http://localhost:" + locPort + "/shelter/pet/dog",
                dummyTestExistedDog, Dog.class);
        Assertions.assertEquals(dummyTestExistedDog, actualDog);
    }

    /**
     * Тест загрузки фото Питомца
     * @return Проверяет что Эндпоинт возвращает ожидаемый экземпляр типа Питомец
     */
    @Test
    void uploadPetPictureTest() throws Exception {
        FileInputStream dummyInpStream = new FileInputStream(photoDir + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo.jpg", dummyInpStream.readAllBytes());
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.postForEntity("http://localhost:" + locPort + "/shelter/pet/dog/4/setPhoto",
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
        dummyTestExistedDog.setAge(5);
        testRestTemp.put("http://localhost:" + locPort + "/shelter/pet/dog",
                dummyTestExistedDog);
        Assertions.assertEquals(dummyTestExistedDog,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/dog/" + dummyTestExistedDog.getPetID(),
                        Dog.class)
        );
    }

    /**
     * Тест удаления данных о Питомце
     * @return Проверяет что Эндпоинт возвращает Пустоту при попытке получить данные об удалённом Питомце
     */
    @Test
    void deletePetTest() throws Exception {
        testRestTemp.delete("http://localhost:" + locPort + "/shelter/pet/dog?petID=4");
        Assertions.assertNull(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/dog/" + dummyTestExistedDog.getPetID(),
                Dog.class)
        );
    }

    /**
     * Тест изменения статуса Питомца
     * @return Проверяет что Эндпоинт возвращает объект класса Питомец, у которого
     * изменён статус на установленный
     */
    @Test
    void updatePetStateTest() throws Exception {
        dummyTestExistedDog.setCurrentState(PetState.WITH_VISITOR.name());
        testRestTemp.put("http://localhost:" + locPort + "/shelter/pet/dog/setPetState?petID="+
                        dummyTestExistedDog.getPetID() + "&inpState=" + PetState.WITH_VISITOR
                , Dog.class);
        Assertions.assertEquals(PetState.WITH_VISITOR.name(),
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/pet/dog/" + dummyTestExistedDog.getPetID(),
                        Dog.class).getCurrentState()
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
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/showAllKidsPet",
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
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/showAllAdultPets",
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
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/showAdultPetsForVisitor",
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
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/showKidsPetsForVisitor",
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
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/showAllPetsWithState?inpState=" + PetState.AT_SHELTER,
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
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/pet/dog/showAllPetsWithState?inpState=" + PetState.ADOPTED,
                        List.class).getStatusCode()
        );
    }
}

