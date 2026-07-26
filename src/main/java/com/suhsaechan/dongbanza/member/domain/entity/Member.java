package com.suhsaechan.dongbanza.member.domain.entity;

import com.suhsaechan.dongbanza.common.entity.BaseEntity;
import com.suhsaechan.dongbanza.member.domain.constants.MemberRole;
import com.suhsaechan.dongbanza.member.domain.constants.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString(exclude = "password")
@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  public static final String DEFAULT_CHARACTER_NAME = "주인공";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String password;

  // @Builder.Default가 없으면 Lombok이 초기값을 무시해 빌더 경유 시 null이 저장된다
  @Builder.Default
  private String characterName = DEFAULT_CHARACTER_NAME;

  @Enumerated(EnumType.STRING)
  private MemberRole role;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  @Setter
  private String profileImageUrl;

  private LocalDate birthDate;

  private String gender;

  private String mbti;

  @Builder.Default
  private Integer totalRegressionCount = 0;

  @Builder.Default
  private Integer totalScore = 0;

  private String gameProgress;

  public void increaseRegressionCount() {
    if (this.totalRegressionCount == null) {
      this.totalRegressionCount = 0;
    }
    this.totalRegressionCount += 1;
  }

  public void updateScore(int value) {
    if (this.totalScore == null) {
      this.totalScore = 0;
    }
    this.totalScore += value;
  }

  public void updateGameProgress(String progress) {
    this.gameProgress = progress;
  }
}
