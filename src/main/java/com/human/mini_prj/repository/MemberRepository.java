package com.human.mini_prj.repository;

import com.human.mini_prj.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.human.mini_prj.constant.Provider;

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





    Optional<Member> findByUserIdAndPwd(
            String userId, String pwd
    );
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

}