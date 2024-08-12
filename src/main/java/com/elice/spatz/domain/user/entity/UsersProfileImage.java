package com.elice.spatz.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class UsersProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    private String imageUrl;

    public UsersProfileImage(Users user, String imageUrl) {
        this.user = user;
        this.imageUrl = imageUrl;
    }

    public void changeImageUrl(String imgUrl) {
        this.imageUrl = imgUrl;
    }
}
