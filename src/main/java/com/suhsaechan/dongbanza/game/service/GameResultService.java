package com.suhsaechan.dongbanza.game.service;

import com.suhsaechan.dongbanza.common.exception.ErrorCode;
import com.suhsaechan.dongbanza.common.exception.api.MemberException;
import com.suhsaechan.dongbanza.game.domain.entity.GameResult;
import com.suhsaechan.dongbanza.game.dto.request.GameResultRequest;
import com.suhsaechan.dongbanza.game.dto.response.GameRankingDto;
import com.suhsaechan.dongbanza.game.dto.response.GameRankingPageDto;
import com.suhsaechan.dongbanza.game.dto.response.GameResultResponse;
import com.suhsaechan.dongbanza.game.repository.GameResultRepository;
import com.suhsaechan.dongbanza.member.domain.constants.GameProgress;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import com.suhsaechan.dongbanza.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameResultService {

  private final GameResultRepository gameResultRepository;
  private final MemberRepository memberRepository;

  public GameResultResponse saveGameResult(GameResultRequest request,long memberId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    GameResult gameResult = request.toGameResult(member);
    gameResultRepository.save(gameResult);

    // null exception 확인
    if (member.getTotalScore() == null) {
      member.updateScore(0);
    }

    // 멤버 총점수 업데이트
    member.updateScore(request.getFinalScore());
    member.updateGameProgress(GameProgress.GAME_END);

    if(!request.getSuccess()){
      // member 총회귀횟수 업데이트
      member.increaseRegressionCount();
    }

    memberRepository.save(member);
    return GameResultResponse.from(gameResult);
  }

  public List<GameResultResponse> getGameResultsByMember(Long memberId) {
    return gameResultRepository.findByMemberId(memberId).stream()
        .map(GameResultResponse::from)
        .collect(Collectors.toList());
  }

  public void deleteGameResult(Long gameResultId, Long memberId) {
    GameResult gameResult = gameResultRepository.findByIdAndMemberId(gameResultId, memberId)
        .orElseThrow(() -> new IllegalArgumentException("Game result not found"));

    gameResultRepository.delete(gameResult);
  }

  @Transactional(readOnly = true)
  public GameRankingPageDto getGameRankingList(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<GameResult> gameResultsPage = gameResultRepository.findAllByOrderByFinalScoreDesc(pageable);
    Page<GameRankingDto> rankingDtoPage = gameResultsPage.map(gameResult -> GameRankingDto.from(gameResult.getMember(), gameResult));
    return new GameRankingPageDto(rankingDtoPage);
  }
}
