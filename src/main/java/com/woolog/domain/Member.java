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
    private String nickName;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true, nullable = false)
    private String hashId;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(Long id, String name, String email, String password, String nickName, String hashId, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.hashId = hashId;
        this.role = role;
    }

    public MemberEditor.MemberEditorBuilder toEditor() {
        return MemberEditor.builder()
                .nickName(this.nickName)
                .password(this.password);

    }

    public boolean matchPassword(String rawPassword) {
        return this.getPassword().matches(rawPassword);
    }

    public void edit(MemberEditor memberEditor) {
        this.nickName = memberEditor.getNickName();
        this.password = memberEditor.getPassword();
    }
}
