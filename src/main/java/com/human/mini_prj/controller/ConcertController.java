package com.human.mini_prj.controller;

import lombok.RequiredArgsConstructor;
import com.human.mini_prj.entity.Concert; // 본인의 entity 패키지 경로에 맞게 확인
import com.human.mini_prj.service.ConcertService; // service 패키지 경로
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concerts") // 프론트엔드 요청 주소와 매칭
@CrossOrigin(origins = "http://localhost:3000") // CORS 허용 (프론트 포트에 맞게 조정)
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    // GET 방식: /concerts/{id} 요청 처리
    @GetMapping("/{id}")
    public ResponseEntity<Concert> getReservationDetail(@PathVariable Long id) {
        Concert concert = concertService.getReservationDetail(id);
        return ResponseEntity.ok(concert); // 200 OK와 함께 데이터 전송
    }

    @GetMapping("") // 또는 /concerts
    public ResponseEntity<List<Concert>> getAllConcerts() {
        List<Concert> list = concertService.getAllConcerts(); // 서비스에서 findAll() 호출
        return ResponseEntity.ok(list);
    }
}