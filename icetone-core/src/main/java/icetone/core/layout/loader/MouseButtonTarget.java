package icetone.core.layout.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import icetone.core.event.mouse.MouseUIButtonEvent;

@Retention(value = RetentionPolicy.RUNTIME)
@Inherited()
@Target(value = ElementType.METHOD)
public @interface MouseButtonTarget {

    String id() default "";
    int button() default MouseUIButtonEvent.LEFT;
    boolean pressed() default false; 
}