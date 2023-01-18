package com.example.animalsheltertelegrambot.component;

import com.example.animalsheltertelegrambot.model.*;
import com.example.animalsheltertelegrambot.record.*;
import org.springframework.stereotype.Component;

@Component
public class RecordMapper {
    public UserRecord toRecord(UserData userData) {
        UserRecord userRecord = new UserRecord();
        userRecord.setId(userData.getId());
        userRecord.setIdChat(userData.getIdChat());
        userRecord.setName(userData.getName());
        userRecord.setPhoneNumber(userData.getPhoneNumber());
        userRecord.setShelter(userData.getShelter());
        userRecord.setDate(userData.getDate());
        if (userData.getAnimal() != null) {
            userRecord.setAnimal(toRecord(userData.getAnimal()));
        }
        return userRecord;
    }

    public AnimalRecord toRecord(Animal animal) {
        AnimalRecord animalRecord = new AnimalRecord();
        animalRecord.setId(animal.getId());
        animalRecord.setAnimalType(animal.getAnimalType());
        animalRecord.setAnimalName(animal.getAnimalName());
        if (animal.getVolunteer() != null) {
            animalRecord.setVolunteer(toRecord(animal.getVolunteer()));
        }
        return animalRecord;
    }

    public VolunteerRecord toRecord(Volunteer volunteer) {
        VolunteerRecord volunteerRecord = new VolunteerRecord();
        volunteerRecord.setId(volunteer.getId());
        volunteerRecord.setIdChat(volunteer.getIdChat());
        volunteerRecord.setName(volunteer.getName());
        volunteerRecord.setLastName(volunteer.getLastName());
        return volunteerRecord;
    }

    public PhotoOfAnimalRecord toRecord(PhotoOfAnimal photoOfAnimal) {
        return new PhotoOfAnimalRecord(
                photoOfAnimal.getId(),
                photoOfAnimal.getMediaType(),
                "http://localhost:8080/volunteer/" + photoOfAnimal.getId() + "/photo"
        );
    }

    public ReportRecord toRecord(Report report) {
        ReportRecord reportRecord = new ReportRecord();
        reportRecord.setId(report.getId());
        reportRecord.setName(report.getName());
        reportRecord.setDate(report.getDate());
        reportRecord.setDiet(report.getDiet());
        reportRecord.setHealth(report.getHealth());
        reportRecord.setBehaviorChange(report.getBehaviorChange());
        if (report.getUserData() != null) {
            reportRecord.setUser(toRecord(report.getUserData()));
        }
        if (report.getPhotoOfAnimal() != null) {
            reportRecord.setPhotoOfAnimal(toRecord(report.getPhotoOfAnimal()));
        }
        return reportRecord;
    }

    public Volunteer toEntity(VolunteerRecord volunteerRecord) {
        Volunteer volunteer = new Volunteer();
        volunteer.setIdChat(volunteerRecord.getIdChat());
        volunteer.setName(volunteerRecord.getName());
        volunteer.setLastName(volunteerRecord.getLastName());
        return volunteer;
    }

    public Animal toEntity(AnimalRecord animalRecord) {
        Animal animal = new Animal();
        animal.setAnimalType(animalRecord.getAnimalType());
        animal.setAnimalName(animalRecord.getAnimalName());
        if (animalRecord.getVolunteer() != null) {
            Volunteer volunteer = toEntity(animalRecord.getVolunteer());
            volunteer.setId(animalRecord.getVolunteer().getId());
            animal.setVolunteer(volunteer);
        }
        return animal;
    }

    public UserData toEntity(UserRecord userRecord) {
        UserData userData = new UserData();
        userData.setIdChat(userRecord.getIdChat());
        userData.setName(userRecord.getName());
        userData.setPhoneNumber(userRecord.getPhoneNumber());
        userData.setShelter(userRecord.getShelter());
        userData.setDate(userRecord.getDate());
        if (userRecord.getAnimal() != null) {
            Animal animal = toEntity(userRecord.getAnimal());
            animal.setId(userRecord.getAnimal().getId());
            userData.setAnimal(animal);
        }
        return userData;
    }

    public Report toEntity(ReportRecord reportRecord) {
        Report report = new Report();
        report.setName(reportRecord.getName());
        report.setDate(reportRecord.getDate());
        report.setDiet(reportRecord.getDiet());
        report.setHealth(reportRecord.getHealth());
        report.setBehaviorChange(reportRecord.getBehaviorChange());
        if (reportRecord.getUser() != null) {
            UserData userData = toEntity(reportRecord.getUser());
            userData.setId(reportRecord.getUser().getId());
            report.setUserData(userData);
        }
        return report;
    }
}
