package com.example.myably.user;


import com.example.myably.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUser(PostUserReq postuserReq){
        System.out.println(postuserReq.getPassword());
        String createUserQuery = "insert into User (email,nickName ,name, phoneNumber ,password) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{
                postuserReq.getEmail(),postuserReq.getNickName(), postuserReq.getName(), postuserReq.getPhoneNumber(), postuserReq.getPassword()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    public int checkNickName(String nickName){
        return this.jdbcTemplate.queryForObject("select exists(select nickName from User where nickName = ?)",
                int.class,
                nickName);
    }

    public int checkEmail(String email){
        return this.jdbcTemplate.queryForObject("select exists(select email from User where email = ?)",
                int.class,
                email);
    }

    public int checkPhoneNumber(String phoneNumber){
        return this.jdbcTemplate.queryForObject("select exists(select phoneNumber from User where phoneNumber = ?)",
                int.class,
                phoneNumber);

    }


    public void createAuth(String phoneNumber, String code){
        String createAuthQuery = "insert into auth (phoneNumber, code) values (?,?)";
        Object[] createAuthparamas = new Object[]{phoneNumber, code};
        this.jdbcTemplate.update(createAuthQuery, createAuthparamas);

    }
    public void updateCode(String phoneNumber, String code){
        Object[] createAuthparamas = new Object[]{code, phoneNumber};
        this.jdbcTemplate.update("update auth set code = ? where phoneNumber = ?", createAuthparamas);
    }

    public void updatePasswordCode(String phoneNumber, String code){
        Object[] createAuthparamas = new Object[]{code, phoneNumber};
        this.jdbcTemplate.update("update auth set code = ?, status= 'P' where phoneNumber = ?", createAuthparamas);
    }

    public int checkAuthPhoneNumber(String phoneNumber){
        String checkEmailQuery = "select exists(select phoneNumber from auth where phoneNumber = ?)";
        String checkEmailParams = phoneNumber;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public int checkAuthCode(PostCodeReq postCodeReq){
        String checkPhoneQuery = "select exists(select phoneNumber,code from auth where phoneNumber = ? and code = ?)";
        String checkPhoneParams = postCodeReq.getPhoneNumber();
        int checkPwParams = postCodeReq.getCode();
        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
                int.class,
                checkPhoneParams, checkPwParams);
    }
    public void updateVerifyStatus(String phoneNumber){
        this.jdbcTemplate.update("update auth set status = 'T' where phoneNumber = ?",phoneNumber);
    }

    public void updateVerifyPasswordStatus(String phoneNumber){
        this.jdbcTemplate.update("update auth set status = 'C' where phoneNumber = ?",phoneNumber);
    }

    public char checkPhoneVerify(String phoneNumber){
        char check = this.jdbcTemplate.queryForObject("select STATUS from AUTH where phonenumber = ?",
                char.class,phoneNumber);
        return check;
    }

    public LoginInfo checkPhoneNumberAccount(String phoneNumber) {
        return this.jdbcTemplate.queryForObject("select password, userIdx from user where phoneNumber=?",
                (rs, rowNum) -> new LoginInfo(
                        rs.getString("password"),
                        rs.getInt("userIdx")),
                phoneNumber);
    }

    public LoginInfo checkEmailAccount(String email) {
        return this.jdbcTemplate.queryForObject("select password, userIdx from user where email=?",
                (rs, rowNum) -> new LoginInfo(
                        rs.getString("password"),
                        rs.getInt("userIdx")),
                email);
    }

    public LoginInfo checkNickNameAccount(String nickName){
            return this.jdbcTemplate.queryForObject("select password, userIdx from user where nickName=?",
                    (rs, rowNum) -> new LoginInfo(
                            rs.getString("password"),
                            rs.getInt("userIdx")),
                    nickName);
    }

    public GetUserRes userInfo(int userIdx){
        return this.jdbcTemplate.queryForObject("select * from User where userIdx = ?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("phoneNumber")),
                userIdx);
    }

    public void changePassword(ChangePasswordReq changePasswordReq){
        Object[] updatePassword = new Object[]{changePasswordReq.getPassword(), changePasswordReq.getPhoneNumber()};
        this.jdbcTemplate.update("update User set password = ? where phoneNumber = ?",updatePassword);

    }

}
