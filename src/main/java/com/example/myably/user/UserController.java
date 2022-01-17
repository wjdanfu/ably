package com.example.myably.user;

import com.example.myably.config.BaseException;
import com.example.myably.config.BaseResponse;
import com.example.myably.user.dto.*;
import com.example.myably.utils.JwtService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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

    @ApiOperation(value = "회원 등록", response = BaseResponse.class)
    @PostMapping("/user")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2019, message = "이메일 형식을 확인해 주세요"),
            @ApiResponse(code = 2014, message = "인증 완료된 휴대폰 번호가 아닙니다."),
            @ApiResponse(code = 2003, message = "중복된 닉네임입니다."),
            @ApiResponse(code = 2004, message = "중복된 이메일입니다."),
            @ApiResponse(code = 2009, message = "이미 가입된 휴대폰 번호 입니다."),
            @ApiResponse(code = 2005, message = "비밀번호 암호화에 실패하였습니다.")
    })
    public BaseResponse createUser(@RequestBody PostUserReq postUserReq){
        if(postUserReq.getNickName() == null || postUserReq.getPassword()==null || postUserReq.getName()==null ||
        postUserReq.getPhoneNumber()==null || postUserReq.getEmail() ==null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(!isPhoneNumber(postUserReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            userService.createUser(postUserReq);
            return new BaseResponse();
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ApiOperation(value = "회원가입 인증 코드 보내기", response = BaseResponse.class)
    @PostMapping("/account/code")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2009, message = "이미 가입된 휴대폰 번호 입니다."),
            @ApiResponse(code = 2010, message = "코드 전송 실패.")
    })
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

    @ApiOperation(value = "회원가입 인증 코드 확인하기", response = BaseResponse.class)
    @PostMapping("/account/verify")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2011, message = "코드를 입력해 주세요"),
            @ApiResponse(code = 2012, message = "먼저 코드 전송을 해주세요."),
            @ApiResponse(code = 2013, message = "코드가 일치하지 않습니다."),
            @ApiResponse(code = 2015, message = "이미 인증 된 휴대폰 번호입니다.")
    })
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
    @ApiOperation(value = "로그인", response = BaseResponse.class)
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2019, message = "이메일 형식을 확인해 주세요"),
            @ApiResponse(code = 2016, message = "가입된 회원이 아닙니다."),
            @ApiResponse(code = 2020, message = "입력한 정보가 맞는지 확인 해주세요."),
            @ApiResponse(code = 2017, message = "비밀번호가 틀렸습니다.")
    })
    public BaseResponse<PostLoginRes> loginUser(@RequestBody PostLoginReq postLoginReq) {
        if(postLoginReq.getPhoneNumber() == null && postLoginReq.getNickName() ==null && postLoginReq.getEmail()==null){
            return new BaseResponse<>(REQUEST_ERROR);
        }else if (postLoginReq.getPassword()==null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(postLoginReq.getPhoneNumber() != null && !isPhoneNumber(postLoginReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        if(postLoginReq.getEmail() != null && !isRegexEmail(postLoginReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostLoginRes postLoginRes = userService.loginUser(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2001, message = "JWT를 입력해주세요."),
            @ApiResponse(code = 2002, message = "유효하지 않은 JWT입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 유저의 접근입니다.")
    })
    @ApiOperation(value = "유저정보 확인", response = BaseResponse.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "JWT Token", required = false,dataType = "string"
    ,paramType = "header")})
    @GetMapping("/user/{userIdx}")
    public BaseResponse<GetUserRes> userInfo(@PathVariable("userIdx") int userIdx){
        try {
            if (jwtService.getJwt() == null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            int userIdxByJWT = jwtService.getUserIdx();
            if (userIdxByJWT != userIdx){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserRes getUserRes = userService.userInfo(userIdx);
            return new BaseResponse<>(getUserRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ApiOperation(value = "비밀번호 변경 인증코드 발송", response = BaseResponse.class)
    @PostMapping("/reset-password/code")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2016, message = "가입된 회원이 아닙니다."),
            @ApiResponse(code = 2010, message = "코드 전송 실패.")
    })
    public BaseResponse<String> phonePasswordAuth(@RequestBody PostPhoneReq postPhoneReq) {
        if(postPhoneReq.getPhoneNumber() == null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(!isPhoneNumber(postPhoneReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        try{
            String code = userService.phonePasswordAuth(postPhoneReq.getPhoneNumber());
            return new BaseResponse<>(code);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ApiOperation(value = "비밀번호 변경 인증코드 확인하기", response = BaseResponse.class)
    @PostMapping("/reset-password/verify")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2011, message = "코드를 입력해 주세요"),
            @ApiResponse(code = 2012, message = "먼저 코드 전송을 해주세요."),
            @ApiResponse(code = 2013, message = "코드가 일치하지 않습니다."),
            @ApiResponse(code = 2016, message = "가입된 회원이 아닙니다.")
    })
    public BaseResponse<String> verifyPasswordCode(@RequestBody PostCodeReq postCodeReq) {
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
            userService.verifyPasswordCode(postCodeReq);
            return new BaseResponse<>("인증 완료");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ApiOperation(value = "비밀번호 변경", response = BaseResponse.class)
    @PatchMapping("/reset-password")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "성공"),
            @ApiResponse(code = 2007, message = "입력값을 확인해 주세요"),
            @ApiResponse(code = 2008, message = "휴대폰 번호 형식을 확인해 주세요."),
            @ApiResponse(code = 2014, message = "인증 완료된 휴대폰 번호가 아닙니다."),
            @ApiResponse(code = 2016, message = "가입된 회원이 아닙니다.")
    })
    public BaseResponse<String> changePassword(@RequestBody ChangePasswordReq changePasswordReq){
        System.out.println(changePasswordReq.getPhoneNumber());
        System.out.println(changePasswordReq.getPassword());
        if(changePasswordReq.getPhoneNumber() == null || changePasswordReq.getPassword() == null){
            return new BaseResponse<>(REQUEST_ERROR);
        }
        if(!isPhoneNumber(changePasswordReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        if(userService.authStatus(changePasswordReq.getPhoneNumber()) != 'C'){
            return new BaseResponse<>(NON_VERIFIED_PHONE_NUMBER);
        }
        try{
            userService.changePassword(changePasswordReq);
            return new BaseResponse<>("비밀번호가 변경되었습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
