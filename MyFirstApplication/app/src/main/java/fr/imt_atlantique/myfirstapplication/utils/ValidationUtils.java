package fr.imt_atlantique.myfirstapplication.utils;

import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class ValidationUtils {

    private static final String TAG = "Validation";

    public static boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, "FR");
            Log.d(TAG, "Validated phone number: " + phoneNumber);

            return phoneNumberUtil.isValidNumber(parsedPhoneNumber);
        } catch (NumberParseException e) {
            Log.e(TAG, "Error parsing phone number", e);
            return false;
        }
    }

    public static boolean isValidName(String name) {
        Log.d(TAG, "Validating name: " + name);
        return !name.matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidCity(String city) {
        Log.d(TAG, "Validating city: " + city);
        return city.matches("[a-zA-Z\\s]+");
    }
}

