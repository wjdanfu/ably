package com.example.myably.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private String email;
    private String name;
    private String nickName;
    private String phoneNumber;
}
