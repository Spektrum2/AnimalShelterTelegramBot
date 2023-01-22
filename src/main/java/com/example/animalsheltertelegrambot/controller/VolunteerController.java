package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.model.*;
import com.example.animalsheltertelegrambot.record.AnimalRecord;
import com.example.animalsheltertelegrambot.record.ReportRecord;
import com.example.animalsheltertelegrambot.record.UserRecord;
import com.example.animalsheltertelegrambot.record.VolunteerRecord;
import com.example.animalsheltertelegrambot.service.PhotoOfAnimalService;
import com.example.animalsheltertelegrambot.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    private final VolunteerService volunteerService;
    private final PhotoOfAnimalService photoOfAnimalService;

    public VolunteerController(VolunteerService volunteerService, PhotoOfAnimalService photoOfAnimalService) {
        this.volunteerService = volunteerService;
        this.photoOfAnimalService = photoOfAnimalService;
    }

    @Operation(
            summary = "Вывод всех волонтеров",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Вывод всех волонтеров",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            }
    )
    @GetMapping
    public Collection<VolunteerRecord> getAllVolunteers() {
        return volunteerService.getAllVolunteers();
    }

    @Operation(
            summary = "Вывод всех пользователей",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Вывод всех пользователей",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserData.class))
                            )
                    )
            }
    )
    @GetMapping("/user")
    public Collection<UserRecord> getAllUsers() {
        return volunteerService.getAllUsers();
    }

    @Operation(
            summary = "Поиск волонтера по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поиск волонтера по id",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    )
            }
    )
    @GetMapping("{id}")
    public VolunteerRecord findVolunteer(@Parameter(description = "Введите id волонтера", example = "1")
                                         @PathVariable Long id) {
        return volunteerService.findVolunteer(id);
    }

    @Operation(
            summary = "Поиск пользователя по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поиск пользователя по id",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @GetMapping("/user/{id}")
    public UserRecord findUser(@Parameter(description = "Введите id пользователя", example = "1")
                               @PathVariable Long id) {
        return volunteerService.findUser(id);
    }

    @Operation(
            summary = "Запись волонтера в БД",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запись волонтера в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    )
            }
    )
    @PostMapping
    public VolunteerRecord postVolunteer(@RequestBody @Valid VolunteerRecord volunteerRecord) {
        return volunteerService.createVolunteer(volunteerRecord);
    }

    @Operation(
            summary = "Удаление волонтера",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаление волонтера",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    )
            }
    )
    @DeleteMapping("{id}")
    public VolunteerRecord deleteVolunteer(@Parameter(description = "Введите id волонтера", example = "1")
                                           @PathVariable Long id) {
        return volunteerService.deleteVolunteer(id);
    }

    @Operation(
            summary = "Изменение волонтера",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изменение волонтера",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    )
            }
    )
    @PutMapping("{id}")
    public VolunteerRecord putVolunteer(@Parameter(description = "Введите id волонтера", example = "1")
                                        @PathVariable Long id,
                                        @RequestBody @Valid VolunteerRecord volunteerRecord) {
        return volunteerService.editVolunteer(id, volunteerRecord);
    }

    @Operation(
            summary = "Привязка животного к волонтеру",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Привязка животного к волонтеру",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            }
    )
    @PatchMapping("{id}/animal")
    public AnimalRecord patchVolunteerAnimal(@Parameter(description = "Введите id волонтера", example = "1")
                                             @PathVariable Long id,
                                             @Parameter(description = "Введите id животного", example = "1")
                                             @RequestParam("animalId") Long animalId) {
        return volunteerService.patchVolunteerAnimal(id, animalId);
    }

    @Operation(
            summary = "Поиск всех отчетов пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поиск всех отчетов пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            tags = "Report"
    )
    @GetMapping("{id}/reports")
    public Collection<ReportRecord> findReportsByUser(@Parameter(description = "Введите id пользователя", example = "1")
                                                      @PathVariable Long id) {
        return volunteerService.findReportsByUser(id);
    }

    @Operation(
            summary = "Привязка животного к пользователю",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Привязка животного к пользователю",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @PatchMapping("/user/{id}/animal")
    public UserRecord patchUserAnimal(@Parameter(description = "Введите id пользователя", example = "1")
                                      @PathVariable Long id,
                                      @Parameter(description = "Введите id животного", example = "1")
                                      @RequestParam("animalId") Long animalId) {
        return volunteerService.patchUserAnimal(id, animalId);
    }

    @Operation(
            summary = "Просмотр фотографии животного",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Просмотр фотографии животного",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PhotoOfAnimal.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> readAvatarFromDb(@Parameter(description = "Введите id фотографии", example = "1")
                                                   @PathVariable long id) {
        Pair<String, byte[]> pair = photoOfAnimalService.readAvatarFromDb(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pair.getFirst()))
                .contentLength(pair.getSecond().length)
                .body(pair.getSecond());
    }

    @Operation(
            summary = "Поиск пользователя по животному",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поиск пользователя по животному",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @GetMapping("/animal/{animalId}")
    public UserRecord findUserByAnimal(@Parameter(description = "Введите id животного", example = "1")
                                       @PathVariable Long animalId) {
        return volunteerService.findUserByAnimal(animalId);
    }

    @Operation(
            summary = "Увеличение испытательного срока пользователю",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Увеличение испытательного срока пользователю",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @PatchMapping("/user/{id}/period/")
    public UserRecord extensionPeriod(@Parameter(description = "Введите id пользователя", example = "1")
                                      @PathVariable Long id,
                                      @Parameter(description = "Введите 1 - увеличить испытательный срок на 14 дней. Введите 2  - увеличить испытательный срок на 30 дней", example = "1")
                                      @RequestParam("number") Integer number) {
        return volunteerService.extensionPeriod(id, number);
    }

    @Operation(
            summary = "Поиск пользователей по приюту(приют собак или приют кошек)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поиск пользователей по приюту(приют собак или приют кошек)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @GetMapping("/user/shelter")
    public Collection<UserRecord> getAllUserShelterDogOrShelterCat(@Parameter(description = "Введите 1 - показать пользователей, которые обратились в приют собак. Введите 2  -  показать пользователей, которые обратились в приют кошек", example = "1")
                                                                   @RequestParam("number") Integer number) {
        return volunteerService.getAllUserShelterDogOrShelterCat(number);
    }

    @Operation(
            summary = "Отправка сообщений пользователю",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отправка сообщений пользователю",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @GetMapping("/user/{id}/decision/")
    public String sendMessageToUser(@Parameter(description = "Введите id пользователя", example = "1")
                                      @PathVariable Long id,
                                      @Parameter(description = "Введите 1 - отчет заполняется плохо. Введите 2  - вы прошли испытательный срок. Введите 3 - вы не прошли испытательный срок", example = "1")
                                      @RequestParam("number") Integer number) {
        volunteerService.sendMessageToUser(id,number);
        return "Сообщение отправлено";
    }
}
