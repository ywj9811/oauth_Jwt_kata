package code.kata.domain.oauth.service;

import code.kata.domain.user.domain.User;

public interface Oauth2Service {
    User getUserData(String accessToken);
}
