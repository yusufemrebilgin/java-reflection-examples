package ch07.repeatable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

class Annotations {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ScanPackages {
        String[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ScheduledExecutorClass {
    }

    @Target(ElementType.METHOD)
    @Repeatable(ExecutionSchedules.class)
    @interface ExecuteOnSchedule {
        int delaySeconds() default 0;
        int periodSeconds();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ExecutionSchedules {
        ExecuteOnSchedule[] value();
    }

}
