package com.human.mini_prj.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;      // 콘서트명
    private String posterUrl;  // 포스터 이미지
    private int price;         // 기본 가격

    // 👉 8개 섹션별로 컬럼을 각각 선언하기
    private String notice;     // 공지사항
    private String casting;    // 캐스팅 정보
    private String detail;     // 상품 상세
    private String priceInfo;  // 가격 및 할인 정보
    private String guide;      // 이용안내
    private String refund;     // 취소 및 환불 규정
    private String location;   // 장소 오시는 길
    // (리뷰의 경우 보통 리뷰 테이블을 따로 파서 연동합니다!)
}