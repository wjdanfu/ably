package com.example.myably.user;


import com.example.myably.user.dto.PostCodeReq;
import com.example.myably.user.dto.PostUserReq;
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
        String createUserQuery = "insert into User (nickName ,name, phoneNumber ,password) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{
                postuserReq.getNickName(), postuserReq.getName(), postuserReq.getPhoneNumber(), postuserReq.getPassword()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    public int checkName(String name){
        return this.jdbcTemplate.queryForObject("select exists(select name from User where name = ?)",
                int.class,
                name);
    }

    public int checkNickName(String nickName){
        return this.jdbcTemplate.queryForObject("select exists(select nickName from User where nickName = ?)",
                int.class,
                nickName);
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
    public int updateCode(String phoneNumber, String code){
        Object[] createAuthparamas = new Object[]{code, phoneNumber};
        return this.jdbcTemplate.update("update auth set code = ? where phoneNumber = ?", createAuthparamas);
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

    public char checkPhoneVerify(String phoneNumber){
        System.out.println(phoneNumber);
        char check = this.jdbcTemplate.queryForObject("select STATUS from AUTH where phonenumber = ?",
                char.class,phoneNumber);
        System.out.println(check);
        return check;
    }

}
