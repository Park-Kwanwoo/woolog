package com.woolog.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Member(Long id, String name, String email, String password, String nickname, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public NicknameEditor.NicknameEditorBuilder toNicknameEditor() {
        return NicknameEditor.builder()
                .nickname(this.nickname);

    }

    public PasswordEditor.PasswordEditorBuilder toPasswordEditor() {
        return PasswordEditor.builder()
                .password(this.password);

    }

    public void editNickname(NicknameEditor nicknameEditor) {
        this.nickname = nicknameEditor.getNickname();
    }

    public void editPassword(PasswordEditor passwordEditor) {
        this.password = passwordEditor.getPassword();
    }
}
