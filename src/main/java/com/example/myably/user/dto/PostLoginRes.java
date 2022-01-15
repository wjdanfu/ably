package com.example.myably.user.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostLoginRes {
    private String jwt;
    private int userIdx;

}
