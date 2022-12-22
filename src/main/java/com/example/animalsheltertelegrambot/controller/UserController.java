package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.service.UserService;
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
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    @GetMapping
    public Collection<UserData> getAllUsers() {
        return userService.getAllUsers();
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
    @GetMapping("{id}")
    public UserData findUser(@Parameter(description = "Введите id пользователя", example = "1")
                             @PathVariable Long id) {
        return userService.findUser(id);
    }

    @Operation(
            summary = "Запись пользователя в БД",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запись пользователя в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @PostMapping
    public UserData postUser(@RequestBody UserData userData) {
        return userService.createUser(userData);
    }

    @Operation(
            summary = "Удаление пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаление пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @DeleteMapping("{id}")
    public UserData deleteUser(@Parameter(description = "Введите id пользователя", example = "1")
                               @PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @Operation(
            summary = "Изменение пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изменение пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    )
            }
    )
    @PutMapping("{id}")
    public UserData putUser(@Parameter(description = "Введите id пользователя", example = "1")
                            @PathVariable Long id,
                            @RequestBody UserData userData) {
        return userService.editUser(id, userData);
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
    public Collection<Report> findReportsByUser(@Parameter(description = "Введите id пользователя", example = "1")
                                                @PathVariable Long id) {
        return userService.findReportsByUser(id);
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
    @PatchMapping("{id}/animal")
    public UserData patchUserAnimal(@Parameter(description = "Введите id пользователя", example = "1")
                                    @PathVariable Long id,
                                    @Parameter(description = "Введите id животного", example = "1")
                                    @RequestParam("animalId") Long animalId) {
        return userService.patchUserAnimal(id, animalId);
    }
}
