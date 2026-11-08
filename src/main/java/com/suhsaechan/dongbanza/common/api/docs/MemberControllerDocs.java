package com.suhsaechan.dongbanza.common.api.docs;

import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.member.dto.request.GuestStartForm;
import com.suhsaechan.dongbanza.member.dto.response.GuestStartResponse;
import com.suhsaechan.dongbanza.member.dto.response.MemberDto;
import com.suhsaechan.dongbanza.member.dto.response.NicknameSuggestionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "게스트 시작 및 내 정보 API")
public interface MemberControllerDocs {

  @Operation(summary = "닉네임 추천", description = "시작 화면 초기값으로 쓸 닉네임을 생성한다. 저장하지 않는다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "생성 성공",
              content = @Content(schema = @Schema(implementation = NicknameSuggestionResponse.class)))
      })
  ResponseEntity<NicknameSuggestionResponse> suggestNickname();

  @Operation(summary = "게스트 시작",
      description = "가입·로그인 없이 닉네임(선택)과 MBTI로 플레이 계정을 만들고 토큰을 발급한다. "
          + "닉네임을 생략하면 서버가 자동 생성한다. 액세스 토큰은 Authorization 헤더, 리프레시 토큰은 body로 내려간다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "시작 성공",
              content = @Content(schema = @Schema(implementation = GuestStartResponse.class))),
          @ApiResponse(responseCode = "400", description = "닉네임 또는 MBTI 형식 오류")
      })
  ResponseEntity<GuestStartResponse> startAsGuest(@RequestBody GuestStartForm form);

  @Operation(summary = "내 정보 조회", description = "토큰 소유자의 정보를 조회한다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공",
              content = @Content(schema = @Schema(implementation = MemberDto.class))),
          @ApiResponse(responseCode = "403", description = "접근 거부"),
          @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
      }
  )
  ResponseEntity<MemberDto> getMemberById(
      @AuthenticationPrincipal CustomUserDetails userDetails);
}
