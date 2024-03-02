package pcrc.alcha.ui.advice;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.ui.view.ApiErrorView;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class AlchaControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<?> clientAbortException() {
        return new ResponseEntity<>(new ApiErrorView(Collections.singletonList(MessageType.INVALID_IMAGE)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AlchaException.class)
    public ResponseEntity<?> opMessageException(AlchaException ex) {
        return new ResponseEntity<>(new ApiErrorView(ex), ex.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorView(MessageType.BAD_REQUEST, ex.getMessage()), status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorView(MessageType.BAD_REQUEST, ex.getMessage()), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorView(MessageType.BAD_REQUEST, ex.getMessage()), status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorView(MessageType.BAD_REQUEST, ex.getMessage()), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorView(MessageType.INTERNAL_SERVER_ERROR, ex.getMessage()), status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        if (ex instanceof MethodArgumentNotValidException) {
            return new ResponseEntity<>(new ApiErrorView(MessageType.BAD_REQUEST, MessageType.BAD_REQUEST.getMessage()), status);
        }
        return new ResponseEntity<>(new ApiErrorView(MessageType.INTERNAL_SERVER_ERROR, ex.getMessage()), status);
    }


}

