package com.realbeatz.utils;

import com.realbeatz.user.User;
import com.realbeatz.user.profile.UserProfile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class UserUtils {
    public static final int MAX_USERNAME_LENGTH = 30;
    // BCRYPT max input length is 72
    public static final int MAX_PASSWORD_LENGTH = 72;
    public static final int MAX_LAST_NAME_LENGTH = 40;
    public static final int MAX_FIRST_NAME_LENGTH = 50;
    public static final int MAX_BIO_LENGTH = 150;

    public static final Map<String, Predicate<Object>> userAccountChecks = new HashMap<>();

    /**
     * Returns a hashmap containing all the checks needed for
     * validating each field of a {@link User} and {@link UserProfile} mapped to the name
     * of the field being validated
     * @return hashmap containing all the checks
     */
    public static Map<String, Predicate<Object>> getUserAccountChecks() {
        if (userAccountChecks.isEmpty()) {
            initializeChecks();
        }
        return userAccountChecks;
    }

    /**
     * Defines all the input validation predicates for
     * user input on User and UserProfile
     */
    private static void initializeChecks() {
        Predicate<Object> usernameCheck = username -> {
            String usernameString = (String) username;
            return usernameString.length() <= MAX_USERNAME_LENGTH;
        };
        Predicate<Object> passwordCheck = password -> {
            String passwordString = (String) password;
            return passwordString.length() <= MAX_PASSWORD_LENGTH;
        };
        Predicate<Object> lastNameCheck = lastName -> {
            String lastNameString = (String) lastName;
            return lastNameString.length() <= MAX_LAST_NAME_LENGTH;
        };
        Predicate<Object> firstNameCheck = firstName -> {
            String firstNameString = (String) firstName;
            return firstNameString.length() <= MAX_FIRST_NAME_LENGTH;
        };
        Predicate<Object> dobCheck = dob -> {
            LocalDate dateOfBirth = (LocalDate) dob;
            return dateOfBirth.isBefore(LocalDate.now());
        };
        Predicate<Object> bioCheck = bio -> {
            if (bio == null) return true;
            String bioString = (String) bio;
            return bioString.length() <= MAX_BIO_LENGTH;
        };

        userAccountChecks.put("username", usernameCheck);
        userAccountChecks.put("password", passwordCheck);
        userAccountChecks.put("lastName", lastNameCheck);
        userAccountChecks.put("firstName", firstNameCheck);
        userAccountChecks.put("dob", dobCheck);
        userAccountChecks.put("bio", bioCheck);
    }
}
