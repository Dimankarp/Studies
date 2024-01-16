package mitya.haha.utils;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleGuard {
    public String[] roles() default {""};
}
