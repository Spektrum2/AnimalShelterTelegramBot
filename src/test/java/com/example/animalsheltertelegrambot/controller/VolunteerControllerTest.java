package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.component.RecordMapper;
import com.example.animalsheltertelegrambot.model.AnimalType;
import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.record.AnimalRecord;
import com.example.animalsheltertelegrambot.record.ReportRecord;
import com.example.animalsheltertelegrambot.record.UserRecord;
import com.example.animalsheltertelegrambot.record.VolunteerRecord;
import com.example.animalsheltertelegrambot.repository.AnimalRepository;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.example.animalsheltertelegrambot.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VolunteerControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private RecordMapper recordMapper;
    private final Faker faker = new Faker();

    @AfterEach
    public void afterEach() {
        reportRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    @Test
    public void createTest() {
        addVolunteer(generateVolunteer());
    }

    @Test
    public void putTest() {
        VolunteerRecord volunteerRecord = addVolunteer(generateVolunteer());
        String lastName = volunteerRecord.getLastName();

        ResponseEntity<VolunteerRecord> getRecordResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/volunteer/" + volunteerRecord.getId(), VolunteerRecord.class);
        assertThat(getRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRecordResponseEntity.getBody()).isNotNull();
        assertThat(getRecordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(volunteerRecord);
        assertThat(getRecordResponseEntity.getBody().getLastName()).isEqualTo(lastName);

        volunteerRecord.setLastName("Gates");

        ResponseEntity<VolunteerRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/" + volunteerRecord.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(volunteerRecord),
                VolunteerRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(volunteerRecord);
        assertThat(recordResponseEntity.getBody().getLastName()).isEqualTo("Gates");
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
        List<UserRecord> userRecords = Stream.generate(() -> generateUser(animalRecords.get(faker.random().nextInt(animalRecords.size()))))
                .limit(10)
                .toList();
        List<ReportRecord> reportRecords = Stream.generate(() -> generateReport(userRecords.get(faker.random().nextInt(userRecords.size()))))
                .limit(50)
                .toList();
        UserRecord userRecord = userRecords.get(0);
        List<ReportRecord> expected = reportRecords.stream()
                .filter(r -> r.getUser().getId().equals(userRecord.getId()))
                .toList();

        ResponseEntity<List<VolunteerRecord>> getAllVolunteersResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(getAllVolunteersResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllVolunteersResponseEntity.getBody())
                .hasSize(volunteerRecords.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(volunteerRecords);

        ResponseEntity<List<UserRecord>> getAllUsersResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/user/",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(getAllUsersResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllUsersResponseEntity.getBody())
                .hasSize(userRecords.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(userRecords);

        ResponseEntity<List<ReportRecord>> getReportsResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/" + userRecord.getId() + "/reports/",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(getReportsResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getReportsResponseEntity.getBody()).isNotNull();
        assertThat(getReportsResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void findUser() {
        VolunteerRecord volunteerRecord = addVolunteer(generateVolunteer());
        AnimalRecord animalRecord = addAnimal(generateAnimal(volunteerRecord));
        UserRecord userRecord = generateUser(animalRecord);

        ResponseEntity<UserRecord> getRecordResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/volunteer/user/" + userRecord.getId(), UserRecord.class);

        assertThat(getRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRecordResponseEntity.getBody()).isNotNull();
        assertThat(getRecordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(userRecord);
        assertThat(getRecordResponseEntity.getBody().getAnimal()).usingRecursiveComparison().isEqualTo(animalRecord);

        ResponseEntity<UserRecord> getByAnimalRecordResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/volunteer/animal/" + animalRecord.getId(), UserRecord.class);

        assertThat(getByAnimalRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getByAnimalRecordResponseEntity.getBody()).isNotNull();
        assertThat(getByAnimalRecordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(userRecord);
        assertThat(getByAnimalRecordResponseEntity.getBody().getAnimal()).usingRecursiveComparison().isEqualTo(animalRecord);


    }

    @Test
    public void deleteTest() {
        VolunteerRecord volunteerRecord = addVolunteer(generateVolunteer());

        ResponseEntity<VolunteerRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/" + volunteerRecord.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(volunteerRecord),
                VolunteerRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(volunteerRecord);
        assertThat(recordResponseEntity.getBody().getLastName()).isEqualTo(volunteerRecord.getLastName());
    }

    @Test
    public void patchVolunteerAnimalTest() {
        VolunteerRecord volunteerRecord = addVolunteer(generateVolunteer());
        AnimalRecord animalRecord = addAnimal(generateAnimal(null));
        AnimalRecord animalRecord1 = animalRecord;
        animalRecord1.setVolunteer(volunteerRecord);

        ResponseEntity<AnimalRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/" + volunteerRecord.getId() + "/animal?animalId=" + animalRecord.getId(),
                HttpMethod.PATCH,
                new HttpEntity<>(animalRecord),
                AnimalRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(animalRecord1);
        assertThat(recordResponseEntity.getBody().getVolunteer()).usingRecursiveComparison().isEqualTo(volunteerRecord);
    }

    @Test
    public void patchUserAnimal() {
        VolunteerRecord volunteerRecord = addVolunteer(generateVolunteer());
        AnimalRecord animalRecord = addAnimal(generateAnimal(volunteerRecord));
        UserRecord userRecord = generateUser(animalRecord);
        UserRecord userRecord1 = userRecord;
        userRecord1.setDate(userRecord1.getDate().plusMonths(1));
        userRecord1.setAnimal(animalRecord);

        ResponseEntity<UserRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/user/" + userRecord.getId() + "/animal?animalId=" + animalRecord.getId(),
                HttpMethod.PATCH,
                new HttpEntity<>(userRecord),
                UserRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(userRecord1);
        assertThat(recordResponseEntity.getBody().getAnimal()).usingRecursiveComparison().isEqualTo(animalRecord);
    }


    private UserRecord generateUser(AnimalRecord animalRecord) {
        UserRecord userRecord = new UserRecord();
        userRecord.setChatId(faker.number().randomNumber());
        userRecord.setName(faker.name().firstName());
        userRecord.setPhoneNumber(faker.phoneNumber().phoneNumber());
        userRecord.setShelter(faker.random().nextInt(1, 2));
        userRecord.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        if (animalRecord != null) {
            userRecord.setAnimal(animalRecord);
        }
        UserData userData = recordMapper.toEntity(userRecord);
        return recordMapper.toRecord(userRepository.save(userData));
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

    private VolunteerRecord generateVolunteer() {
        VolunteerRecord volunteerRecord = new VolunteerRecord();
        volunteerRecord.setChatId(faker.number().randomNumber());
        volunteerRecord.setName(faker.name().firstName());
        volunteerRecord.setLastName(faker.name().lastName());
        return volunteerRecord;
    }

    private ReportRecord generateReport(UserRecord userRecord) {
        ReportRecord reportRecord = new ReportRecord();
        reportRecord.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        reportRecord.setDiet(faker.book().title());
        reportRecord.setHealth(faker.book().title());
        reportRecord.setBehaviorChange(faker.book().title());
        if (userRecord != null) {
            reportRecord.setUser(userRecord);
        }
        Report report = recordMapper.toEntity(reportRecord);
        return recordMapper.toRecord(reportRepository.save(report));
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