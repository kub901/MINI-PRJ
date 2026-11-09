import React, { useState } from "react";
import styled from "styled-components";
import Star from "../../assets/star.png";
import yellowStar from "../../assets/yellostar.png";

export default function ReviewStars() {
  const [clicked, setClicked] = useState([false, false, false, false, false]);
  const [content, setContent] = useState("");

  const array = [0, 1, 2, 3, 4];
  const clickedStarNum = clicked.filter(Boolean).length;

  const starScore = (index) => {
    setClicked(clicked.map((_, i) => i <= index));
  };

  const handleSubmit = () => {
    if (clickedStarNum === 0) {
      alert("별점을 선택해 주세요.");
      return;
    }

    if (!content.trim()) {
      alert("리뷰 내용을 입력해 주세요.");
      return;
    }

    alert("리뷰 등록 기능은 백엔드 API 연결 후 사용할 수 있습니다.");
  };

  const handleCancel = () => {
    setClicked([false, false, false, false, false]);
    setContent("");
  };

  return (
    <ReviewContainer>
      <Title>리뷰 작성하기</Title>

      <Label>별점 선택 (클릭하여 선택)</Label>

      <RatingRow>
        <StarsWrap>
          {array.map((index) => (
            <img
              key={index}
              onClick={() => starScore(index)}
              src={clicked[index] ? yellowStar : Star}
              alt={`${index + 1}점`}
            />
          ))}
        </StarsWrap>

        <StarNum>{clickedStarNum}/5점</StarNum>
      </RatingRow>

      <ReviewInput
        placeholder="리뷰 내용을 작성해주세요."
        value={content}
        onChange={(e) => setContent(e.target.value)}
      />

      <ButtonRow>
        <SubmitButton onClick={handleSubmit}>리뷰 등록</SubmitButton>

        <CancelButton onClick={handleCancel}>취소</CancelButton>
      </ButtonRow>
    </ReviewContainer>
  );
}

const ReviewContainer = styled.div`
  width: 480px;
  max-width: calc(100%-40px); // 화면밖으로 나가는거 방지용
  margin: 30px auto;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px #000;
`;

const Title = styled.h2`
  text-align: center;
  font-size: 24px;
  margin: 0 0 28px;
`;

const Label = styled.div`
  font-size: 15px;
  margin-bottom: 10px;
`;

const RatingRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
`;

const StarsWrap = styled.div`
  display: flex;
  gap: 5px;

  img {
    width: 25px;
    height: 25px;
    cursor: pointer;
  }
`;

const StarNum = styled.div`
  font-size: 15px;
  color: #26282b;
`;

const ReviewInput = styled.textarea`
  box-sizing: border-box;
  width: 100%;
  height: 120px;
  padding: 12px;
  border: 2px dashed #aaa;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;

  &:focus {
    outline: none;
    border-color: #648bd8;
  }
`;

const ButtonRow = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
`;

const SubmitButton = styled.button`
  border: none;
  border-radius: 20px;
  padding: 10px 18px;
  background: #527fd0;
  color: white;
  cursor: pointer;
`;

const CancelButton = styled.button`
  border: 1px solid #bbb;
  border-radius: 20px;
  padding: 10px 18px;
  background: white;
  color: #5555;
  cursor: pointer;
`;
