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
    private PetService testService;

    private final String PHOTO_DIR = "src/main/resources/static/pets_photos";

    private final Cat dummyTestCat = new Cat();

    @BeforeEach
    void setUp() {
        dummyTestCat.setId(10L);
        dummyTestCat.setPetID(3L);
        dummyTestCat.setPetName("Шебба");
        dummyTestCat.setAnimalGender("девочка");
        dummyTestCat.setAge(7);
        dummyTestCat.setCurrentState(PetState.AT_SHELTER.getCode());
    }

    @Test
    void addPetTest() {
        when(testPetRepo.save(any())).thenReturn(dummyTestCat);
        Assertions.assertEquals(dummyTestCat, testService.addCat(dummyTestCat));
    }

    @Test
    void findPetTestSuccess() {
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestCat));
        Assertions.assertEquals(dummyTestCat, testService.findCat(10L));
    }

    @Test
    void findPetTestNotFound() {
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.findCat(100L));
    }

    @Test
    void updatePetTestSuccess() {
        dummyTestCat.setAge(3);
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestCat));
        when(testPetRepo.save(any(Cat.class))).thenReturn(dummyTestCat);
        Assertions.assertEquals(dummyTestCat, testService.updateCat(dummyTestCat));
    }

    @Test
    void updatePetTestFail() {
        dummyTestCat.setAge(3);
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.updateCat(dummyTestCat));
    }

    @Test
    void deletePetTest() {
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertTrue(testService.deleteCat(dummyTestCat.getId()));
    }

    @Test
    void getKidsPetsForVisitorTest() {
        List<Cat> expectedListCats = List.of(dummyTestCat);
        when(testPetRepo.getKidsPetsForVisitor()).thenReturn(expectedListCats);
        Assertions.assertArrayEquals(expectedListCats.toArray(),
                testService.getKidsCatsForVisitor().toArray());
    }

    @Test
    void getAdultPetsForVisitorTest() {
        List<Cat> expectedListCats = List.of(dummyTestCat);
        when(testPetRepo.getAdultPetsForVisitor()).thenReturn(expectedListCats);
        Assertions.assertArrayEquals(expectedListCats.toArray(),
                testService.getAdultCatsForVisitor().toArray());
    }

    @Test
    void getAllKidsPetsTest() {
        List<Cat> expectedListCats = List.of(dummyTestCat);
        when(testPetRepo.getAllByAgeBefore(any(Integer.class))).thenReturn(expectedListCats);
        Assertions.assertArrayEquals(expectedListCats.toArray(),
                testService.getAllKidsCats(2).toArray());
    }

    @Test
    void getAllAdultPetsTest() {
        List<Cat> expectedListCats = List.of(dummyTestCat);
        when(testPetRepo.getAllByAgeAfter(any(Integer.class))).thenReturn(expectedListCats);
        Assertions.assertArrayEquals(expectedListCats.toArray(),
                testService.getAllAdultCats(2).toArray());
    }

    @Test
    void getAllPetsWithStateTest() {
        List<Cat> expectedListCats = List.of(dummyTestCat);
        when(testPetRepo.getAllByCurrentStateLike(any(String.class))).thenReturn(expectedListCats);
        Assertions.assertArrayEquals(expectedListCats.toArray(),
                testService.getAllCatsWithState(PetState.AT_SHELTER.getCode()).toArray());
    }

    @Test
    void addPetPhotoTestSuccess() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestCat));
        when(testPetRepo.save(any(Cat.class))).thenReturn(dummyTestCat);
        testService.addCatPhoto(10L, uploadFile);
        Assertions.assertNotNull(dummyTestCat.getPathFileToPhoto());
    }

    @Test
    void addPetPhotoTestNotFound() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(PetNotFoundException.class,
                ()->{testService.addCatPhoto(100L, uploadFile);
        }
        );
    }

    @Test
    void putPetStateTestSuccess() {
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.of(dummyTestCat));
        when(testPetRepo.save(any(Cat.class))).thenReturn(dummyTestCat);
        Assertions.assertEquals(dummyTestCat,
                testService.putCatState(10L, PetState.STAY_OUT)
                );
    }

    @Test
    void putPetStateTestNotFound() {
        when(testPetRepo.getCatByPetID(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(PetNotFoundException.class,
                ()->{testService.putCatState(100L, PetState.AT_SHELTER);
        }
        );
    }
}