package ru.jd6team7.cooperatproject1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.jd6team7.cooperatproject1.exceptions.PetNotFoundException;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.repository.PetRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository testPetRepo;

    @InjectMocks
    private PetService testService;

    private final String PHOTO_DIR = "src/main/resources/static/pets_photos";

    private final Pet dummyTestPet = new Pet();

    @BeforeEach
    void setUp() {
        dummyTestPet.setId(10L);
        dummyTestPet.setPetName("Бетти");
        dummyTestPet.setAnimalKind("собака");
        dummyTestPet.setAnimalGender("девочка");
        dummyTestPet.setAge(7);
        dummyTestPet.setCurrentState(PetState.AT_SHELTER.getCode());
    }

    @Test
    void addPetTest() {
        when(testPetRepo.save(any())).thenReturn(dummyTestPet);
        Assertions.assertEquals(dummyTestPet, testService.addPet(dummyTestPet));
    }

    @Test
    void findPetTestSuccess() {
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyTestPet));
        Assertions.assertEquals(dummyTestPet, testService.findPet(10L));
    }

    @Test
    void findPetTestNotFound() {
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.findPet(100L));
    }

    @Test
    void updatePetTestSuccess() {
        dummyTestPet.setPetName("Чара");
        dummyTestPet.setAge(3);
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyTestPet));
        when(testPetRepo.save(any(Pet.class))).thenReturn(dummyTestPet);
        Assertions.assertEquals(dummyTestPet, testService.updatePet(dummyTestPet));
    }

    @Test
    void updatePetTestFail() {
        dummyTestPet.setPetName("Чара");
        dummyTestPet.setAge(3);
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertNull(testService.updatePet(dummyTestPet));
    }

    @Test
    void deletePetTest() {
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertEquals(true, testService.deletePet(dummyTestPet.getId()));
    }

    @Test
    void getKidsPetsForVisitorTest() {
        List<Pet> expectedListPets = List.of(dummyTestPet);
        when(testPetRepo.getKidsPetsForVisitor()).thenReturn(expectedListPets);
        Assertions.assertEquals(expectedListPets, testService.getKidsPetsForVisitor());
    }

    @Test
    void getAdultPetsForVisitorTest() {
        List<Pet> expectedListPets = List.of(dummyTestPet);
        when(testPetRepo.getAdultPetsForVisitor()).thenReturn(expectedListPets);
        Assertions.assertEquals(expectedListPets, testService.getAdultPetsForVisitor());
    }

    @Test
    void getAllKidsPetsTest() {
        List<Pet> expectedListPets = List.of(dummyTestPet);
        when(testPetRepo.getAllByAgeBefore(any(Integer.class))).thenReturn(expectedListPets);
        Assertions.assertEquals(expectedListPets, testService.getAllKidsPets(2));
    }

    @Test
    void getAllAdultPetsTest() {
        List<Pet> expectedListPets = List.of(dummyTestPet);
        when(testPetRepo.getAllByAgeAfter(any(Integer.class))).thenReturn(expectedListPets);
        Assertions.assertEquals(expectedListPets, testService.getAllAdultPets(2));
    }

    @Test
    void getAllPetsWithStateTest() {
        List<Pet> expectedListPets = List.of(dummyTestPet);
        when(testPetRepo.getAllByCurrentStateLike(any(String.class))).thenReturn(expectedListPets);
        Assertions.assertEquals(expectedListPets, testService.getAllPetsWithState(PetState.AT_SHELTER.getCode()));
    }

    @Test
    void addPetPhotoTestSuccess() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyTestPet));
        when(testPetRepo.save(any(Pet.class))).thenReturn(dummyTestPet);
        testService.addPetPhoto(10L, uploadFile);
        Assertions.assertNotNull(dummyTestPet.getPathFileToPhoto());
    }

    @Test
    void addPetPhotoTestNotFound() throws IOException {
        FileInputStream dummyInpStream = new FileInputStream(PHOTO_DIR + "/test_photo.jpg");
        MultipartFile uploadFile = new MockMultipartFile("test_photo", dummyInpStream);
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(PetNotFoundException.class,
                ()->{testService.addPetPhoto(100L, uploadFile);
        }
        );
    }

    @Test
    void putPetStateTestSuccess() {
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyTestPet));
        when(testPetRepo.save(any(Pet.class))).thenReturn(dummyTestPet);
        Assertions.assertEquals(dummyTestPet,
                testService.putPetState(10L, PetState.STAY_OUT)
                );
    }

    @Test
    void putPetStateTestNotFound() {
        when(testPetRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(PetNotFoundException.class,
                ()->{testService.putPetState(100L, PetState.AT_SHELTER);
        }
        );
    }
}