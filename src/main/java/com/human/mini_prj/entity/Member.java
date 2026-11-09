package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_id", unique = true, length = 50)
    private String userId;

    private String name;

    private String phone;

    private String pwd;

    private String email;

    private String image;

    private LocalDateTime regDate;
}