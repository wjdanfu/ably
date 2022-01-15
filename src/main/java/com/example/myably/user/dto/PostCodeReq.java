package com.example.myably.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCodeReq {
    private String phoneNumber;
    private Integer code;
}
