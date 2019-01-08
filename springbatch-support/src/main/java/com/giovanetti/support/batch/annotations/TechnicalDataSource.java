package com.giovanetti.support.batch.annotations;

import org.springframework.context.annotation.Primary;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Primary
public @interface TechnicalDataSource {

}
