package ch07.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOperation {

    int numberOfRetries();

    long durationBetweenRetriesInMs() default 0;

    Class<? extends Throwable>[] retryExceptions() default {Exception.class};

    String failureMessage() default "Operation failed after retrying";

}
