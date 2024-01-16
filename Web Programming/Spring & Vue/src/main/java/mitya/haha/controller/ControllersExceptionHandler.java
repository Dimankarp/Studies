package mitya.haha.controller;

import mitya.haha.service.RoleAlreadyRegisteredException;
import mitya.haha.service.UserAlreadyRegisteredException;
import mitya.haha.utils.NotEnoughAuthorityException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.management.relation.RoleNotFoundException;

@ControllerAdvice
public class ControllersExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value
            = {UserAlreadyRegisteredException.class, RoleAlreadyRegisteredException.class})
    protected void handleConflict() {

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value
            = {UsernameNotFoundException.class, RoleNotFoundException.class})
    protected void handeNotFound() {

    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value
            = {JwtValidationException.class})
    protected void handleForbidden() {

    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value
            = {NotEnoughAuthorityException.class})
    protected void handleRoleGuard() {
    }

}