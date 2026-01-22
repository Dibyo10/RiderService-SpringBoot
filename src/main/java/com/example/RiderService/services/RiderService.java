package com.example.RiderService.services;

import com.example.RiderService.dto.CreateRiderRequest;
import com.example.RiderService.models.Rider;
import com.example.RiderService.models.User;
import com.example.RiderService.repositories.RiderRepository;
import com.example.RiderService.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final UserRepository userRepository;

    public Optional<Rider> getRiderByRiderId(Long riderId)
    {
        return riderRepository.findById(riderId);
    }

    public Rider createRiderProfile(CreateRiderRequest request){

        User user=userRepository.findById(request.getUserId()).orElseThrow(()->new RuntimeException("User Not Found"));
        Rider rider = new Rider();
        rider.setUser(user);
        rider.setDl(request.getDl());

        return riderRepository.save(rider);



    }



}
