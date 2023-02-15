package manager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String name();

    String[] aliases();

    String desc();

    int basicArgsCount() default 0;

    int objectArgsCount() default 0;

    Class[] basicArgsTypes() default {};

    Class[] objectArgsTypes() default {};

}
