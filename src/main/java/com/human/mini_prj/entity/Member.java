package com.human.mini_prj.entity;

import com.human.mini_prj.constant.Provider;
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

    @Column(length = 100)
    private String name;
    @Column(length = 100)
    private String phone;
    @Column(nullable = true, length = 255)
    private String pwd;
    @Column(unique = true,length = 150)
    private String email;
    @Column(length = 255)
    private String image;

    private LocalDateTime regDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Authority authority;

    //제공자 추가
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Provider provider;

    @Column(length = 100)
    private String providerId;  // 카카오 회원 번호 (카카오 회원 식별)

    @PrePersist // DB에 INSERT 되기 전에 실행되는 메소드
    public void prePersist() {
        this.regDate = LocalDateTime.now(); // 현재 날짜 및 시간으로 초기화
        this.authority = Authority.ROLE_USER;
        if (this.provider == null) this.provider = Provider.LOCAL; // 일반 가입은 LOCAL
    }
}