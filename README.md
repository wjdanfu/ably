# ably
에이블리 벡엔드 과제


## 시작하기
jdk가 설치가 안되어있으면 먼저 설치해주세요 

**본 과제는 openjdk version "17.0.1" 환경에서 만들어 졌습니다 (jdk 버전이 맞지않으면 컴파일이 안될 수 있습니다.)**

터미널을 열고 압축을 푼 폴더에서 다음 코드를 입력하여 실행시켜줍니다.
```
./gradlew clean build
java -jar build/libs/myably-0.0.1-SNAPSHOT.jar
```
### 사용 방법
- api 테스트 
  - 해당 링크에서 Example Value (json 형식)을 참고하여 request를 보내면서 테스트 할 수 있습니다. 
  
  - http://localhost:8080/swagger-ui.html#/

- db 확인
    - db에 있는 값을 확인 하려면 해당 링크로 접속하셔서 확인하시면 됩니다 (User Name : sa)

    - http://localhost:8080/h2-console/

### 사용 기술
- Spring boot
- h2
- swagger
- jwt

### 참고 사항
- 실제로 외부 서비스와 연동하여, SMS를 보내도록 구현하지 않아도 된다는 요구 사항이 있어 **휴대폰 인증 코드는 response 값**에 넣어 주었습니다.
- 로그인 식별값은 이메일 + 비밀번호, 닉네임 + 비밀번호, 휴대폰 번호 + 비밀번호 아무거나 가능합니다 (이름은 동명이인이 있을수 있다 가정해서 식별x)




