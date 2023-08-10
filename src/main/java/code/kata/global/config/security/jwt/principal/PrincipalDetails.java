package code.kata.global.config.security.jwt.principal;

import code.kata.domain.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class PrincipalDetails implements UserDetails {
    public abstract User getUser();
}
