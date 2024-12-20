package com.woolog.annotation;

import com.woolog.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockUserFactory.class)
public @interface CustomWithMockUser {

    String email() default "test@abc.com";
    String password() default "qwer123$";
    String nickname() default "테스터박";

    String name() default "가누";

    String role() default "";

}
