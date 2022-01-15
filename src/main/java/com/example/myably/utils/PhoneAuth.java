package com.example.myably.utils;

import java.util.Random;


public class PhoneAuth {
    public static String createCode() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) { // 인증코드 4자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }
}
