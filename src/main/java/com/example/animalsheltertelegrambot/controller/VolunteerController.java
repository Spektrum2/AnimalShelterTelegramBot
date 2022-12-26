package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.model.Volunteer;
import com.example.animalsheltertelegrambot.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
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
    public Collection<Volunteer> getAllVolunteer() {
        return volunteerService.getAllVolunteer();
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
    public Volunteer findVolunteer(@Parameter(description = "Введите id волонтера", example = "1")
                                   @PathVariable Long id) {
        return volunteerService.findVolunteer(id);
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
    public Volunteer postVolunteer(@RequestBody Volunteer volunteer) {
        return volunteerService.createVolunteer(volunteer);
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
    public Volunteer deleteVolunteer(@Parameter(description = "Введите id волонтера", example = "1")
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
    public Volunteer putUser(@Parameter(description = "Введите id волонтера", example = "1")
                             @PathVariable Long id,
                             @RequestBody Volunteer volunteer) {
        return volunteerService.editVolunteer(id, volunteer);
    }

    @Operation(
            summary = "Привязка животного к волонтеру",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Привязка животного к волонтеру",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    )
            }
    )
    @PatchMapping("{id}/animal")
    public Volunteer patchVolunteerAnimal(@Parameter(description = "Введите id волонтера", example = "1")
                                          @PathVariable Long id,
                                          @Parameter(description = "Введите id животного", example = "1")
                                          @RequestParam("animalId") Long animalId) {
        return volunteerService.patchVolunteerAnimal(id, animalId);
    }
}
