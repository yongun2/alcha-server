package pcrc.alcha.ui.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pcrc.alcha.application.service.user.UserOperationUseCase;
import pcrc.alcha.application.service.user.UserReadUseCase;
import pcrc.alcha.application.service.user.UserReadUseCase.UserFindQuery;
import pcrc.alcha.application.service.user.UserService;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.ui.requestBody.UserCreateRequest;
import pcrc.alcha.ui.view.ApiResponseView;
import pcrc.alcha.ui.view.user.UserView;

import java.util.Objects;

import static pcrc.alcha.application.service.user.UserOperationUseCase.*;
import static pcrc.alcha.application.service.user.UserReadUseCase.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserOperationUseCase operationUseCase;
    private final UserReadUseCase readUseCase;

    @PostMapping("/register")
    ResponseEntity<ApiResponseView<UserView>> signUp(@RequestBody @Validated UserCreateRequest request) {

        UserCreateCommand command = UserCreateCommand.builder()
                .username(request.username())
                .password(request.password())
                .nickname(request.nickname())
                .profileImgBase64(request.profileImageBase64())
                .build();
        FindUserResult register = operationUseCase.register(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new UserView(register)));
    }

    @PostMapping("/login")
    void login() {

    }

    @GetMapping("/check-duplicate")
    ResponseEntity<ApiResponseView<UserView>> checkDuplicate(@RequestParam(required = false) String username, @RequestParam(required = false) String nickname) {

        if ((username != null && nickname != null) || (username == null && nickname == null)) {
            throw new AlchaException(MessageType.BAD_REQUEST);
        }

        UserFindQuery query = UserFindQuery.builder()
                .username(username)
                .nickname(nickname)
                .build();

        FindUserResult ok = readUseCase.checkDuplicate(query);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseView<>(new UserView(ok)));
    }

    @DeleteMapping("/logout")
    void logout() {

    }

    @GetMapping("/health-check")
    String healthTest() {
        return "ok";
    }
}
