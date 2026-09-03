package com.bohdan.urlshortener.util;

import java.security.SecureRandom;

public final class Base62Util {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();
    public static final int DEFAULT_LENGTH = 7;

    private Base62Util() {
        // Забороняємо створювати екземпляр утилітарного класу
    }

    public static String generateRandomCode() {
        return generateRandomCode(DEFAULT_LENGTH);
    }

    public static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(randomIndex));
        }
        return sb.toString();
    }

}
