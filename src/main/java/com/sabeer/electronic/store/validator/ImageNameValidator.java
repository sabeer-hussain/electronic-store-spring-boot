package com.sabeer.electronic.store.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageNameValidator implements ConstraintValidator<ImageNameValid, String> {

    private static Logger LOGGER = LoggerFactory.getLogger(ImageNameValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        LOGGER.info("Message from isValid : {}", value);

        // logic
        if (value.isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}
