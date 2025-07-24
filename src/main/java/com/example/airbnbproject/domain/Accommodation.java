package com.example.airbnbproject.domain;

import com.example.airbnbproject.dto.AccommodationRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ACCOMMODATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, length = 30)
    private String view;

    @Column(nullable = false, length = 1000)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private AccommodationInfo accommodationInfo;

    public static Accommodation of(AccommodationRequestDto dto, User user) {
        Accommodation a = new Accommodation();
        a.setName(dto.getName());
        a.setPrice(dto.getPrice());
        a.setView(dto.getView());
        a.setImage(dto.getImage());
        a.setUser(user);
        return a;
    }
}
