package com.human.mini_prj.service;

import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public Concert getReservationDetail(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트가 존재하지 않습니다. id=" + id));
    }

    // 전체 콘서트 목록 조회
    public List<Concert> getAllConcerts() {
        return concertRepository.findAll();
    }
}