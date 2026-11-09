package com.human.mini_prj.repository;

import com.human.mini_prj.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// interface 에서 생성해서 쿼리문을 hibernate에서 생성
@Repository
public interface MemberRepository extends JpaRepository <Member,Long> {

    // 기본적인 CRUD는 상속을 통해서 만들어졌고, 이를 구현하는 구현체는 별도의 hibernate가 SQL 문으로 변경 해줌
    Optional<Member> findByEmail(String email); // Sql : select * from member where email = ?
    // 하나의 레코드를 반환받으면 맴버 레코드로
    // 옵셔널을 거는 이유는 랩핑, 없으면 널 입세션e
    boolean existsByEmail(String email); // sql: select cont (*) from member where email =?

    boolean existsByUserId(String userId);
    // 카멜 표기법으로 소문자 , 오타 구분
    Optional<Member> findByEmailAndPwd(String email,String pwd); // Sql : select * from member where email = ? and pwd=?
    // Repository 구현체는 모두 hibernate가 만들어내는거
    Optional<Member> findByUserId(String userId);
    // 1. 이름이 정확히 일치하는 회원 목록 조회


    List<Member> findByName(String name);

    // 2. 이름에 특정 글자가 포함된 회원 조회
    List<Member> findByNameContaining(String name);

    // 3. 특정 도메인(예:@gmail.com) 으로 끝나는 이메일을 가진 회원 조회
    List<Member> findByEmailContaining(String email);
    // 4. 이름 또는 이메일이 일치하는 회원 조회
    List<Member> findByNameOrEmail(String name, String email);
    // 5. 특정 일시 이후에 가입한 회원 조회
    List<Member> findByRegDateAfter(LocalDateTime regDate);
    // 6. 두 일시 사이에 가입한 회원 조회
    List<Member> findByRegDateBetween(
            LocalDateTime startDate,        // 내 Member 엔티티에는 startDate가 없고 regDate가 있어서 StartDateBetween이 아니라 그냥 findByRegDateBetween 임.
            LocalDateTime endDate
    );
    // 7. 전체 회원을 가입일 최신순으로 조회
    List<Member> findAllByOrderByRegDateDesc();
    // 8. 가장 최근에 가입한 회원 3명만 조회
    List<Member> findTop3ByOrderByRegDateDesc();          // findTop3 뒤에 By를 붙여야함
    // 9. 대소무낮 구분 없이 이름에 키워드가 포함된 회원을 오름차순으로 조회
    List<Member> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    // 10. 이메일로 회원 삭제
    void deleteByEmail(String email);


    Optional<Member> findByUserIdAndPwd(
            String userId, String pwd
    );

}