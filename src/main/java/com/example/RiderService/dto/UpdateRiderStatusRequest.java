package com.example.RiderService.dto;


import com.example.RiderService.models.RiderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to update a rider status, just pass an ENUM")
public class UpdateRiderStatusRequest {

    @NotNull
    private RiderStatus status;

}
