package appollo.cnt.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = Iso4217CurrencyCodeValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Iso4217CurrencyCodeConstraint {

    String message() default "Not a valid ISO-4217 Currency Code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
