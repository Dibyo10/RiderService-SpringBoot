package com.example.RiderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Schema(description = "Response containing last-known rider location")
@NoArgsConstructor
public class RiderLocationResponse {

    @Schema(description = "Rider ID", example = "12")
    private Long riderId;

    @Schema(description = "Latitude", example = "12.9352")
    private Double latitude;

    @Schema(description = "Longitude", example = "77.6245")
    private Double longitude;

    @Schema(
            description = "Epoch timestamp (milliseconds) when location was last updated",
            example = "1706100000000"
    )
    private long updatedAt;
}

