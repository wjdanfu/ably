package com.example.myably.config;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    POST_USERS_EXISTS_NICKNAME(false,2003,"중복된 닉네임입니다."),
    POST_USERS_EXISTS_NAME(false,2004,"중복된 이름입니다."),
    PASSWORD_ENCRYPTION_ERROR(false, 2005, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 2006, "비밀번호 복호화에 실패하였습니다."),
    REQUEST_ERROR(false,2007,"입력값을 확인해 주세요"),
    POST_USERS_INVALID_PHONE_NUMBER(false,2008,"휴대폰 번호 형식을 확인해 주세요."),
    POST_USERS_EXISTS_PHONE_NUMBER(false,2009,"이미 가입된 휴대폰 번호 입니다."),
    SEND_CODE_ERROR(false,2010,"코드 전송 실패."),
    POST_AUTH_EMPTY_CODE(false,2011,"코드를 입력해 주세요"),
    NON_EXIST_PHONE_CODE(false,2012,"먼저 코드 전송을 해주세요."),
    INVALID_AUTH_PHONE_CODE(false,2013,"코드가 일치하지 않습니다."),
    NON_VERIFIED_PHONE_NUMBER(false,2014,"인증 완료된 휴대폰 번호가 아닙니다."),
    ALREADY_VERIFIED_PHONE(false,2015,"이미 인증 된 휴대폰 번호입니다."),
    NON_EXIST_USER(false,2016,"가입된 회원이 아닙니다."),
    FAIL_LOGIN(false,2017,"비밀번호가 틀렸습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
