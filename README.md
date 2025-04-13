# PETTY
🐶 반려동물을 위한 가장 완벽한 여행 | PETTY

### build 및 실행
```shell
./gradlew build # 전체 빌드(컴파일 + 테스트 + jar 생성)
./gradlew bootRun # 빌드 없이 실행
```

---

### 🔐 시크릿 설정 (필수)

이 프로젝트는 **JWT 인증** 및 **DB 연결**을 위해 별도의 시크릿 설정 파일(`application-secret.yml`)이 필요합니다.  
해당 설정은 보안상 Git에 포함되어 있지 않으며, 직접 생성하지 마시고 **담당자에게 요청**해 주세요.