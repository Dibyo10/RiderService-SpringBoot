package com.example.RiderService.services;

import com.example.RiderService.dto.CreateRiderRequest;
import com.example.RiderService.models.Rider;
import com.example.RiderService.models.RiderStatus;
import com.example.RiderService.models.User;
import com.example.RiderService.repositories.RiderRepository;
import com.example.RiderService.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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


        riderRepository.save(rider);

        return rider;

    }

    public boolean isValidTransition(RiderStatus current, RiderStatus after){
        return switch (current){
            case OFFLINE -> after == RiderStatus.ONLINE;
            case ONLINE -> after == RiderStatus.BUSY || after == RiderStatus.OFFLINE;
            case BUSY -> after == RiderStatus.ONLINE;
        };
    }

    public Rider updateStatus(Long riderId, RiderStatus newStatus) {

        Rider rider = riderRepository.findById(riderId).orElseThrow(() -> new RuntimeException("Rider not found"));



        RiderStatus current = rider.getStatus();

        if (!isValidTransition(current, newStatus)) {
            throw new RuntimeException(
                    "Invalid transition: " + current + " → " + newStatus
            );
        }

        rider.setStatus(newStatus);
        return riderRepository.save(rider);
    }

    public List<Rider> getAvailableRiders(){
        return riderRepository.findByStatus(RiderStatus.ONLINE);
    }






}
