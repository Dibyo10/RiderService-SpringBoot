package com.example.RiderService.repositories;

import com.example.RiderService.models.Rider;
import com.example.RiderService.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository <Rider, Long>{

    Optional<Rider> findByUser(User user);
}
