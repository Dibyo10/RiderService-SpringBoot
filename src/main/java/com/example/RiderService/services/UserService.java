package com.example.RiderService.services;


import com.example.RiderService.models.User;
import com.example.RiderService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
    public User createUser(User user){
        return userRepository.save(user);
    }




}
