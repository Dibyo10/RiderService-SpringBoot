package com.example.RiderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create a user")
public class CreateUserRequest {

    @NotBlank
    @Schema(example = "Vimal")
    private String name;

    @Email
    @Schema(example = "v@gmail.com")
    private String email;

    @NotBlank
    @Schema(example = "strongPassword123")
    private String password;

    @NotBlank
    @Schema(example = "7903632688")
    private String phoneNumber;
}
