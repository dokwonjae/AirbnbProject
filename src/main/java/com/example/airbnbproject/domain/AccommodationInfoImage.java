package com.example.airbnbproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationInfoImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(length = 1000)
    private String imageUrl; // URL 또는 null

    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_info_id")
    private AccommodationInfo accommodationInfo;
}
