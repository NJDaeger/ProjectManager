package com.njdaeger.projectmanager.webapp;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class TokenUtil {

    private static final int TOKEN_LENGTH = 64;
    private static final char[] CHARS_ARRAY = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '_', '$', '-',
            '.', ',', '=', '<', '>', '|', ':', ';', '[', ']', '+', '*', '&', '^', '%', '#'};

    static final Set<String> GENERATED_TOKENS = new HashSet<>();

    public static String generateToken(String seed) {
        var sb = new StringBuilder();
        var sr = new SecureRandom(seed.getBytes(StandardCharsets.UTF_8));
        var curLength = 0;
        while (curLength++ < TOKEN_LENGTH) sb.append(CHARS_ARRAY[sr.nextInt(CHARS_ARRAY.length)]);
        if (GENERATED_TOKENS.contains(sb.toString())) return generateToken(sb.toString(), sr.nextInt(1000) + "");
        else GENERATED_TOKENS.add(sb.toString());
        return sb.toString();
    }

    public static String generateToken(String userId, String otp) {
        return generateToken(userId + otp);
    }

}
