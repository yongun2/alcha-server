package pcrc.alcha.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {
    // 400
    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),
    BAD_USERNAME_PATTERN("UserID must be 5-20 characters, lowercase letters, numbers, and special symbols (_), (-) only.", HttpStatus.BAD_REQUEST),
    BAD_PASSWORD_PATTERN("Password must be 8 to 16 characters of upper and lowercase letters, numbers, and special characters.", HttpStatus.BAD_REQUEST),
    BAD_NICKNAME_PATTERN("Nickname must be 4-12 characters", HttpStatus.BAD_REQUEST),

    INVALID_IMAGE("Invalid image.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("Invalid image type, only png and jpeg are allowed", HttpStatus.BAD_REQUEST),

    //401
    INVALID_TOKEN("Received an invalid token.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("The token has expired.", HttpStatus.UNAUTHORIZED),

    // 404
    USER_NOT_FOUND("No such user found.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUNT("No such category found", HttpStatus.NOT_FOUND),

    // 409
    DUPLICATED_USERNAME("Duplicated username", HttpStatus.CONFLICT),
    DUPLICATED_NICKNAME("Duplicated nickname", HttpStatus.CONFLICT),

    // 500
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR_IMAGE_SAVE ("An error occurred while saving image.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String message;
    private final HttpStatus status;

    MessageType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
