package com.human.mini_prj.service;

import com.human.mini_prj.entity.Member;
import com.human.mini_prj.repository.MemberRepository;
import com.human.mini_prj.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void withdraw(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.isWithdrawn()) {
            throw new IllegalStateException("이미 탈퇴 처리된 계정입니다.");
        }

        if (reservationRepository.existsActiveReservationByMember(member)) {
            throw new IllegalStateException("진행 중인 예매 내역이 있어 탈퇴할 수 없습니다.");
        }

        member.withdraw();
    }
}