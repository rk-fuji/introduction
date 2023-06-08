package com.example.introduction2.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = MediaTypeImageValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MediaTypeImage {

	// バリデーションエラー時のメッセージ
	String message() default "jpeg, jpg, png, gif 形式のファイルを指定してください";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
