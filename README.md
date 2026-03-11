# 맑은기술 CMS Server

## 프로젝트 실행 방법
```bash
./gradlew bootRun
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
- Caffeine Cache 기반
- 30초 후 자동 만료
- 일회용 (사용 시 즉시 삭제)

#### Refresh Token 보안
- Bearer prefix 처리
- 낙관적 락 (`@Version`)으로 Race Condition 방지
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