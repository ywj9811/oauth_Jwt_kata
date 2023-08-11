package code.kata.domain.oauth.service.kakao;

import code.kata.domain.oauth.dto.OauthToUser;
import code.kata.domain.oauth.dto.kakao.KakaoResponse;
import code.kata.domain.oauth.service.Oauth2Service;
import code.kata.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService implements Oauth2Service {
    private final WebClient webClient;
    private final KakaoValid kakaoValid;
    private final OauthToUser oauthToUser;

    @Override
    public User getUserData(String accessToken) {
        kakaoValid.valid(accessToken);

        KakaoResponse kakaoResponse = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is4xxClientError(), response -> Mono.error(new ClassCastException()))
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is4xxClientError(), response -> Mono.error(new ClassCastException()))
                .bodyToMono(KakaoResponse.class)
                .block();

        return oauthToUser.fromKakao(kakaoResponse);
    }
}
