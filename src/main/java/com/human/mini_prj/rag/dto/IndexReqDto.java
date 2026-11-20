
package com.human.mini_prj.rag.dto;

// record는 불변 데이터 전달 객체
// 모든 필드는 자동으로 private final
// 생성자, 접근메서드 등등이 자동으로 생성 됨, 보일러 플레이트 방지
// Spring -> Fast API 색인 요청
public record IndexReqDto(
        Long concertId,
        String title,
        Integer price,
        String location,
        String notice,
        String casting,
        String detail,
        String priceInfo,
        String guide,
        String refund,
        String story,
        String mood

) {

}