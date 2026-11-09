package com.suhsaechan.dongbanza.game.controller;

import com.suhsaechan.dongbanza.common.api.docs.GameResultControllerDocs;
import com.suhsaechan.dongbanza.common.exception.ErrorCode;
import com.suhsaechan.dongbanza.common.exception.api.MemberException;
import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.game.dto.request.GameResultRequest;
import com.suhsaechan.dongbanza.game.dto.response.GameRankingDto;
import com.suhsaechan.dongbanza.game.dto.response.GameRankingPageDto;
import com.suhsaechan.dongbanza.game.dto.response.GameResultResponse;
import com.suhsaechan.dongbanza.game.service.GameResultService;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import com.suhsaechan.dongbanza.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameResultController implements GameResultControllerDocs {

  private final GameResultService gameResultService;
  private final MemberRepository memberRepository;

  @PostMapping("/over")
  public ResponseEntity<GameResultResponse> saveGameResult(
      @RequestBody GameResultRequest gameResultRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    GameResultResponse gameResultResponse
        = gameResultService.saveGameResult(gameResultRequest, Long.parseLong(userDetails.getUsername()));
    return ResponseEntity.status(HttpStatus.CREATED).body(gameResultResponse);
  }

  @GetMapping("/ranking")
  public ResponseEntity<GameRankingPageDto> getGameRanking(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    GameRankingPageDto gameRankingPageDto = gameResultService.getGameRankingList(page, size);
    return ResponseEntity.ok(gameRankingPageDto);
  }

  @GetMapping("/my-results")
  public ResponseEntity<List<GameResultResponse>> getMyGameResults(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    List<GameResultResponse> gameResults = gameResultService.getGameResultsByMember(
        userDetails.getMember().getId());
    return ResponseEntity.ok(gameResults);
  }

  @DeleteMapping("/my-results/{gameResultId}")
  public ResponseEntity<Void> deleteGameResult(
      @PathVariable Long gameResultId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    gameResultService.deleteGameResult(gameResultId,
        userDetails.getMember().getId());
    return ResponseEntity.noContent().build();
  }
}
