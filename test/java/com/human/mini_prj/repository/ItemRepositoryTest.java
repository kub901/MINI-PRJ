
package com.human.mini_prj.repository;

import com.human.mini_prj.constant.ItemSellStatus;
import com.human.mini_prj.entity.Item;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@SpringBootTest
class itemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository; // 필드를 통한 의존성 투입

    @Test
    @DisplayName("상품 저장 테스트")       // 필드 이름
    public void createItemTest() {

        for (int i = 1; i <= 10; i++) {
            Item item = new Item();

            item.setItemNm("테스트 상품" + i);
            item.setPrice(1000 * i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            // 상품 저장 후 반환된 객체를 변수에 저장
            Item saveItem = itemRepository.save(item);

            log.info("Item 저장 : {}", saveItem);
        }
    }

    @Test
    @DisplayName("상품 조회 테스트")
    public void findByItemNumTest() {
        this.createItemTest(); //10 개 상품 생성
        // 상품명으로 검색
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품5");

        for (Item item : itemList) {
            log.info("상품 조회 테스트 : {}", item);
        }
    }
    @Test
    @DisplayName("상품명 OR 상세 설명으로 조회")
    void findByItemOrItemDetailTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트상품1", "테스트 상품 상세 설명 5");
        for (Item item : itemList) {
            log.info("상품명 OR 상세 설명으로 조회 : {} ", item);
        }
    }
    @Test
    @DisplayName("가격이 특정 값보다 작은 상품")
    void findByPriceLessThanTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByPriceLessThan(10000);
        for (Item item : itemList) {
            log.info("가격이 특정 값보다 작은 상품 : {} ", item);
        }
    }
    @Test
    @DisplayName("가격이 특정 값보다 작은 상품을 가격 내림차순으로")
    void findByPriceLessThanOrderByPriceDescTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10000);
        for (Item item : itemList) {
            log.info("가격이 특정 값보다 작은 상품을 가격 내림차순으로 : {}", item);
        }
    }
    @Test
    @DisplayName("가격 범위 검색")
    void findByPriceBetweenTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByPriceBetween(1000 , 5000);
        for(Item item: itemList) {
            log.info("가격 범위 검색 테스트: {}", item);
        }
    }
    @Test
    @DisplayName("상품명에 특정 문자열이 포함된 상품")
    void findByItemNmContainingTest() {
        this.createItemTest();
        List<Item> itemList =itemRepository.findByItemNmContaining("휴면교육센터");
        for( Item item : itemList) {
            log.info("상품명에 특정 문자열이 포함된 상품 테스트: {}", item);

        }
    }
    @Test
    @DisplayName("판매 상태로 검색")
    void  findByItemSellStatusTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemSellStatus(ItemSellStatus.SELL);
        for(Item item : itemList) {
            log.info("판매 상태로 검색 테스트: {}", item);
        }
    }
    @Test
    @DisplayName("판매 상태가 같고 가격이 특정 값 이상인 상품")
    void findByItemSellStatusAndPriceGreaterThanEqualTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemSellStatusAndPriceGreaterThanEqual(ItemSellStatus.SELL,5000);
        for(Item item: itemList) {
            log.info("판매 상태가 같고 가격이 특정 값 이상인 상품Test:{}", item);

        }
    }
    @Test
    @DisplayName("native query 조회")
    public  void nativeQueryTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemDetailLikeNative("테스트 상품 상세 설명1");
        for(Item item : itemList) {
            log.info("native query 조회 : {}", item);
        }
    }


}

