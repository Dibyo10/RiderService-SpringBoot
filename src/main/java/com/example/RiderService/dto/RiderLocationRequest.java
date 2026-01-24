package com.example.RiderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Schema(description = "Request to update a rider location")
@NoArgsConstructor
public class RiderLocationRequest {

    @Schema(example = "12.9715987", description = "Latitude of the rider")
    @NotNull
    private double latitude;
    @Schema(example = "77.5945622", description = "Longitude of the rider")
    @NotNull
    private double longitude;
}
