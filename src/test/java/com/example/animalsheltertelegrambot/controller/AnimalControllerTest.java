package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.model.AnimalType;
import com.example.animalsheltertelegrambot.record.AnimalRecord;
import com.example.animalsheltertelegrambot.record.VolunteerRecord;
import com.example.animalsheltertelegrambot.repository.AnimalRepository;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private AnimalRepository animalRepository;
    private final Faker faker = new Faker();

    @AfterEach
    public void afterEach() {
        animalRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    @Test
    public void createTest() {
        addAnimal(generateAnimal(addVolunteer(generateVolunteer())));
    }

    @Test
    public void putTest() {
        VolunteerRecord volunteerRecord1 = addVolunteer(generateVolunteer());
        VolunteerRecord volunteerRecord2 = addVolunteer(generateVolunteer());
        AnimalRecord animalRecord = addAnimal(generateAnimal(volunteerRecord1));

        ResponseEntity<AnimalRecord> getRecordResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/animal/" + animalRecord.getId(), AnimalRecord.class);
        assertThat(getRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRecordResponseEntity.getBody()).isNotNull();
        assertThat(getRecordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(animalRecord);
        assertThat(getRecordResponseEntity.getBody().getVolunteer()).usingRecursiveComparison().isEqualTo(volunteerRecord1);

        animalRecord.setVolunteer(volunteerRecord2);

        ResponseEntity<AnimalRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/animal/" + animalRecord.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(animalRecord),
                AnimalRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(animalRecord);
        assertThat(recordResponseEntity.getBody().getVolunteer()).usingRecursiveComparison().isEqualTo(volunteerRecord2);
    }

    @Test
    public void findTests() {
        List<VolunteerRecord> volunteerRecords = Stream.generate(this::generateVolunteer)
                .limit(10)
                .map(this::addVolunteer)
                .toList();
        List<AnimalRecord> animalRecords = Stream.generate(() -> generateAnimal(volunteerRecords.get(faker.random().nextInt(volunteerRecords.size()))))
                .limit(30)
                .map(this::addAnimal)
                .toList();


        ResponseEntity<List<AnimalRecord>> getAllAnimalsResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/animal/",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(getAllAnimalsResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllAnimalsResponseEntity.getBody())
                .hasSize(animalRecords.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(animalRecords);
    }

    @Test
    public void deleteTest() {
        VolunteerRecord volunteerRecord = addVolunteer(generateVolunteer());
        AnimalRecord animalRecord = addAnimal(generateAnimal(volunteerRecord));

        ResponseEntity<AnimalRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/animal/" + animalRecord.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(animalRecord),
                AnimalRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(animalRecord);
        assertThat(recordResponseEntity.getBody().getVolunteer()).usingRecursiveComparison().isEqualTo(volunteerRecord);
    }

    private VolunteerRecord generateVolunteer() {
        VolunteerRecord volunteerRecord = new VolunteerRecord();
        volunteerRecord.setName(faker.name().firstName());
        volunteerRecord.setLastName(faker.name().lastName());
        return volunteerRecord;
    }

    private AnimalRecord generateAnimal(VolunteerRecord volunteerRecord) {
        int number = faker.random().nextInt(1, 2);
        AnimalRecord animalRecord = new AnimalRecord();
        if (number == 1) {
            animalRecord.setAnimalType(AnimalType.CAT);
        } else {
            animalRecord.setAnimalType(AnimalType.DOG);
        }
        animalRecord.setAnimalName(faker.animal().name());
        if (volunteerRecord != null) {
            animalRecord.setVolunteer(volunteerRecord);
        }
        return animalRecord;
    }

    private AnimalRecord addAnimal(AnimalRecord animalRecord) {
        ResponseEntity<AnimalRecord> animalResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/animal", animalRecord, AnimalRecord.class);
        assertThat(animalResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(animalResponseEntity.getBody()).isNotNull();
        assertThat(animalResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(animalRecord);
        assertThat(animalResponseEntity.getBody().getId()).isNotNull();

        return animalResponseEntity.getBody();
    }

    private VolunteerRecord addVolunteer(VolunteerRecord volunteerRecord) {
        ResponseEntity<VolunteerRecord> volunteerResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/volunteer", volunteerRecord, VolunteerRecord.class);
        assertThat(volunteerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(volunteerResponseEntity.getBody()).isNotNull();
        assertThat(volunteerResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(volunteerRecord);
        assertThat(volunteerResponseEntity.getBody().getId()).isNotNull();

        return volunteerResponseEntity.getBody();
    }

}