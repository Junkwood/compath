package com.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        String encoded = encoder.encode("1234");

        System.out.println("encoded password = " + encoded);
        System.out.println("matches? " + encoder.matches("1234", encoded));
    }
}