package pcrc.alcha.utils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithJWTMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    int id() default 129;
    String username() default "asd1234";

    String password() default "$2a$10$GOFol7qBtvRpV7Qaup4PcehZu5Wv8D5cvep1tx/w7ulFfbJ.B75LG";

    String nickname() default "testUserA";
}
