package com.example.myably.user;

import com.example.myably.config.BaseException;
import com.example.myably.config.BaseResponse;
import com.example.myably.user.dto.*;
import com.example.myably.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static com.example.myably.config.BaseResponseStatus.*;
import static com.example.myably.utils.ValidationRegex.*;

@RestController
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/users")
    public BaseResponse createUser(@RequestBody PostUserReq postUserReq){
        if(postUserReq.getNickName() == null || postUserReq.getPassword()==null || postUserReq.getName()==null ||
        postUserReq.getPhoneNumber()==null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(!isPhoneNumber(postUserReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        try{
            userService.createUser(postUserReq);
            return new BaseResponse();
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @PostMapping("/phone/code")
    public BaseResponse<String> phoneAuth(@RequestBody PostPhoneReq postPhoneReq) {
        if(postPhoneReq.getPhoneNumber() == null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(!isPhoneNumber(postPhoneReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        try{
            String code = userService.phoneAuth(postPhoneReq.getPhoneNumber());
            return new BaseResponse<>(code);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @PostMapping("/phone/verify")
    public BaseResponse<String> verifyCode(@RequestBody PostCodeReq postCodeReq) {
        if(postCodeReq.getPhoneNumber() == null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(!isPhoneNumber(postCodeReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        try{
            if(postCodeReq.getCode() == null){
                return new BaseResponse<>(POST_AUTH_EMPTY_CODE);
            }
            userService.verifyCode(postCodeReq);
            return new BaseResponse<>("인증 완료");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @PostMapping("/login")
    public BaseResponse<PostLoginRes> loginUser(@RequestBody PostLoginReq postLoginReq) {
        if(postLoginReq.getPhoneNumber() == null && postLoginReq.getNickName() ==null){
            return new BaseResponse<>(REQUEST_ERROR);
        }else if (postLoginReq.getPassword()==null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(postLoginReq.getPhoneNumber() != null && !isPhoneNumber(postLoginReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        try{
            PostLoginRes postLoginRes = userService.loginUser(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
