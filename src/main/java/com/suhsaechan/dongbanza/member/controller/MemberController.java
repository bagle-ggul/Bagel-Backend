package com.suhsaechan.dongbanza.member.controller;

import com.suhsaechan.dongbanza.common.api.docs.MemberControllerDocs;
import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.common.log.LogMonitoringInvocation;
import com.suhsaechan.dongbanza.member.dto.request.MemberLoginForm;
import com.suhsaechan.dongbanza.member.dto.request.MemberSignUpForm;
import com.suhsaechan.dongbanza.member.dto.response.MemberDto;
import com.suhsaechan.dongbanza.member.dto.response.MemberLoginResponse;
import com.suhsaechan.dongbanza.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

  @PostMapping("login")
  @LogMonitoringInvocation
  public ResponseEntity<MemberLoginResponse> login(
      @Valid @RequestBody MemberLoginForm form
  ) {
    MemberLoginResponse memberLoginResponse = memberService.login(form);
    return ResponseEntity.ok()
        .header("Authorization",
            "Bearer " + memberLoginResponse.getAccessToken())
        .body(MemberLoginResponse.exceptAccessToken(memberLoginResponse));
  }

  @PostMapping("/signup")
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> signup(
      @Valid @RequestBody MemberSignUpForm form
  ) {
    MemberDto savedMember = memberService.save(form);
    return ResponseEntity.ok(savedMember);
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
