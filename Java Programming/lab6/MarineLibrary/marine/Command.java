package marine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for commands methods. Expects targets with signature void (String[], Object[])
 *
 * <p>
 *     Contains necessary metadata for quick command creation.
 * </p>
 */
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
