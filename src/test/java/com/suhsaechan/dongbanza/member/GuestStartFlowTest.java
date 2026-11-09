package com.suhsaechan.dongbanza.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** 가입·로그인 없이 시작하는 흐름 전체 검증: 시작 > 토큰 > 내 정보 > 게임 결과 > 랭킹 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GuestStartFlowTest {

  @Autowired
  private MockMvc mockMvc;

  private MvcResult startAsGuest(String requestBody) throws Exception {
    return mockMvc.perform(post("/api/guest/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  @DisplayName("인증 없이 닉네임 추천을 받을 수 있다")
  void suggestsNicknameWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/guest/nickname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.characterName").isNotEmpty());
  }

  @Test
  @DisplayName("닉네임을 생략하고 MBTI만 보내면 자동 닉네임으로 시작되고 토큰이 발급된다")
  void startsWithGeneratedNickname() throws Exception {
    mockMvc.perform(post("/api/guest/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"mbti\":\"ENFP\"}"))
        .andExpect(status().isOk())
        .andExpect(header().string("Authorization", startsWith("Bearer ")))
        .andExpect(jsonPath("$.characterName").isNotEmpty())
        .andExpect(jsonPath("$.mbti").value("ENFP"))
        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
        // 액세스 토큰은 헤더로만 내려가고 body에는 없다
        .andExpect(jsonPath("$.accessToken").doesNotExist());
  }

  @Test
  @DisplayName("발급된 액세스 토큰과 리프레시 토큰 모두로 내 정보를 조회할 수 있다 (프론트는 리프레시 토큰을 보낸다)")
  void readsMyPageWithEitherToken() throws Exception {
    MvcResult started = startAsGuest("{\"characterName\":\"베이글\",\"mbti\":\"INTJ\"}");
    String accessHeader = started.getResponse().getHeader("Authorization");
    String refreshToken = JsonPath.read(started.getResponse().getContentAsString(), "$.refreshToken");

    mockMvc.perform(get("/api/my-page").header("Authorization", accessHeader))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.characterName").value("베이글"))
        .andExpect(jsonPath("$.mbti").value("INTJ"))
        .andExpect(jsonPath("$.totalScore").value(0))
        // 제거된 개인정보 필드는 응답에 없다
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.birthDate").doesNotExist())
        .andExpect(jsonPath("$.gender").doesNotExist());

    mockMvc.perform(get("/api/my-page").header("Authorization", "Bearer " + refreshToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.characterName").value("베이글"));
  }

  @Test
  @DisplayName("토큰 없이도 랭킹을 조회할 수 있다 (시작 전 홈 화면 노출)")
  void allowsRankingWithoutToken() throws Exception {
    mockMvc.perform(get("/api/game/ranking?page=0&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rankings").isArray());
  }

  @Test
  @DisplayName("토큰 없이는 내 정보에 접근할 수 없다")
  void rejectsMyPageWithoutToken() throws Exception {
    mockMvc.perform(get("/api/my-page"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("MBTI 형식이 틀리거나 닉네임 규칙을 어기면 400으로 거부한다")
  void rejectsInvalidInput() throws Exception {
    mockMvc.perform(post("/api/guest/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"mbti\":\"enfp\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/guest/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"characterName\":\"<script>\",\"mbti\":\"ENFP\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/guest/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("기존 로그인·회원가입 엔드포인트는 더 이상 열려 있지 않다")
  void removedLoginAndSignupAreNotPermitted() throws Exception {
    mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().is4xxClientError());
    mockMvc.perform(post("/api/signup").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("게임 결과를 저장하면 랭킹에 닉네임·MBTI·결과 id가 노출되고 이메일은 없다")
  void rankingExposesNicknameWithoutEmail() throws Exception {
    MvcResult started = startAsGuest("{\"characterName\":\"랭커\",\"mbti\":\"ESTP\"}");
    String bearer = started.getResponse().getHeader("Authorization");

    mockMvc.perform(post("/api/game/over")
            .header("Authorization", bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"finalScore\":99999,\"success\":true,\"details\":\"{}\",\"gamePlaySeconds\":120}"))
        .andExpect(status().isCreated());

    MvcResult ranking = mockMvc.perform(get("/api/game/ranking?page=0&size=5")
            .header("Authorization", bearer))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rankings[0].characterName").value("랭커"))
        .andExpect(jsonPath("$.rankings[0].mbti").value("ESTP"))
        .andExpect(jsonPath("$.rankings[0].gameResultId").isNumber())
        .andExpect(jsonPath("$.rankings[0].memberEmail").doesNotExist())
        .andReturn();

    assertThat(ranking.getResponse().getContentAsString()).doesNotContain("@");
  }
}
