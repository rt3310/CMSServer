# 맑은기술 CMS Server

## 프로젝트 실행 방법

### 1. 환경 변수 설정
`.env` 파일을 프로젝트 루트에 생성하고 필요한 환경 변수를 설정합니다.
```bash
# .env
KAKAO_CLIENT_ID=your-kakao-client-id
KAKAO_CLIENT_SECRET=your-kakao-client-secret
KAKAO_REDIRECT_URI={baseUrl}/{action}/oauth2/code/{registrationId}
JWT_SECRET=your-jwt-secret-key-base64-encoded
```

### 2. (방법 1) Local 실행 (Local Java 25 환경 필요)
```bash
# Linux/Mac
export $(cat .env | xargs) && ./gradlew bootRun

# Windows (PowerShell)
Get-Content .env | ForEach-Object { if ($_ -match "^([^=]+)=(.*)$") { [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2]) } }; ./gradlew bootRun
```

### 2. (방법 2) Docker 실행
```bash
# 이미지 빌드
docker build -t cms-server .

# 컨테이너 실행 (.env 파일 참조)
docker run -d -p 8080:8080 --env-file .env --name cms-server cms-server
```

## 구현 사항

### 1. Contents CRUD

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/contents` | 콘텐츠 생성 |
| GET | `/api/v1/contents` | 목록 조회 (Slice 페이징) |
| GET | `/api/v1/contents/{id}` | 상세 조회 (조회수 증가) |
| PUT | `/api/v1/contents/{id}` | 수정 |
| DELETE | `/api/v1/contents/{id}` | 삭제 |

- Slice 기반 페이징 (offset/limit)
- 조회수 증가 시 Race Condition 방지 (atomic UPDATE 쿼리)
- 수정/삭제 권한 검증 (작성자 또는 ADMIN만 가능)

### 2. 인증/인가

- 로그인 방식은 OAuth2(Kakao) + JWT를 선택하여 구현했습니다.
- REST API 서버의 확장성, 다양한 클라이언트 대응을 위해 선택했습니다.

#### 로그인 과정
1. /oauth2/authorize/kakao 로 접속
2. 카카오 로그인 및 동의 과정 진행
3. clientURL(현재 localhost:3000)으로 redirect를 통한 일회성 토큰 발급 http://localhost:3000/auth?token=onceAuthToken 
4. POST /login에 일회성 토큰을 통한 JWT 발급 요청
5. JWT(Access Token, RefreshToken) 응답

#### OAuth2 로그인
- Kakao OAuth2 Provider 지원
- OAuth2 로그인 성공 시 일회성 인증 토큰 발급

#### JWT 인증
- Access Token + Refresh Token 방식
- Access Token 만료: 30분
- Refresh Token 만료: 14일

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | 일회성 토큰으로 JWT 발급 |
| POST | `/api/v1/auth/refresh` | Refresh Token으로 JWT 갱신 |

#### 일회성 인증 토큰 (OnceAuthToken)
- Caffeine Cache 기반 구현
- 30초 후 자동 만료(TTL)
- 일회용 (사용 시 즉시 삭제)

#### Refresh Token 보안
- Bearer prefix 처리
- 낙관적 락으로 Race Condition 방지 (사용자가 직접 재시도하도록 실패 처리)
- 토큰 재사용 감지 시 세션 무효화 (토큰 탈취 대응)

### 3. Rate Limiting

Bucket4j 기반 API 요청 제한으로 무분별한 API 호출을 방지합니다.

**적용 대상:**
- `POST /api/v1/auth/login`

**특징:**
- IP 기반 요청 제한
- 제한 초과 시 429 Too Many Requests 응답
- 재시도 가능 시간 안내

### 4. 전역 예외 처리

`@RestControllerAdvice`를 통한 일관된 에러 응답을 제공합니다.

| Exception | Status | Description |
|-----------|--------|-------------|
| `AppException` | 가변 | 비즈니스 로직 예외 |
| `MethodArgumentNotValidException` | 400 | 입력값 검증 실패 |
| `HttpMessageNotReadableException` | 400 | 요청 본문 파싱 실패 |
| `OptimisticLockException` | 409 | 동시성 충돌 |
| `Exception` | 500 | 서버 에러 |

**응답 형식:**
```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "errorCode": "E400",
    "message": "에러 메시지"
  }
}
```

### 5. Auditing

엔티티 생성/수정 시 자동으로 감사 정보를 기록합니다.

- `createdBy`: 생성자
- `createdDate`: 생성 일시
- `modifiedBy`: 수정자
- `modifiedDate`: 수정 일시

## AI 활용

### 사용한 AI
- Gemini, Claude Code

### 활용 방식
- 기본 코드 틀 작성(CRUD, DTO, Entity 등)
- 누락된 예외 처리 탐지
- 코드 리펙토링