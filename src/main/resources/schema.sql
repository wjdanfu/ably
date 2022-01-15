CREATE TABLE IF NOT EXISTS User
(
    userIdx bigint(5) NOT NULL AUTO_INCREMENT ,
    nickName    VARCHAR(255)    NOT NULL,
    password     VARCHAR(255)    NOT NULL,
    name         VARCHAR(255)    NOT NULL,
    phoneNumber  VARCHAR(255)    NOT NULL,
    primary key (userIdx)
    );

CREATE TABLE IF NOT EXISTS Auth(
    authIdx bigint(5) NOT NULL AUTO_INCREMENT,
    phoneNumber VARCHAR(255) NOT NULL ,
    code int not null ,
    status char(1) default 'F',
    primary key (authIdx)
);