package code.kata.domain.user.domain;

import code.kata.domain.user.constant.Role;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private int status;
    // 회원 정상 / 휴면 / 탈퇴 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
