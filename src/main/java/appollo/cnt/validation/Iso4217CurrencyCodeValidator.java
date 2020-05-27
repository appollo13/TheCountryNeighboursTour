package appollo.cnt.validation;

import java.util.Currency;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class Iso4217CurrencyCodeValidator implements ConstraintValidator<Iso4217CurrencyCodeConstraint, String> {

    @Override
    public boolean isValid(String currencyCode, ConstraintValidatorContext cxt) {
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
