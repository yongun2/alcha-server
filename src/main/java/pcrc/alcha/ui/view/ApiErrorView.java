package pcrc.alcha.ui.view;


import lombok.Getter;
import lombok.ToString;
import org.springframework.util.ObjectUtils;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class ApiErrorView {
    private final List<Error> errors;

    public ApiErrorView(List<MessageType> messageTypes) {
        this.errors = messageTypes.stream().map(Error::errorWithMessageType).collect(Collectors.toList());
    }

    public ApiErrorView(AlchaException exception) {
        this.errors = Collections.singletonList(Error.errorWithException(exception));
    }

    public ApiErrorView(MessageType messageType, String message) {
        this.errors = Collections.singletonList(Error.errorWithMessageTypeAndMessage(messageType, message));
    }

    @Getter
    @ToString
    public static class Error {
        private final String errorType;
        private final String errorMessage;

        public static Error errorWithMessageType(MessageType messageType) {
            return new Error(messageType.name(), messageType.getMessage());
        }

        public static Error errorWithMessageTypeAndMessage(MessageType messageType, String message) {
            return new Error(messageType.name(), message);
        }

        public static Error errorWithException(AlchaException alchaException) {
            return new Error(alchaException);
        }

        private Error(String errorType, String errorMessage) {
            this.errorType = errorType;
            this.errorMessage = errorMessage;
        }

        private Error(AlchaException alchaException) {
            this.errorType = ObjectUtils.isEmpty(alchaException.getType()) ? alchaException.getStatus().getReasonPhrase() :
                    alchaException.getType();
            this.errorMessage = alchaException.getMessage();
        }
    }
}

