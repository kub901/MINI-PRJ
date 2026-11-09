package com.human.mini_prj.config;

import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInit {

    private final ConcertRepository concertRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 콘서트(상품) 더미 데이터 40개 생성
            if (concertRepository.count() == 0) {
                String[] categories = {"뮤지컬", "콘서트", "클래식", "연극", "내한공연"};
                String[] locations = {"예술의전당 오페라극장", "세종문화회관 대극장", "블루스퀘어 신한카드홀", "LG아트센터 서울", "잠실실내체육관"};
                String[] posters = {
                        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1526478806334-5fd488fcaabc?auto=format&fit=crop&w=800&q=80"
                };

                for (int i = 1; i <= 40; i++) {
                    Concert concert = new Concert();
                    String category = categories[i % categories.length];
                    concert.setTitle(category + " <스타라이트 페스티벌 Vol." + i + ">");
                    concert.setPrice(70000 + (i * 2000));
                    concert.setPosterUrl(posters[i % posters.length]);
                    concert.setNotice("공연 " + i + "회차 안내사항: 관람객 입장 시 티켓 및 신분증을 지참해 주세요.");
                    concert.setCasting("출연진 " + i + "팀: 대한민국 최고의 실력파 뮤지컬 배우 및 아티스트 군단");
                    concert.setDetail("전석 매진 신화에 빛나는 바로 그 공연! " + i + "번째 시즌으로 더욱 완벽해진 무대를 만나보세요.");
                    concert.setPriceInfo("VIP석 " + (70000 + (i * 2000)) + "원 / R석 110,000원 / S석 80,000원");
                    concert.setGuide("관람 연령: 중학생 이상 관람가 / 러닝타임: 160분 (인터미션 15분 포함)");
                    concert.setRefund("관람일 7일 전까지 취소 수수료 면제, 이후 규정에 따른 수수료 부과");
                    concert.setLocation(locations[i % locations.length]);

                    concertRepository.save(concert);
                }
                System.out.println(">>> [DataInit] 콘서트/상품 더미 데이터 40건 자동 생성 완료! <<<");
            }
        };
    }
}