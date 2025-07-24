package com.example.airbnbproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACCOMMODATIONINFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 50)
    private String location;

    @Column(length = 100)
    private String subTitle;

    @Column(length = 50)
    private String personnel;

    @Column(length = 255)
    private String amenities;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @OneToMany(mappedBy = "accommodationInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccommodationInfoImage> images = new ArrayList<>();
}
