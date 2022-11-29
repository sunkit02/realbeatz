package com.realbeatz.utils;

import com.realbeatz.exceptions.InvalidUserInputException;

import java.util.Map;
import java.util.function.Predicate;

public class ValidationUtils {
    public static void validateField(
            String field,
            Object value,
            Map<String, Predicate<Object>> checks) throws InvalidUserInputException {
        if (!checks.containsKey(field)) {
            throw new IllegalArgumentException(
                    "Field: " + field + " is not part of the checklist, " +
                            "check your invalid field filter");
        }

        if (!checks.get(field).test(value)) {
            throw new InvalidUserInputException(
                    "Input: " + value + " for field: " + field + " is not valid");
        }
    }
}
