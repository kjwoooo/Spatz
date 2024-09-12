# SPATZ
<img width="472" alt="spatz-logo" src="https://github.com/user-attachments/assets/ce756c00-8ca8-4a35-9d2a-d357412f92d7">
<br>


## 팀 소개
- 엘리스 Cloud 트랙 3기 6팀
- 팀원: 강대희, 권재우, 안승우, 우연정, 정동희, 조혜연
- 노션: https://www.notion.so/elice-track/6-564ec5d05b6b4045b5f6b322c61ca903
<br>

## 프로젝트 소개
- 기간: 2024.07.22 ~ 2024.08.17
- 주제: 실시간 채팅, 음성 공유 기능을 제공하는 서비스 개발
- 주요 기능: websocket을 이용한 **실시간 채팅**, webRTC를 통한 **음성 통화** 기능
- 특징: 향후 **MSA 구조로의 전환**을 염두에 두고, 현재 프로젝트는 `user`, `chat` **두 도메인을 독립적으로 수직 분리**하여 개발하였습니다.
<br>

## 🖥️ 서비스 화면
### 회원가입 로그인
![회원가입, 로그인](https://github.com/user-attachments/assets/bc38117c-5f50-4335-82b4-c271234cd797)

### 프로필 설정
![프로필 설정](https://github.com/user-attachments/assets/6e90df9f-cd72-41ec-97ca-2476c75e05a2)

### 채팅
![채팅](https://github.com/user-attachments/assets/62cb6417-cd49-45bd-851a-d90111d2ebc4)

### 친구요청
![친구요청](https://github.com/user-attachments/assets/8338c2cc-656a-4b0e-9b33-c3c06f388ad8)

### 친구수락
![친구수락](https://github.com/user-attachments/assets/b010794f-c18a-46d1-a908-35d6354d1c6e)




## 배포 주소
- <http://elice-build.s3-website.ap-northeast-2.amazonaws.com/>
<br>

## 👉 사용 방법
1. 클론: <https://kdt-gitlab.elice.io/cloud_track/class_03/web_project3/team06/multi-module-project.git>
2. 의존성 설치: gradle 실행
3. 실행: 실행 버튼 클릭
4. 접속: <http://localhost:8080/>
<br>

## 📁 파일 구조
```bash
💻 multi-module-project
├─ src
│  └─ main
│     └─ java/com/elice/spatz
│        ├─ config
│        ├─ constans
│        ├─ domain
│        │  ├─ chat
│        │  ├─ file
│        │  ├─ reaction
│        │  ├─ server
│        │  ├─ serverUser
│        │  ├─ user
│        │  └─ userFeature
│        ├─ entity/baseEntity
│        ├─ exception
│        ├─ filter
│        └─ SpatzApplication.java
├─ Dockerfile
└─ build.gradle
```
<br>


## 기술 스택
<div>
    <img src="https://img.shields.io/badge/openjdk-000000?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
    <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
<div>
    <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
    <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
    <img src="https://img.shields.io/badge/nginx-009639?style=for-the-nginx&logo=nginx&logoColor=white">
    <img src="https://img.shields.io/badge/runner-FC6D26?style=for-the-badge&logoColor=white">
    <img src="https://img.shields.io/badge/websocket-FEEA3F?style=for-the-badge&logoColor=white">  </div>
</div>
<div>
    <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
    <img src="https://img.shields.io/badge/amazonroute53-8C4FFF?style=for-the-badge&logo=amazonroute53&logoColor=white">
    <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
    <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
</div>
<br>






