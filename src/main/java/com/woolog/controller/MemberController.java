package com.woolog.controller;

import com.woolog.request.MemberEdit;
import com.woolog.request.Signup;
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
    public MemberResponse getMember(@AuthenticationPrincipal String email) {
        return memberService.getMember(email);
    }

    @PatchMapping("/members")
    public void editMemberInfo(@AuthenticationPrincipal String email, @RequestBody @Valid MemberEdit memberEdit) {
        memberService.editMemberInfo(email, memberEdit);
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
