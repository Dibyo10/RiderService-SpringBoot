package com.example.RiderService.controllers;

import com.example.RiderService.dto.*;
import com.example.RiderService.models.Rider;
import com.example.RiderService.models.RiderStatus;
import com.example.RiderService.repositories.RiderRepository;
import com.example.RiderService.services.RiderLocationService;
import com.example.RiderService.services.RiderService;
import com.example.RiderService.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/riders")
@Tag(name = "Riders", description = "Rider onboarding and lifecycle APIs")
public class RiderController {

    @Autowired
    private final UserService userService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RiderLocationService locationService;




    @GetMapping("/by-user/{id}")
    @Operation(
            summary = "Get rider profile by user ID",
            description = "Fetches the rider profile associated with a given user ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rider found"),
            @ApiResponse(responseCode = "404", description = "Rider not found for user")
    })
    public ResponseEntity<?> getRiderProfile(@PathVariable Long id){

        Optional<Rider> rider= riderService.getRiderByRiderId(id);
        if(rider.isPresent()){
            return new ResponseEntity<>(rider, HttpStatus.OK);
        }
        return new ResponseEntity<>("User Not Found",HttpStatus.NOT_FOUND);

    }

    @PostMapping("/rider-profile")
    @Operation(
            summary = "Create rider profile",
            description = "Converts an existing user into a rider profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rider created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User already a rider")
    })
    public ResponseEntity<?> createRiderProfile(@Valid @RequestBody CreateRiderRequest request){
        userService.getUser(request.getUserId()).orElseThrow(()->new RuntimeException("User Not Found"));

        Rider rider=riderService.createRiderProfile(request);

        riderRepository.save(rider);
        return new ResponseEntity<>(rider,HttpStatus.CREATED);

    }
    @PutMapping("/{riderId}/status")
    @Operation(
            summary = "Update rider availability status",
            description = "Updates rider status (ONLINE, OFFLINE, BUSY) with valid state transitions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Rider not found")
    })
    public ResponseEntity<?> updateRiderStatus(@PathVariable Long riderId , @Valid @RequestBody UpdateRiderStatusRequest request){
        Rider rider=riderService.updateStatus(riderId,request.getStatus());
        return new ResponseEntity<>(rider,HttpStatus.OK);
    }

    @GetMapping("/available")
    @Operation(
            summary = "Get available riders",
            description = "Returns a list of riders currently available for order assignment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available riders fetched")
    })
    public ResponseEntity<List<AvailableRiderResponse>> getAvailableRiders() {

        List<AvailableRiderResponse> response = riderService.getAvailableRiders().stream().map(r ->
                {AvailableRiderResponse dto = new AvailableRiderResponse();
                    dto.setRiderId(r.getId());
                    dto.setRating(r.getRating());
                    return dto;
                        }).toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{riderId}/location")
    @Operation(
            summary = "Update rider current location",
            description = "Called periodically by rider client to update last-known location. Location is stored in Redis with TTL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location updated"),
            @ApiResponse(responseCode = "404", description = "Rider not found"),
            @ApiResponse(responseCode = "409", description = "Rider is offline")
    })
    public ResponseEntity<?> updateLocation(@PathVariable Long riderId, @Valid @RequestBody RiderLocationRequest request){

        Rider rider = riderRepository.findById(riderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (rider.getStatus() == RiderStatus.OFFLINE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Rider is offline");
        }


        locationService.updateLocation(riderId, request.getLatitude(), request.getLongitude());
        return new ResponseEntity<>(HttpStatus.OK);



    }

    @GetMapping("/{riderId}/location")
    @Operation(
            summary = "Get rider current location",
            description = "Returns last-known rider location from Redis"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Location found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RiderLocationResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Location stale or unavailable")
    })
    public ResponseEntity<RiderLocationResponse> getLocation(@PathVariable Long riderId) {

        return locationService.getLocation(riderId).map(ResponseEntity::ok).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location stale or unavailable"));

    }



}
