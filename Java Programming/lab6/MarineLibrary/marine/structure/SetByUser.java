package marine.structure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Annotation for labeling setters of fields, that are set by user in the object.
 *
 *  <p>
 *      Each SetByUser annotation is marked by a name of attribute it sets.
 *      Additionally, complexity or enum tags can be set for different setting
 *      mechanisms
 *  </p>
 *
 * @author Mitya Ha-ha
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface SetByUser {

    /**
     * An attribute that is set by the target
     *
     */
    String attributeName();

    /**
     * Complexity tag. Make true if the target sets an Object field.
     *
     */
    boolean isComplex() default false;
    /**
     * Enum tag. Make true if the target sets an Enum field.
     *
     */
    boolean isEnum() default false;

    /**
     * Class of the Enum field.  Should be set, only if isEnum tag is true.
     *
     */
    Class enumClass() default Enum.class;
    /**
     * Null tag.  Should be true, if the user can skip setting this field.
     *
     */
    boolean canBeNull() default false;


}
