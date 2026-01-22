package com.example.RiderService.controllers;


import com.example.RiderService.dto.CreateUserRequest;
import com.example.RiderService.models.User;
import com.example.RiderService.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User registration and retrieval APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Fetches a user by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUser(@PathVariable Long id){
        Optional<User> user=userService.getUser(id);

        if(user.isPresent()){
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("User Not Found",HttpStatus.NOT_FOUND);

    }

    @PostMapping("/user")
    @Operation(
            summary = "Create a new user",
            description = "Registers a new user in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request){
        User user= userService.createUser(request);
        return new ResponseEntity<>(user,HttpStatus.CREATED);
    }


}
