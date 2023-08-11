package code.kata.domain.oauth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponse {
    private Long id;
    private KakaoAccount kakaoAccount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class KakaoAccount {
        private KakaoProfile profile;
        private Optional<String> email;
        private Optional<String> gender;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class KakaoProfile {
            private Optional<String> nickname;
        }
    }

    @JsonProperty("kakao_account")
    public void setKakaoAccount(KakaoAccount kakaoAccount) {
        this.kakaoAccount = kakaoAccount;
    }

}
