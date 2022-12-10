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
import ru.jd6team7.cooperatproject1.model.Cat;
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.repository.CatRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatServiceTest {

    @Mock
    private CatRepository testPetRepo;

    @InjectMocks
    private CatService testService;

    private final String PHOTO_DIR = "src/main/resources/static/pets_photos";

    private final Cat dummyTestCat = new Cat();

    @BeforeEach
    void setUp() {
        dummyTestCat.setId(10L);
        dummyTestCat.setPetName("Бетти");
        dummyTestCat.setAnimalGender("девочка");
        dummyTestCat.setAge(7);
        dummyTestCat.setCurrentState(PetState.AT_SHELTER.getCode());
    }

    @Test
    void addPetTest() {
        when(testPetRepo.save(any())).thenReturn(dummyTestCat);
        Assertions.assertEquals(dummyTestCat, testService.addPet(dummyTestCat));
    }

    @Test
    void findPetTestSuccess() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestCat));
        Assertions.assertEquals(dummyTestCat, testService.findPet(10L));
    }

    @Test
    void findPetTestNotFound() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.findPet(100L));
    }

    @Test
    void deletePetTest() {
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertEquals(true, testService.deletePet(dummyTestCat.getId()));
    }

    @Test
    void getKidsPetsForVisitorTest() {
        List<Cat> expectedListDogs = List.of(dummyTestCat);
        when(testPetRepo.getKidsPetsForVisitor()).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getKidsPetsForVisitor());
    }

    @Test
    void getAdultPetsForVisitorTest() {
        List<Cat> expectedListDogs = List.of(dummyTestCat);
        when(testPetRepo.getAdultPetsForVisitor()).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAdultPetsForVisitor());
    }

    @Test
    void getAllKidsPetsTest() {
        List<Cat> expectedListDogs = List.of(dummyTestCat);
        when(testPetRepo.getAllByAgeBefore(any(Integer.class))).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAllKidsPets(2));
    }

    @Test
    void getAllAdultPetsTest() {
        List<Cat> expectedListDogs = List.of(dummyTestCat);
        when(testPetRepo.getAllByAgeAfter(any(Integer.class))).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAllAdultPets(2));
    }

    @Test
    void getAllPetsWithStateTest() {
        List<Cat> expectedListDogs = List.of(dummyTestCat);
        when(testPetRepo.getAllByCurrentStateLike(any(String.class))).thenReturn(expectedListDogs);
        Assertions.assertEquals(expectedListDogs, testService.getAllPetsWithState(PetState.AT_SHELTER.getCode()));
    }

    @Test
    void addPetPhotoTestSuccess() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.getPetByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestCat));
        when(testPetRepo.save(any(Cat.class))).thenReturn(dummyTestCat);
        testService.addPetPhoto(10L, uploadFile);
        Assertions.assertNotNull(dummyTestCat.getPathFileToPhoto());
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
}