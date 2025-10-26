package com.example.airbnbproject.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SHA-256 기반 비밀번호 암호화 유틸 클래스
 * - Salt 생성
 * - 비밀번호 + salt 조합 후 SHA-256 해시 적용
 */
public class SHA256 {

    /**
     * 사용자 비밀번호 암호화를 위한 salt 생성기
     * - 16바이트 랜덤값을 생성한 뒤 Base64로 인코딩하여 반환
     * - 예: rVfe93kJh1N8z8pF8GhPCA==
     */
    public static String createSalt() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG"); // 보안용 랜덤 생성기
            byte[] bytes = new byte[16]; // 16바이트 salt
            random.nextBytes(bytes); // 랜덤값 채우기
            return Base64.getEncoder().encodeToString(bytes); // Base64로 인코딩해서 문자열로 반환
        } catch (Exception e) {
            throw new RuntimeException("Salt 생성 실패", e);
        }
    }

    /**
     * SHA-256 기반 암호화 메서드
     * @param plainText : 사용자 입력 비밀번호
     * @param salt : createSalt()로 생성된 salt
     * @return SHA-256으로 해시된 16진수 문자열
     */
    public static String encrypt(String plainText, String salt) {
        try {
            String raw = plainText + salt; // 입력값 + salt를 붙임
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // SHA-256 알고리즘 객체 생성
            md.update(raw.getBytes()); // 바이트 배열로 변환하여 해시 대상 지정
            byte[] digest = md.digest(); // 해시 계산 수행

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b); // 각 바이트를 2자리 16진수로 변환
                if (hex.length() == 1) hexString.append('0'); // 자리수 맞춤
                hexString.append(hex);
            }

            return hexString.toString(); // 최종 결과 반환

        } catch (Exception e) {
            throw new RuntimeException("SHA-256 암호화 실패", e);
        }
    }
}
