package code.kata.domain.oauth.dto;

import code.kata.domain.oauth.constant.Provider;
import code.kata.domain.oauth.dto.kakao.KakaoResponse;
import code.kata.domain.user.constant.Role;
import code.kata.domain.user.domain.User;
import org.springframework.stereotype.Component;

import static code.kata.domain.oauth.constant.Provider.KAKAO;

@Component
public class OauthToUser {
    public User fromKakao(KakaoResponse kakaoResponse) {
        KakaoResponse.KakaoAccount kakaoAccount = kakaoResponse.getKakaoAccount();
        KakaoResponse.KakaoAccount.KakaoProfile profile = kakaoAccount.getProfile();
        return User.builder()
                .userId(KAKAO.name() + kakaoResponse.getId())
                .nickName(profile.getNickname().orElse("noDef"))
                .provider(KAKAO.name())
                .status(1)
                .role(Role.USER)
                .build();
    }
}
