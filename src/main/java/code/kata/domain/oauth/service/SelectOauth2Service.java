package code.kata.domain.oauth.service;

import code.kata.domain.oauth.constant.Provider;
import code.kata.domain.oauth.service.kakao.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static code.kata.domain.oauth.constant.Provider.KAKAO;

@Component
@RequiredArgsConstructor
public class SelectOauth2Service {
    private final KakaoService kakaoService;

    public Oauth2Service selectService(Provider provider) {
        switch (provider) {
            case KAKAO:
                return kakaoService;
            default:
                return kakaoService;
        }
    }
}
