package com.human.mini_prj.repository;

import com.human.mini_prj.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
// 예매 정보를 db에 저장하고 조회하는 rep
public interface ReservationRepository extends JpaRepository<Reservation, Long> {



}