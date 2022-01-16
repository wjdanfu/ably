package com.example.myably.user;


import com.example.myably.config.BaseException;
import com.example.myably.config.BaseResponse;
import com.example.myably.config.BaseResponseStatus;
import com.example.myably.config.secret.Secret;
import com.example.myably.user.dto.*;
import com.example.myably.utils.AES128;
import com.example.myably.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.myably.config.BaseResponseStatus.*;
import static com.example.myably.utils.PhoneAuth.createCode;

@Service
public class UserService {

    private final UserDao userDao;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    @Transactional
    public void createUser(PostUserReq postUserReq) throws BaseException {
        if (userDao.checkAuthPhoneNumber(postUserReq.getPhoneNumber()) == 0 ||
                userDao.checkPhoneVerify(postUserReq.getPhoneNumber()) == 'F'){
            throw new BaseException(NON_VERIFIED_PHONE_NUMBER);
        }
        if (userDao.checkNickName(postUserReq.getNickName()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }
        if (userDao.checkPhoneNumber(postUserReq.getPhoneNumber()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_PHONE_NUMBER);
        }
        if (userDao.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try {

            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());

            postUserReq.setPassword(pwd);
            userDao.createUser(postUserReq);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
    }

    @Transactional
    public String phoneAuth(String phoneNumber) throws BaseException{
        if(userDao.checkPhoneNumber(phoneNumber) == 1)
            throw new BaseException(POST_USERS_EXISTS_PHONE_NUMBER);
        try{
            String code = createCode();
            if(userDao.checkAuthPhoneNumber(phoneNumber) == 1)
                userDao.updateCode(phoneNumber, code);
            else
                userDao.createAuth(phoneNumber, code);
            return code;
        }catch (Exception ignored) {
            throw new BaseException(SEND_CODE_ERROR);
        }
    }

    @Transactional
    public String phonePasswordAuth(String phoneNumber) throws BaseException{
        if(userDao.checkPhoneNumber(phoneNumber) != 1)
            throw new BaseException(NON_EXIST_USER);
        try{
            String code = createCode();
            userDao.updatePasswordCode(phoneNumber, code);
            return code;
        }catch (Exception ignored) {
            throw new BaseException(SEND_CODE_ERROR);
        }
    }

    @Transactional
    public void verifyCode(PostCodeReq postCodeReq) throws BaseException {
        if(userDao.checkAuthPhoneNumber(postCodeReq.getPhoneNumber()) == 0) {
            throw new BaseException(NON_EXIST_PHONE_CODE);
        }
        if(userDao.checkAuthCode(postCodeReq) == 0) {
            throw new BaseException(INVALID_AUTH_PHONE_CODE);
        }
        if(userDao.checkPhoneVerify(postCodeReq.getPhoneNumber())=='T'){
            throw new BaseException(ALREADY_VERIFIED_PHONE);
        }
        else{
            userDao.updateVerifyStatus(postCodeReq.getPhoneNumber());
        }
    }

    @Transactional
    public void verifyPasswordCode(PostCodeReq postCodeReq) throws BaseException {
        userDao.updateVerifyPasswordStatus(postCodeReq.getPhoneNumber());
    }

    @Transactional
    public PostLoginRes loginUser(PostLoginReq postLoginReq) throws BaseException{
        String phoneNumber = postLoginReq.getPhoneNumber();
        String nickName = postLoginReq.getNickName();
        String email = postLoginReq.getEmail();

        PostLoginRes postLoginRes = new PostLoginRes();

        if(userDao.checkPhoneNumber(phoneNumber) == 0 &&
                userDao.checkNickName(nickName) == 0 && userDao.checkEmail(email)==0){
            throw new BaseException(NON_EXIST_USER);
        }

        String realpw;
        LoginInfo loginInfo;
        try{
        if(phoneNumber == null){
            if (email ==null) {
                loginInfo = userDao.checkNickNameAccount(nickName);
            }else{
                loginInfo = userDao.checkEmailAccount(email);
            }
        }else{
            loginInfo = userDao.checkPhoneNumberAccount(phoneNumber);
        }}catch (Exception ignored){
            throw new BaseException(CHECK_INPUT);
        }
        try{
            realpw = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(loginInfo.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
        if (postLoginReq.getPassword().equals(realpw)){

            String jwt = jwtService.createJwt(loginInfo.getUserIdx());
            postLoginRes.setJwt(jwt);
            postLoginRes.setUserIdx(loginInfo.getUserIdx());
            return postLoginRes;
        }
        else {
            throw new BaseException(FAIL_LOGIN);
        }
    }

    public GetUserRes userInfo(int userIdx) throws BaseException{
        GetUserRes getUserRes = userDao.userInfo(userIdx);
        return getUserRes;
    }

    public char authStatus(String phoneNumber){
        return userDao.checkPhoneVerify(phoneNumber);
    }

    @Transactional
    public void changePassword(ChangePasswordReq changePasswordReq) throws BaseException{
        String pwd;
        try {
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(changePasswordReq.getPassword());
            changePasswordReq.setPassword(pwd);
            userDao.changePassword(changePasswordReq);
            userDao.updateVerifyStatus(changePasswordReq.getPhoneNumber());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
    }


}
