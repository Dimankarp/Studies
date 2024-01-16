package mitya.haha.service;


import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyRegisteredException extends RuntimeException{
}
