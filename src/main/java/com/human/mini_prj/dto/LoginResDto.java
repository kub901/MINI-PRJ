package com.human.mini_prj.dto;

import com.human.mini_prj.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class LoginResDto {
    private Long memberId;
    private String userId;
    private String name;
    private String phone;
    private String email;

    public LoginResDto(Member member) {
        this.memberId = member.getId();
        this.userId = member.getUserId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.email = member.getEmail();
    }
}