package ru.miphi.otpauthservicehw.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.miphi.otpauthservicehw.dto.request.GetUsersParamsRequest;
import ru.miphi.otpauthservicehw.dto.request.UpdateOtpConfigRequest;
import ru.miphi.otpauthservicehw.dto.response.GetUsersResponse;
import ru.miphi.otpauthservicehw.dto.response.OtpConfigResponse;
import ru.miphi.otpauthservicehw.service.DeleteUserService;
import ru.miphi.otpauthservicehw.service.GetUsersService;
import ru.miphi.otpauthservicehw.service.UpdateOtpConfigService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminController {

    public static final String BASE = "/admin";
    public static final String OTP_CONFIG = BASE + "/otp-config";
    public static final String USERS = BASE + "/users";
    public static final String DELETE_USER = USERS + "/{id}";

    UpdateOtpConfigService updateOtpConfigService;
    GetUsersService getUsersService;
    DeleteUserService deleteUserService;

    @PatchMapping(OTP_CONFIG)
    @Operation(summary = "изменение конфигурации otp-кодов")
    public ResponseEntity<OtpConfigResponse> updateOtpConfig(@RequestBody @Valid UpdateOtpConfigRequest request) {
        return ResponseEntity.ok(updateOtpConfigService.updateOtpConfig(request));
    }

    @GetMapping(USERS)
    @Operation(summary = "получение страницы пользователей")
    public ResponseEntity<GetUsersResponse> getUsers(@ModelAttribute @Valid GetUsersParamsRequest request) {
        return ResponseEntity.ok(getUsersService.getUsers(request));
    }

    @DeleteMapping(DELETE_USER)
    @Operation(summary = "удаление пользователя по ID")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        deleteUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
