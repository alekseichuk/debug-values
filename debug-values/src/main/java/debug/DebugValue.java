package debug;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface DebugValue {
    String name() default "";
    String comment() default "";
    String group() default "";
    int[] values() default 0;
}
