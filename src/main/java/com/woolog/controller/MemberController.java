package com.woolog.controller;

import com.woolog.request.member.NicknameEdit;
import com.woolog.request.member.PasswordEdit;
import com.woolog.request.member.Signup;
import com.woolog.response.ApiResponse;
import com.woolog.response.MemberResponse;
import com.woolog.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public void signup(@RequestBody @Valid Signup signup) {
        memberService.singup(signup);
    }

    @GetMapping("/members")
    public ApiResponse<MemberResponse> getMember(@AuthenticationPrincipal String email) {
        return ApiResponse.successWithContent(memberService.getMember(email));
    }

    @PatchMapping("/members/nickname")
    public ApiResponse<MemberResponse> editNickname(@AuthenticationPrincipal String email, @RequestBody @Valid NicknameEdit nicknameEdit) {
        return ApiResponse.successWithContent(memberService.editNickname(email, nicknameEdit));
    }

    @PatchMapping("/members/password")
    public ApiResponse<MemberResponse> editPassword(@AuthenticationPrincipal String email, @RequestBody @Valid PasswordEdit passwordEdit) {
        return ApiResponse.successWithContent(memberService.editPassword(email, passwordEdit));
    }

    @DeleteMapping("/members")
    public void deleteMember(@AuthenticationPrincipal String email) {
        memberService.deleteMember(email);
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/member")
    public String member() {
        return "member";
    }
}
