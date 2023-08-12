package code.kata.domain.oauth.service.kakao;

import code.kata.domain.oauth.dto.OauthToUser;
import code.kata.domain.oauth.dto.kakao.KakaoResponse;
import code.kata.domain.oauth.service.Oauth2Service;
import code.kata.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService implements Oauth2Service {
    private final WebClient webClient;
    private final KakaoValid kakaoValid;
    private final OauthToUser oauthToUser;

    @Override
    public Mono<User> getUserData(String accessToken) {
        kakaoValid.valid(accessToken);
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is4xxClientError(), response -> Mono.error(new ClassCastException()))
                //적절한 예외 처리
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is5xxServerError(), response -> Mono.error(new ClassCastException()))
                //적절한 예외 처리
                .bodyToMono(KakaoResponse.class)
                .flatMap(oauthToUser::fromKakao);
    }
}
