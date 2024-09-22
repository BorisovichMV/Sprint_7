package org.example;

import java.security.SecureRandom;

public class RandomStringGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String AZBUKA = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя";
    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length, String alphabet) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

    public static String generateFirstName() {
        return generateRandomString(10, ALPHABET);
    }

    public static String generateRussianFirstName() {
        return generateRandomString(10, AZBUKA);
    }

    public static String generateLastName() {
        return generateRandomString(10, ALPHABET);
    }

    public static String generateRussianLastName() {
        return generateRandomString(10, AZBUKA);
    }

    public static String generateAddress() {
        return generateRandomString(15, ALPHABET);
    }

    public static String generateRussianAddress() {
        return generateRandomString(15, AZBUKA + " .,");
    }

    public static String generateMetroStation() {
        return generateRandomString(1, DIGITS);
    }

    public static String generatePhone() {
        return "+" + generateRandomString(10, DIGITS);
    }

    public static String generateLogin() {
        return generateRandomString(7, ALPHABET);
    }

    public static String generateComment() {
        return generateRandomString(30, " " + ALPHABET);
    }

    public static String generatePassword() {
        return generateRandomString(15, ALPHABET + DIGITS);
    }
}
