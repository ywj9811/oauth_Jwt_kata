package code.kata.domain.user.service;

import code.kata.domain.oauth.constant.Provider;
import code.kata.domain.oauth.service.Oauth2Service;
import code.kata.domain.oauth.service.SelectOauth2Service;
import code.kata.domain.user.domain.User;
import code.kata.domain.user.repository.UserRepository;
import code.kata.global.config.security.jwt.JwtProvider;
import code.kata.global.config.security.jwt.principal.Oauth2PrincipalDetails;
import code.kata.global.response.SuccessResponse;
import code.kata.global.response.TokenInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static code.kata.global.response.SuccessResponse.create;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserAuthService {
    private final SelectOauth2Service selectOauth2Service;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public SuccessResponse<TokenInfoResponse> signIn(String accessToken, String provider) {
        Oauth2Service oauth2Service = selectOauth2Service.selectService(Provider.valueOf(provider.toUpperCase()));
        User userData = oauth2Service.getUserData(accessToken);

        User user = userRepository.findByUserId(userData.getUserId())
                .orElseGet(() -> signUp(userData));

        log.info("로그인 작업 완료 토큰 발행 시작");
        TokenInfoResponse token = jwtProvider.createToken(getAuthentication(user), Provider.valueOf(provider.toUpperCase()));
        log.info("토큰 발행 완료");
        return create(OK.value(), "ok", token);
    }

    private User signUp(User userData) {
        log.info("회원가입 진행");
        User save = userRepository.save(userData);
        return save;
    }

    private Authentication getAuthentication(User user) {
        log.info("authentication 시작");
        try {
            Oauth2PrincipalDetails principalDetails = new Oauth2PrincipalDetails(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication저장");
            return authentication;
        } catch (Exception e) {
            log.info("exception : {}", e.getMessage());
            log.info("stack {}", e.getStackTrace());
            throw e;
        }
    }
}
