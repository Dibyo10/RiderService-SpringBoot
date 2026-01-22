package com.example.RiderService.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "rider")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    private Double rating;


    @Lob
    @Column(name = "dl")
    private byte[] dl;

}
