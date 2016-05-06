package com.vistrav.ask.annotations;

import com.vistrav.ask.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AskGranted {
    String value();

    int id() default Constants.DEFAULT_ID;
}
