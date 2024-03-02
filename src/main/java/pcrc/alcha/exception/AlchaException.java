package pcrc.alcha.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlchaException extends RuntimeException {
    private final HttpStatus status;
    private final String type;

    public AlchaException(MessageType message) {
        super(message.getMessage());
        this.status = message.getStatus();
        this.type = message.name();
    }
}
