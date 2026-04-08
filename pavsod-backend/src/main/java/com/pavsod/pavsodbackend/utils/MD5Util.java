package com.pavsod.pavsodbackend.utils;

import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;

public class MD5Util {

    public static String encrypt(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
    }

}