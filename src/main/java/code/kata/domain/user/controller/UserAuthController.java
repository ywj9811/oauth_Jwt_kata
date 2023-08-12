package code.kata.domain.user.controller;

import code.kata.domain.user.dto.request.SignInRequest;
import code.kata.domain.user.service.UserAuthService;
import code.kata.global.response.SuccessResponse;
import code.kata.global.response.TokenInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/signIn/{provider}")
    public Mono<SuccessResponse<TokenInfoResponse>> signIn(@PathVariable String provider, @RequestBody SignInRequest signInRequest) {
        Mono<SuccessResponse<TokenInfoResponse>> tokenInfoResponseSuccessResponse = userAuthService.signIn(signInRequest.getAccessToken(), provider);
        return tokenInfoResponseSuccessResponse;
    }
}
