package com.woolog.controller;

import com.woolog.request.Signup;
import com.woolog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public void signup(@RequestBody Signup signup) {
        memberService.singup(signup);
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
