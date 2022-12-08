package ru.jd6team7.cooperatproject1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.repository.DogRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository testPetRepo;

    @InjectMocks
    private PetService testService;

    private final String PHOTO_DIR = "src/main/resources/static/pets_photos";

    private final Dog dummyTestDog = new Dog();

    @BeforeEach
    void setUp() {
        dummyTestDog.setId(10L);
        dummyTestDog.setPetID(10L);
        dummyTestDog.setPetName("Бетти");
        dummyTestDog.setAnimalGender("девочка");
        dummyTestDog.setAge(7);
        dummyTestDog.setCurrentState(PetState.AT_SHELTER.getCode());
    }

    @Test
    void addPetTest() {
        when(testPetRepo.save(any())).thenReturn(dummyTestDog);
        Assertions.assertEquals(dummyTestDog, testService.addPet(dummyTestDog));
    }

    @Test
    void findPetTestSuccess() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestDog));
        Assertions.assertEquals(dummyTestDog, testService.findPet(10L));
    }

    @Test
    void findPetTestNotFound() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.findPet(100L));
    }

    @Test
    void updatePetTestSuccess() {
        dummyTestDog.setPetName("Чара");
        dummyTestDog.setAge(3);
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestDog));
        when(testPetRepo.save(any(Dog.class))).thenReturn(dummyTestDog);
        Assertions.assertEquals(dummyTestDog, testService.updatePet(dummyTestDog));
    }

    @Test
    void updatePetTestFail() {
        dummyTestDog.setPetName("Чара");
        dummyTestDog.setAge(3);
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.updatePet(dummyTestDog));
    }

    @Test
    void deletePetTest() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertEquals(true, testService.deletePet(dummyTestDog.getId()));
    }

    @Test
    void getKidsPetsForVisitorTest() {
        List<Dog> expectedListDogs = List.of(dummyTestDog);
        when(testPetRepo.getKidsPetsForVisitor()).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getKidsPetsForVisitor());
    }

    @Test
    void getAdultPetsForVisitorTest() {
        List<Dog> expectedListDogs = List.of(dummyTestDog);
        when(testPetRepo.getAdultPetsForVisitor()).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAdultPetsForVisitor());
    }

    @Test
    void getAllKidsPetsTest() {
        List<Dog> expectedListDogs = List.of(dummyTestDog);
        when(testPetRepo.getAllByAgeBefore(any(Integer.class))).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAllKidsPets(2));
    }

    @Test
    void getAllAdultPetsTest() {
        List<Dog> expectedListDogs = List.of(dummyTestDog);
        when(testPetRepo.getAllByAgeAfter(any(Integer.class))).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAllAdultPets(2));
    }

    @Test
    void getAllPetsWithStateTest() {
        List<Dog> expectedListDogs = List.of(dummyTestDog);
        when(testPetRepo.getAllByCurrentStateLike(any(String.class))).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAllPetsWithState(PetState.AT_SHELTER.getCode()));
    }

    @Test
    void addPetPhotoTestSuccess() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestDog));
        when(testPetRepo.save(any(Dog.class))).thenReturn(dummyTestDog);
        testService.addPetPhoto(10L, uploadFile);
        Assertions.assertNotNull(dummyTestDog.getPathFileToPhoto());
    }

    @Test
    void addPetPhotoTestNotFound() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(PetNotFoundException.class,
                ()->{testService.addPetPhoto(100L, uploadFile);
        }
        );
    }

    @Test
    void putPetStateTestSuccess() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestDog));
        when(testPetRepo.save(any(Dog.class))).thenReturn(dummyTestDog);
        Assertions.assertEquals(dummyTestDog,
                testService.putDogState(10L, PetState.STAY_OUT)
                );
    }

    @Test
    void putPetStateTestNotFound() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(PetNotFoundException.class,
                ()->{testService.putDogState(100L, PetState.AT_SHELTER);
        }
        );
    }
}