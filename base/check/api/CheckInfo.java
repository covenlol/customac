package dev.phoenixhaven.customac.base.check.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();

    String type();

    CheckType checkType() default CheckType.OTHER;

    boolean autoBan() default false;

    int banVl() default 20;
}
