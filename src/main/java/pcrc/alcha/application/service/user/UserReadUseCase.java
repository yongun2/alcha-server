package pcrc.alcha.application.service.user;

import lombok.Builder;
import pcrc.alcha.application.domain.auth.User;

public interface UserReadUseCase {
    FindUserResult getUserByUsername(UserFindQuery query);

    FindUserResult getUserByNickname(UserFindQuery query);

    FindUserResult checkDuplicate(UserFindQuery query);

    @Builder
    record UserFindQuery(long userId, String username, String nickname) {
    }

    @Builder
    record FindUserResult(long userId, String username, String nickname, String profileImgUrl) {
        public static FindUserResult findByUser(User user) {
            return FindUserResult.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .profileImgUrl(user.getProfileImgUrl())
                    .build();
        }

        public static FindUserResult findByUsername(String username) {
            return FindUserResult.builder()
                    .username(username)
                    .build();
        }

        public static FindUserResult findByNickname(String nickname) {
            return FindUserResult.builder()
                    .nickname(nickname)
                    .build();
        }
    }
}
