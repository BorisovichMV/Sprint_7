package org.example;

import java.security.SecureRandom;

public class RandomStringGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    public static String generateFirstName() {
        return generateRandomString(10);
    }

    public static String generateLogin() {
        return generateRandomString(7);
    }

    public static String generatePassword() {
        return generateRandomString(15);
    }
}
