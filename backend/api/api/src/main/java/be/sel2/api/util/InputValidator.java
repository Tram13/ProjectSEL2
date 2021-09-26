package be.sel2.api.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class used to validate various different user inputs
 */
public class InputValidator {

    public static final int MAX_STRING_LEN_SHORT = 256;
    public static final int MAX_STRING_LEN_LONG = 4096;
    public static final int MAX_STRING_LEN_FEEDBACK = 16384;

    public static final int MIN_PASSWORD_LEN = 8;

    public static final String KBO_REGEX = "^[01]\\d{9}$";
    public static final String OVO_REGEX = "^OVO\\d{6}$";
    public static final String NIS_REGEX = "^\\d{5}$";

    public static final String EMAIL_REGEX = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|" +
            "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@" +
            "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
            "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
            "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";

    public static final String EMAIL_RULES = "must be a valid email address";
    public static final String KBO_RULES = "must be a valid KBO number";
    public static final String OVO_RULES = "must be a valid OVO code";
    public static final String NIS_RULES = "must be a valid NIS number";
    public static final String NAME_RULES = String.format("must be at most %d characters long, cannot be blank and may not contain newlines", InputValidator.MAX_STRING_LEN_SHORT);
    public static final String MAX_LEN_SHORT_RULES = String.format("may be at most %d characters long", InputValidator.MAX_STRING_LEN_SHORT);
    public static final String MAX_LEN_LONG_RULES = String.format("may be at most %d characters long", InputValidator.MAX_STRING_LEN_LONG);
    public static final String PHONE_NUMBER_RULES = "must be a valid BE or NL phone number";
    public static final String PASSWORD_RULES = String.format("must be at least %d characters and at most %d characters long and must contain at least 1 lowercase letter, 1 uppercase letter and 1 number", InputValidator.MIN_PASSWORD_LEN, InputValidator.MAX_STRING_LEN_SHORT);
    public static final String INVALID_DATE_ORDER = "Production deadline cannot be before Testing and Integration deadline";

    private InputValidator() {
    } //Prevents instantiation

    public static String listOfOptionsRule(Collection<?> options) {
        return String.format("Should be from options: %s", options);
    }

    private static boolean validateValue(String value, String regex) {
        if (value == null) {
            return false;
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);

        return m.find();
    }

    public static boolean validateDeadlines(Date before, Date after) {
        if (before != null && after != null) {
            return before.getTime() <= after.getTime(); // Production deadline cannot be before Testing and Integration deadline
        }
        return true;
    }

    public static boolean validateEmail(String email) {
        return validateValue(email, EMAIL_REGEX);
    }

    public static boolean validateKBO(String kbo) {
        return validateValue(kbo, KBO_REGEX);
    }

    public static boolean validateOVO(String ovo) {
        return validateValue(ovo, OVO_REGEX);
    }

    public static boolean validateNIS(String nis) {
        return validateValue(nis, NIS_REGEX);
    }

    public static boolean validateName(String name) {
        if (name == null || name.isBlank() || name.contains("\n")) {
            return false;
        }
        return name.length() <= MAX_STRING_LEN_SHORT;
    }

    public static boolean validateMaxLenShort(String input) {
        return input == null || input.length() <= MAX_STRING_LEN_SHORT;
    }

    public static boolean validateMaxLenLong(String input) {
        return input == null || input.length() <= MAX_STRING_LEN_LONG;
    }

    public static boolean validateMaxLenFeedback(String input) {
        return input == null || input.length() <= MAX_STRING_LEN_FEEDBACK;
    }

    public static boolean validatePhoneNumber(String number) {
        if (number == null) {
            return false;
        }

        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        try {
            PhoneNumber phoneNumberBE = util.parse(number, "BE"); //Try parsing as BE number
            if (util.isValidNumber(phoneNumberBE)) {
                return true;
            }
            PhoneNumber phoneNumberNL = util.parse(number, "NL"); //Try parsing as NL number
            return util.isValidNumber(phoneNumberNL);

        } catch (NumberParseException ex) {
            return false;
        }
    }

    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < MIN_PASSWORD_LEN || password.length() > MAX_STRING_LEN_SHORT) {
            //Password is too short or too long
            return false;
        }
        return password.matches("^.*[0-9].*$") && // Does password contain a number?
                password.matches("^.*[A-Z].*$") && // Does password contain an uppercase letter?
                password.matches("^.*[a-z].*$"); // Does password contain a lowercase letter?
    }
}
