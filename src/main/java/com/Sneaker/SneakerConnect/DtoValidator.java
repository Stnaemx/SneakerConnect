package com.Sneaker.SneakerConnect;

import com.Sneaker.SneakerConnect.exceptions.DtoNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DtoValidator<T> {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public void validate(T dtoObject) {
        Set<ConstraintViolation<T>> errors = validator.validate(dtoObject);

        // streaming all the error violation objects into set of strings
        if(!errors.isEmpty()) {
            var errorMessages = errors.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            throw new DtoNotValidException(errorMessages);
        }
    }
}