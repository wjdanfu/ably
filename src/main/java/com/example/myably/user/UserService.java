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
        if (userDao.checkNickName(postUserReq.getNickName()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }
        if (userDao.checkAuthPhoneNumber(postUserReq.getPhoneNumber()) == 0 ||
                userDao.checkPhoneVerify(postUserReq.getPhoneNumber()) == 'F'){
            throw new BaseException(NON_VERIFIED_PHONE_NUMBER);
        }
        String pwd;
        try {
            //암호화
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
    public PostLoginRes loginUser(PostLoginReq postLoginReq) throws BaseException{
        String phoneNumber = postLoginReq.getPhoneNumber();
        String nickName = postLoginReq.getNickName();

        PostLoginRes postLoginRes = new PostLoginRes();

        if(userDao.checkPhoneNumber(phoneNumber) == 0 &&
                userDao.checkNickName(nickName) == 0){
            throw new BaseException(NON_EXIST_USER);
        }

        String realpw;
        LoginInfo loginInfo;
        if(nickName == null){
            loginInfo = userDao.checkPhoneNumberAccount(phoneNumber);
        }
        else{
            loginInfo = userDao.checkNickNameAccount(nickName);
        }
        System.out.println(loginInfo.getUserIdx());
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
}
