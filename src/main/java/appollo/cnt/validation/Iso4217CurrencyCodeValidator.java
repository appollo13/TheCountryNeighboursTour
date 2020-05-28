package appollo.cnt.validation;

import java.util.Currency;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class Iso4217CurrencyCodeValidator implements ConstraintValidator<Iso4217CurrencyCodeConstraint, String> {

    private boolean required;

    @Override
    public void initialize(Iso4217CurrencyCodeConstraint constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String currencyCode, ConstraintValidatorContext cxt) {
        if (!required && currencyCode == null) {
            return true;
        }
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
