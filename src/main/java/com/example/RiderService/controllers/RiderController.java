package com.example.RiderService.controllers;

import com.example.RiderService.dto.CreateRiderRequest;
import com.example.RiderService.models.Rider;
import com.example.RiderService.models.User;
import com.example.RiderService.repositories.RiderRepository;
import com.example.RiderService.repositories.UserRepository;
import com.example.RiderService.services.RiderService;
import com.example.RiderService.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/riders")
@Tag(name = "Riders", description = "Rider onboarding and lifecycle APIs")
public class RiderController {

    private final UserService userService;
    private RiderService riderService;
    private UserRepository userRepository;
    private RiderRepository riderRepository;



    @GetMapping("/by-user/{id}")
    @Operation(summary = "Get rider profile by user ID")
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
            description = "Converts an existing user into a rider"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rider created"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> createRiderProfile(@Valid @RequestBody CreateRiderRequest request){
        User user=userService.getUser(request.getUserId()).orElseThrow(()->new RuntimeException("User Not Found"));

        Rider rider = new Rider();
        rider.setUser(user);
        rider.setDl(request.getDl());

        riderRepository.save(rider);
        return new ResponseEntity<>(rider,HttpStatus.CREATED);

    }

}
