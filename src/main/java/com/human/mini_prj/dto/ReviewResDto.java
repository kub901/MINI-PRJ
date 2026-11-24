
package com.human.mini_prj.dto;

import com.human.mini_prj.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResDto {

    private Long id;
    private Integer rating;
    private String content;
    private LocalDateTime regDate;
    private String productTitle;

    public ReviewResDto(Review review, String productTitle) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.regDate = review.getRegDate();
        this.productTitle = productTitle;
    }
}