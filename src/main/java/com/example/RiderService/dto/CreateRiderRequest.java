package com.example.RiderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "Request to create a rider")
public class CreateRiderRequest {

    @NotNull
    @Schema(
            description = "Existing user ID",
            example = "42"
    )
    private Long userId;

    @NotNull
    @Schema(
            description = "Base64 encoded driving license",
            example = "ZRDzKQ=="
    )
    private byte[] dl;
}
