package myframework.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Service field variables should use this annotation
 */
@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
public @interface MyAutowired {
	
}