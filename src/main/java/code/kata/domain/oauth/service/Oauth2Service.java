package code.kata.domain.oauth.service;

import code.kata.domain.oauth.constant.Provider;
import code.kata.domain.oauth.service.kakao.KakaoService;
import code.kata.domain.user.domain.User;
import net.bytebuddy.implementation.bind.annotation.DefaultMethod;
import reactor.core.publisher.Mono;

public interface Oauth2Service {
    Mono<User> getUserData(String accessToken);
}
