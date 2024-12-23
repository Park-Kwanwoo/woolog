package com.woolog.controller;

import com.woolog.request.MemberEdit;
import com.woolog.request.Signup;
import com.woolog.response.MemberResponse;
import com.woolog.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public void signup(@RequestBody @Valid Signup signup) {
        memberService.singup(signup);
    }

    @GetMapping("/members/{memberHashId}")
    public MemberResponse getMember(@PathVariable String memberHashId) {
        return memberService.getMember(memberHashId);
    }

    @PatchMapping("/members/{memberHashId}")
    public void editMemberInfo(@PathVariable String memberHashId, @RequestBody @Valid MemberEdit memberEdit) {
        memberService.editMemberInfo(memberHashId, memberEdit);
    }

    @DeleteMapping("/members/{memberHashId}")
    public void deleteMember(@PathVariable String memberHashId) {
        memberService.deleteMember(memberHashId);
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
