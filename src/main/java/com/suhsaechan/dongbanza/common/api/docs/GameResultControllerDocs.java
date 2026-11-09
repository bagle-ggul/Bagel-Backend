package com.suhsaechan.dongbanza.common.api.docs;

import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.game.dto.request.GameResultRequest;
import com.suhsaechan.dongbanza.game.dto.response.GameResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "게임 결과 API", description = "게임 결과 관련 API")
public interface GameResultControllerDocs {

  @Operation(summary = "게임 결과 저장", description = "게임이 끝난 후 결과를 저장한다.",
      responses = {
          @ApiResponse(responseCode = "201", description = "게임 결과 저장 성공"),
          @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
          @ApiResponse(responseCode = "403", description = "인증 실패")
      })
  ResponseEntity<GameResultResponse> saveGameResult(@RequestBody GameResultRequest gameResultRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails);

  @Operation(summary = "내 게임 결과 조회", description = "게스트 토큰 주인의 게임 결과를 조회한다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "게임 결과 조회 성공",
              content = @Content(schema = @Schema(implementation = GameResultResponse.class))),
          @ApiResponse(responseCode = "403", description = "인증 실패"),
          @ApiResponse(responseCode = "404", description = "게임 결과를 찾을 수 없음")
      })
  ResponseEntity<List<GameResultResponse>> getMyGameResults(
      @AuthenticationPrincipal CustomUserDetails userDetails);

  @Operation(summary = "게임 결과 삭제", description = "게스트 토큰 주인의 특정 게임 결과를 삭제한다.",
      responses = {
          @ApiResponse(responseCode = "204", description = "게임 결과 삭제 성공"),
          @ApiResponse(responseCode = "403", description = "인증 실패"),
          @ApiResponse(responseCode = "404", description = "게임 결과를 찾을 수 없음")
      })
  ResponseEntity<Void> deleteGameResult(@PathVariable Long gameResultId,
      @AuthenticationPrincipal CustomUserDetails userDetails);
}
