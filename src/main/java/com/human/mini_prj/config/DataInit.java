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
            if (concertRepository.count() == 0) {
                String[] categories = {"뮤지컬", "콘서트", "클래식", "연극", "내한공연"};
                String[] locations = {
                        "예술의전당 오페라극장", "세종문화회관 대극장", "블루스퀘어 신한카드홀",
                        "LG아트센터 서울", "잠실실내체육관", "대학로 소극장 혜화", "올림픽공원 체조경기장"
                };
                String[] posters = {
                        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1526478806334-5fd488fcaabc?auto=format&fit=crop&w=800&q=80"
                };

                // 40개의 공연에 각각 고유한 개성을 부여하기 위한 재료 배열들
                String[] themes = {
                        "달빛 세레나데", "붉은 성채", "미드나잇 재즈", "시간의 시계탑",
                        "겨울 숲", "네온사인 락", "비밀의 서점", "황금빛 무도회",
                        "푸른 바다의 전설", "마법사의 공방", "별빛 소나타", "철길 위의 연인들",
                        "도시의 유령", "마지막 1분", "체스판의 왕", "바람의 노래",
                        "거울 속 미로", "은빛 늑대", "여름날의 꿈", "잊혀진 기억",
                        "가면 속의 진실", "폭풍의 언덕", "안개 낀 항구", "낙원의 경계",
                        "시간여행자", "사막의 장미", "크리스탈 궁전", "유성우의 밤",
                        "체리블라썸 팝", "딥블루 오션", "스칼렛 탱고", "코스믹 오디세이",
                        "벨벳 루머", "선셋 그루브", "팬텀 에피소드", "오로라 헌터",
                        "파리의 공기", "뉴욕의 빗소리", "런던의 안개", "서울의 야경"
                };

                for (int i = 0; i < 40; i++) {
                    Concert concert = new Concert();
                    String category = categories[i % categories.length];
                    String theme = themes[i]; // 40개 각각 고유한 테마 이름 매칭

                    concert.setTitle(category + " <" + theme + ">");
                    concert.setPrice(60000 + (i * 1500));
                    concert.setPosterUrl(posters[i % posters.length]);
                    concert.setNotice((i + 1) + "회차 안내사항: 티켓 수령 시 예매번호와 신분증 필수 지참.");
                    concert.setCasting("출연진 " + (i + 1) + "팀: 독보적인 매력을 지닌 실력파 아티스트 및 뮤지컬 배우진");

                    // 인덱스 i를 활용해 40개 모두 완전히 다른 텍스트(설명, 줄거리, 분위기) 생성
                    concert.setDetail(
                            theme + " 테마로 진행되는 본 공연은 " + category + " 장르의 진수를 보여줍니다. " +
                                    "총 러닝타임 150분 동안 관객들에게 잊지 못할 시각적, 청각적 몰입감을 선사하며, " +
                                    "특히 " + (i + 1) + "번째 시즌만을 위해 편곡된 시그니처 넘버들이 하이라이트입니다."
                    );

                    concert.setStory(
                            "어느 평화로운 도시에서 시작된 의문의 사건을 계기로, 주인공이 " + theme + "의 숨겨진 진실을 찾아 떠나는 여정을 그린 서사입니다. " +
                                    "예상치 못한 반들과 깊은 감정선이 어우러져 극의 몰입도를 극대화하며, " +
                                    "마지막 장면에 등장하는 주인공의 독백은 긴 여운을 남깁니다."
                    );

                    concert.setMood(
                            (i % 2 == 0 ? "로맨틱한, 웅장한, " : "신나는, 몽환적인, ") +
                                    (i % 3 == 0 ? "감성적인, 여운이 남는, " : "역동적인, 몰입감 있는, ") +
                                    "테마_" + theme + ", " + category + "추천"
                    );

                    concert.setPriceInfo("VIP석 " + (90000 + (i * 1000)) + "원");
                    concert.setGuide("관람 연령: 8세 이상 관람가 / 러닝타임: 150분 (인터미션 15분 포함)");
                    concert.setRefund("관람일 7일 전까지 취소 수수료 면제");
                    concert.setLocation(locations[i % locations.length]);

                    concertRepository.save(concert);
                }
                System.out.println(">>> [DataInit] 40개의 완벽히 고유한 RAG용 콘서트 더미 데이터 생성 완료! <<<");
            }
        };
    }
}