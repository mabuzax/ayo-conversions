package com.ayo.conversion.util;

public class StringUtil {

    /**
     * Declaring constructor as private to avoid this class being instantiated
     */
    private StringUtil() {
    }

    /**
     * This method evaluates if a String value is empty or null.
     *
     * @param str the string value to evaluate
     * @return boolean value indicating if the string is empty or null
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
