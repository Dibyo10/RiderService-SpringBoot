package com.example.RiderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response to get available riders")
public class AvailableRiderResponse {


    private Long riderId;
    private Double rating;
}
