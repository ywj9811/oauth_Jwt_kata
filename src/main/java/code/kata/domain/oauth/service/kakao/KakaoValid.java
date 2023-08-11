package code.kata.domain.oauth.service.kakao;

import code.kata.domain.oauth.dto.kakao.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class KakaoValid {
    @Value("${oauth2.kakao.app-id}")
    private String appId;
    private final WebClient webClient;

    public void valid(String accessToken) {
        KakaoTokenResponse kakaoTokenResponse = webClient.get()
                .uri("https://kapi.kakao.com/v1/user/access_token_info")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is4xxClientError(), response -> Mono.error(new ClassCastException()))
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is4xxClientError(), response -> Mono.error(new ClassCastException()))
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        if (!kakaoTokenResponse.getAppId().equals(appId))
            throw new ClassCastException(); // 적절한 예외 처리
    }
}
