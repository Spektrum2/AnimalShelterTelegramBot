package com.example.animalsheltertelegrambot.controller;
import com.example.animalsheltertelegrambot.model.Animal;
import com.example.animalsheltertelegrambot.record.AnimalRecord;
import com.example.animalsheltertelegrambot.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/animal")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Operation(
            summary = "Вывод всех животных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Вывод всех животных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                            )
                    )
            }
    )
    @GetMapping
    public Collection<AnimalRecord> getAllUsers() {
        return animalService.getAllAnimal();
    }

    @Operation(
            summary = "Поиск животного по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поиск животного по id",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            }
    )
    @GetMapping("{id}")
    public AnimalRecord findAnimal(@Parameter(description = "Введите id животного", example = "1")
                             @PathVariable Long id) {
        return animalService.findAnimal(id);
    }

    @Operation(
            summary = "Запись животного в БД",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запись животного в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            }
    )
    @PostMapping
    public AnimalRecord postAnimal(@RequestBody @Valid AnimalRecord animalRecord) {
        return animalService.createAnimal(animalRecord);
    }

    @Operation(
            summary = "Удаление животного",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаление животного",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            }
    )
    @DeleteMapping("{id}")
    public Animal deleteAnimal(@Parameter(description = "Введите id животного", example = "1")
                               @PathVariable Long id) {
        return animalService.deleteAnimal(id);
    }

    @Operation(
            summary = "Изменение животного",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изменение животного",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            }
    )
    @PutMapping("{id}")
    public AnimalRecord putUser(@Parameter(description = "Введите id животного", example = "1")
                          @PathVariable Long id,
                          @RequestBody @Valid AnimalRecord animalRecord) {
        return animalService.editAnimal(id, animalRecord);
    }
}
