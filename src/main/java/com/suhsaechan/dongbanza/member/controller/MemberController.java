package com.suhsaechan.dongbanza.member.controller;

import com.suhsaechan.dongbanza.common.api.docs.MemberControllerDocs;
import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.common.log.LogMonitoringInvocation;
import com.suhsaechan.dongbanza.member.dto.request.GuestStartForm;
import com.suhsaechan.dongbanza.member.dto.response.GuestStartResponse;
import com.suhsaechan.dongbanza.member.dto.response.MemberDto;
import com.suhsaechan.dongbanza.member.dto.response.NicknameSuggestionResponse;
import com.suhsaechan.dongbanza.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MemberController implements MemberControllerDocs {

  private final MemberService memberService;

  @GetMapping("/guest/nickname")
  @LogMonitoringInvocation
  public ResponseEntity<NicknameSuggestionResponse> suggestNickname() {
    return ResponseEntity.ok(new NicknameSuggestionResponse(memberService.suggestNickname()));
  }

  @PostMapping("/guest/start")
  @LogMonitoringInvocation
  public ResponseEntity<GuestStartResponse> startAsGuest(
      @Valid @RequestBody GuestStartForm form
  ) {
    GuestStartResponse response = memberService.startAsGuest(form);
    return ResponseEntity.ok()
        .header("Authorization", "Bearer " + response.getAccessToken())
        .body(response.exceptAccessToken());
  }

  @GetMapping("/my-page")
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> getMemberById(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    MemberDto memberDto
        = memberService.getMemberById(userDetails.getMember().getId());
    return ResponseEntity.ok(memberDto);
  }
}
