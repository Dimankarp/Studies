package mitya.haha.utils;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotEnoughAuthorityException extends RuntimeException{
}
