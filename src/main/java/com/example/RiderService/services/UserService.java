package com.example.RiderService.services;


import com.example.RiderService.dto.CreateUserRequest;
import com.example.RiderService.models.User;
import com.example.RiderService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateUserRequest request;




    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
    public User createUser(CreateUserRequest request){
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        return user;
    }




}
