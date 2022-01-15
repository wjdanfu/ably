package com.example.myably.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String nickName;
    private String name;
    private String password;
    private String phoneNumber;
}
