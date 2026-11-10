package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.human.mini_prj.constant.Authority;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Authority authority;

    @PrePersist // DB에 INSERT 되기 전에 실행되는 메소드
    public void prePersist() {
        this.regDate = LocalDateTime.now(); // 현재 날짜 및 시간으로 초기화
        this.authority = Authority.ROLE_USER;
    }
}