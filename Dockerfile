# 1. 기본 이미지 선택
FROM openjdk:17-jdk-slim

# 2. 작업 디렉터리 설정
WORKDIR /app

# 3. JAR 파일을 컨테이너로 복사
COPY .build/libs/spatz-0.0.1-SNAPSHOT.jar app.jar

# 4. 환경 변수 설정
ENV AWS_ACCESS_KEY_ID=AKIA3FLDZOSMKPOR4A54
ENV AWS_SECRET_ACCESS_KEY=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
ENV CLIENT_ID_GOOGLE=345901738029-3ljh338gljpu1ma93u8aumt1l7l5mrco.apps.googleusercontent.com
ENV CLIENT_SECRET_GOOGLE=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
ENV JWT_SECRET_KEY=1393dc94-cb16-4c0e-933d-3333551e51be
ENV MAIL_SERVER_PASSWORD=dtdxblaouaoxuwrv
ENV MAIL_SERVER_USERNAME=spatzapplication@gmail.com
ENV CLIENT_ID_NAVER=7fmUl_VDVUlabrL0KMWB
ENV CLIENT_SECRET_NAVER=p3QadWvMRr

# 5. 애플리케이션 실행 명령어 설정
CMD ["java", "-jar", "app.jar"]
