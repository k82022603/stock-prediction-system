# Pull Request (PR) 완전 가이드
## 바이브코딩 시대의 필수 협업 도구

---

## 📚 목차

1. [PR이란 무엇인가?](#1-pr이란-무엇인가)
2. [왜 PR을 사용하는가?](#2-왜-pr을-사용하는가)
3. [PR의 생명주기](#3-pr의-생명주기)
4. [전통적인 PR 워크플로우](#4-전통적인-pr-워크플로우)
5. [바이브코딩에서의 PR](#5-바이브코딩에서의-pr)
6. [Claude Code로 PR 만들기](#6-claude-code로-pr-만들기)
7. [Cursor로 PR 만들기](#7-cursor로-pr-만들기)
8. [GitHub Copilot으로 PR 만들기](#8-github-copilot으로-pr-만들기)
9. [PR 작성 베스트 프랙티스](#9-pr-작성-베스트-프랙티스)
10. [PR 리뷰 가이드](#10-pr-리뷰-가이드)
11. [실전 시나리오](#11-실전-시나리오)
12. [FAQ](#12-faq)

---

## 1. PR이란 무엇인가?

### 1.1 정의

**Pull Request (PR)** = "내가 작성한 코드를 메인 브랜치에 병합(merge)해주세요"라는 요청

다른 말로:
- GitHub, GitLab: **Pull Request (PR)**
- Bitbucket: **Pull Request (PR)**
- Azure DevOps: **Pull Request (PR)**

> 💡 **참고:** GitLab에서는 "Merge Request (MR)"이라고 부르기도 하지만, 의미는 같습니다.

### 1.2 비유로 이해하기

#### 🏢 회사 문서 작업으로 비유

```
상황: 회사의 공식 매뉴얼을 수정해야 함

전통적 방식:
1. 원본 문서를 직접 수정
2. 실수하면 원본 손상
3. 여러 사람이 동시 작업 시 충돌

PR 방식:
1. 원본을 복사 (Branch 생성)
2. 복사본을 수정
3. 상사에게 "제가 이렇게 수정했는데 검토해주세요" (PR 생성)
4. 상사가 검토 후 승인
5. 승인되면 원본에 반영 (Merge)
```

#### 🎓 논문 작성으로 비유

```
1. 교수님의 원본 논문 (main branch)
2. 학생이 복사본을 받음 (git clone)
3. 학생이 새 챕터 작성 (feature branch)
4. "3장을 작성했습니다. 검토해주세요" (PR 생성)
5. 교수님이 검토 및 피드백 (Code Review)
6. 수정 후 승인 (Approve)
7. 원본 논문에 추가 (Merge)
```

### 1.3 핵심 개념

| 용어 | 설명 | 예시 |
|------|------|------|
| **Source Branch** | 수정한 코드가 있는 브랜치 | `feature/add-login` |
| **Target Branch** | 병합하려는 대상 브랜치 | `main` 또는 `develop` |
| **Commit** | 저장된 코드 변경사항 | "Add login API" |
| **Review** | 코드 검토 | "여기 버그 있어요" |
| **Approve** | 승인 | "좋습니다, 병합하세요" |
| **Merge** | 병합 | 코드가 main으로 합쳐짐 |

---

## 2. 왜 PR을 사용하는가?

### 2.1 코드 품질 보장

#### ❌ PR 없이 개발

```java
// 개발자 A가 직접 main에 커밋
public void login(String username, String password) {
    // SQL Injection 취약점!
    String sql = "SELECT * FROM users WHERE username='" + username + "'";
    // 아무도 검토 안 함 → 프로덕션 배포 → 보안 사고
}
```

#### ✅ PR로 개발

```java
// 개발자 A가 feature 브랜치에 커밋
public void login(String username, String password) {
    String sql = "SELECT * FROM users WHERE username='" + username + "'";
}

// PR 생성 → 개발자 B가 리뷰
개발자 B: "SQL Injection 취약점이 있습니다. PreparedStatement를 사용하세요."

// 개발자 A가 수정
public void login(String username, String password) {
    String sql = "SELECT * FROM users WHERE username=?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, username);
    // ...
}

// 승인 후 병합 → 안전한 코드가 프로덕션에 배포
```

### 2.2 지식 공유

**Before PR:**
```
개발자 A만 이 코드를 알고 있음
↓
개발자 A 퇴사
↓
코드 유지보수 불가능
```

**With PR:**
```
개발자 A가 PR 생성
↓
개발자 B, C, D가 리뷰하며 코드 이해
↓
개발자 A 퇴사해도
↓
팀 전체가 코드를 이해하고 있음
```

### 2.3 버그 조기 발견

| 버그 발견 시점 | 수정 비용 | 예시 |
|----------------|-----------|------|
| 개발 중 (PR 리뷰) | **1x** | $100 |
| QA 단계 | 10x | $1,000 |
| 스테이징 | 50x | $5,000 |
| 프로덕션 | 100-1000x | $10,000 - $100,000 |

### 2.4 협업 효율 증대

**시나리오: 2명이 같은 파일을 동시에 수정**

#### ❌ PR 없이

```
개발자 A: UserService.java 수정 → main에 push
개발자 B: UserService.java 수정 → main에 push
↓
충돌 발생! 💥
↓
누가 먼저 push했는지도 모름
수정 내역이 뒤엉킴
```

#### ✅ PR로

```
개발자 A: feature/user-profile 브랜치에서 작업 → PR 생성
개발자 B: feature/user-settings 브랜치에서 작업 → PR 생성
↓
독립적으로 작업 가능
↓
각자의 PR을 순차적으로 병합
↓
충돌이 있어도 명확하게 해결 가능
```

### 2.5 배포 안전성

**Continuous Deployment with PR:**

```
PR 생성
  ↓
자동 테스트 실행 (CI)
  ↓
테스트 실패? → 병합 불가
  ↓
테스트 성공? → 리뷰 진행
  ↓
승인 후 병합
  ↓
자동 배포 (CD)
```

---

## 3. PR의 생명주기

### 3.1 전체 흐름

```
┌─────────────────────────────────────────────────────────┐
│                   1. Branch 생성                        │
│  main → feature/add-login                              │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   2. 코드 작성                          │
│  - LoginController.java 생성                           │
│  - LoginService.java 생성                              │
│  - 테스트 코드 작성                                     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   3. Commit & Push                      │
│  git add .                                             │
│  git commit -m "Add login feature"                     │
│  git push origin feature/add-login                     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   4. PR 생성                            │
│  GitHub에서 "New Pull Request" 버튼 클릭                │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   5. 자동 검사                          │
│  - CI 테스트 실행                                       │
│  - 코드 분석 (SonarQube)                               │
│  - 보안 스캔                                           │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   6. Code Review                        │
│  팀원들이 코드 검토 및 피드백                            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   7. 수정 반영                          │
│  피드백에 따라 코드 수정 후 다시 push                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   8. Approve                            │
│  리뷰어들이 승인                                        │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   9. Merge                              │
│  feature/add-login → main                              │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   10. Branch 삭제                       │
│  feature/add-login 브랜치 삭제 (선택)                   │
└─────────────────────────────────────────────────────────┘
```

### 3.2 상태 다이어그램

```
    [Draft]
       ↓
  [Open] ←──────────┐
       ↓            │
  [In Review]       │
       ↓            │
  [Changes Requested]
       │
       ↓
  [Approved]
       ↓
  [Merged] → [Closed]
```

**상태 설명:**

| 상태 | 설명 | 다음 액션 |
|------|------|-----------|
| **Draft** | 작성 중, 리뷰 요청 전 | "Ready for review" 클릭 |
| **Open** | 리뷰 대기 중 | 리뷰어 지정 |
| **In Review** | 리뷰 진행 중 | 피드백 기다림 |
| **Changes Requested** | 수정 요청됨 | 코드 수정 후 push |
| **Approved** | 승인됨 | Merge 버튼 클릭 |
| **Merged** | 병합 완료 | 브랜치 삭제 |
| **Closed** | PR 종료 (병합 안 함) | - |

---

## 4. 전통적인 PR 워크플로우

### 4.1 터미널 명령어로 전체 과정

```bash
# Step 1: 저장소 클론 (최초 1회)
git clone https://github.com/username/stock-prediction-system.git
cd stock-prediction-system

# Step 2: 최신 코드 받기
git checkout main
git pull origin main

# Step 3: 새 브랜치 생성
git checkout -b feature/add-portfolio-api

# Step 4: 코드 작성
# (여기서 실제 코딩 작업)
# - PortfolioController.java 생성
# - PortfolioService.java 생성
# - 테스트 코드 작성

# Step 5: 변경 사항 확인
git status
# 출력:
# modified:   backend/src/main/java/com/stock/controller/PortfolioController.java
# new file:   backend/src/main/java/com/stock/service/PortfolioService.java

# Step 6: 스테이징
git add .
# 또는 특정 파일만
git add backend/src/main/java/com/stock/controller/PortfolioController.java

# Step 7: 커밋
git commit -m "feat: Add portfolio creation API

- Implement POST /api/portfolios endpoint
- Add PortfolioService business logic
- Write unit tests for PortfolioService"

# Step 8: 원격 저장소에 푸시
git push origin feature/add-portfolio-api

# 출력:
# To https://github.com/username/stock-prediction-system.git
#  * [new branch]      feature/add-portfolio-api -> feature/add-portfolio-api
# remote: 
# remote: Create a pull request for 'feature/add-portfolio-api' on GitHub by visiting:
# remote:      https://github.com/username/stock-prediction-system/pull/new/feature/add-portfolio-api

# Step 9: GitHub에서 PR 생성
# 위 링크를 브라우저에서 열거나, GitHub 웹사이트에서 "New Pull Request" 버튼 클릭
```

### 4.2 GitHub 웹에서 PR 생성

#### 화면 구성

```
┌─────────────────────────────────────────────────────────┐
│  Compare changes                                        │
│                                                         │
│  base: main    ←    compare: feature/add-portfolio-api │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │ Title: Add portfolio creation API               │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │ ## Description                                  │   │
│  │ Implements portfolio creation feature           │   │
│  │                                                 │   │
│  │ ## Changes                                      │   │
│  │ - Added PortfolioController                     │   │
│  │ - Added PortfolioService                        │   │
│  │ - Added unit tests                              │   │
│  │                                                 │   │
│  │ ## Testing                                      │   │
│  │ - Tested with Postman                           │   │
│  │ - All unit tests pass                           │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  Reviewers:    [@john]  [@sarah]                       │
│  Assignees:    [@me]                                   │
│  Labels:       [enhancement] [backend]                 │
│  Projects:     [Sprint 5]                              │
│  Milestone:    [v1.2.0]                                │
│                                                         │
│  [Create Pull Request]                                 │
└─────────────────────────────────────────────────────────┘
```

#### PR 제목 작성 팁

**❌ 나쁜 예시:**
```
update code
fix bug
changes
```

**✅ 좋은 예시:**
```
feat: Add portfolio creation API
fix: Fix null pointer exception in StockService
refactor: Extract duplicate code in PredictionController
docs: Update API documentation for predictions endpoint
```

**형식:** `<type>: <subject>`

**Type 종류:**
- `feat`: 새 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `docs`: 문서
- `test`: 테스트
- `chore`: 빌드, 설정
- `style`: 코드 포맷팅

#### PR 설명 템플릿

```markdown
## Description
이 PR이 무엇을 하는지 간단히 설명

## Changes
- 변경사항 1
- 변경사항 2
- 변경사항 3

## Type of Change
- [ ] 새 기능 (feat)
- [ ] 버그 수정 (fix)
- [ ] Breaking change (기존 기능에 영향)
- [ ] 문서 업데이트

## Testing
어떻게 테스트했는지 설명
- [ ] 단위 테스트 추가
- [ ] 통합 테스트 통과
- [ ] 수동 테스트 완료

## Screenshots (if applicable)
스크린샷 첨부 (UI 변경 시)

## Checklist
- [ ] 코드가 스타일 가이드를 따름
- [ ] 자체 코드 리뷰 완료
- [ ] 주석 추가 (복잡한 로직)
- [ ] 문서 업데이트
- [ ] 테스트 추가
- [ ] 모든 테스트 통과
- [ ] 의존성 변경 사항 명시

## Related Issues
Closes #123
Related to #456
```

---

## 5. 바이브코딩에서의 PR

### 5.1 전통 vs 바이브코딩

| 단계 | 전통 개발 | 바이브코딩 |
|------|-----------|------------|
| **코드 작성** | 수동 타이핑 (1-2일) | AI에게 요청 (10분) |
| **테스트 작성** | 수동 작성 (2-4시간) | AI가 자동 생성 (5분) |
| **문서 작성** | 수동 작성 (1-2시간) | AI가 자동 생성 (2분) |
| **PR 생성** | 수동 (10분) | AI가 도움 (3분) |
| **코드 리뷰** | 수동 (1-2시간) | AI 사전 리뷰 (5분) |

### 5.2 바이브코딩 PR의 특징

#### 특징 1: AI가 생성한 코드도 리뷰 필요

```
❌ 잘못된 생각:
"AI가 만든 코드니까 완벽해. 리뷰 안 해도 돼."

✅ 올바른 생각:
"AI가 만든 코드도 반드시 리뷰해야 해.
 - 비즈니스 로직이 맞는지
 - 보안 취약점은 없는지
 - 성능 문제는 없는지"
```

#### 특징 2: PR 설명이 더 중요

```markdown
## AI 프롬프트
Claude Code에게 다음과 같이 요청했습니다:
> "포트폴리오 생성 API를 만들어줘. 
>  사용자 인증 필요하고, 이름과 설명을 입력받아."

## 생성된 코드
- PortfolioController.java
- PortfolioService.java
- PortfolioMapper.java
- PortfolioMapper.xml
- PortfolioServiceTest.java

## 수동 수정 사항
- PortfolioService에 중복 체크 로직 추가
- 에러 메시지 한글로 변경
```

#### 특징 3: 더 큰 PR

전통 개발:
```
PR 크기: 100-200 줄
이유: 수동 타이핑이라 적게 작성
```

바이브코딩:
```
PR 크기: 500-1000 줄
이유: AI가 빠르게 생성
```

**해결책:** PR을 논리적 단위로 분리

```
❌ 하나의 거대한 PR:
"Add entire user management system" (2000줄)

✅ 여러 개의 작은 PR:
1. "Add User model and mapper" (200줄)
2. "Add UserService business logic" (300줄)
3. "Add AuthController endpoints" (250줄)
4. "Add JWT authentication" (400줄)
```

---

## 6. Claude Code로 PR 만들기

### 6.1 Claude Code 소개

**Claude Code:**
- 브라우저 또는 데스크톱 앱
- Claude 3.5 Sonnet 모델
- 자연어로 코드 생성
- 프로젝트 전체 이해

### 6.2 전체 워크플로우

```
Step 1: Claude Code에게 기능 요청
  ↓
Step 2: Claude가 코드 생성
  ↓
Step 3: 로컬에서 테스트
  ↓
Step 4: Git 명령어로 커밋
  ↓
Step 5: GitHub에서 PR 생성
  ↓
Step 6: Claude에게 PR 설명 작성 요청
```

### 6.3 실전 예시

#### Step 1: Claude에게 기능 요청

```
나: "포트폴리오 생성 API를 만들어줘.

요구사항:
1. POST /api/portfolios
2. Request: { name, description }
3. Response: Portfolio 객체
4. JWT 인증 필요
5. 포트폴리오 이름 중복 체크
6. MyBatis 사용
7. 테스트 코드 포함

기존 StockController 스타일을 따라가."
```

#### Step 2: Claude가 코드 생성

Claude가 생성한 파일:
```
✅ PortfolioController.java
✅ PortfolioService.java
✅ PortfolioMapper.java
✅ PortfolioMapper.xml
✅ Portfolio.java (Model)
✅ PortfolioDTO.java
✅ PortfolioServiceTest.java
```

#### Step 3: 코드 검토 및 수정

```
나: "PortfolioService에서 이름 중복 체크 로직을 
     더 명확하게 작성해줘."

Claude: [코드 수정]

나: "테스트 커버리지를 80% 이상으로 만들어줘."

Claude: [테스트 추가]
```

#### Step 4: Git 커밋

```bash
# 터미널에서 실행
git checkout -b feature/add-portfolio-api
git add .
git commit -m "feat: Add portfolio creation API

- Implement POST /api/portfolios endpoint
- Add PortfolioService with duplicate check
- Add PortfolioMapper and XML
- Write unit tests with 85% coverage

Generated with Claude Code"

git push origin feature/add-portfolio-api
```

#### Step 5: Claude에게 PR 설명 요청

```
나: "GitHub PR 설명을 작성해줘. 
     방금 만든 포트폴리오 생성 API에 대해."

Claude: "다음과 같이 작성하시면 좋습니다:

## Description
사용자가 자신의 주식 포트폴리오를 생성할 수 있는 API를 추가했습니다.

## Changes
- **PortfolioController**: POST /api/portfolios 엔드포인트 추가
- **PortfolioService**: 포트폴리오 생성 및 중복 체크 로직
- **PortfolioMapper**: MyBatis 매퍼 인터페이스 및 XML
- **Portfolio**: 도메인 모델
- **PortfolioServiceTest**: 85% 커버리지 단위 테스트

## Type of Change
- [x] 새 기능 (feat)

## Testing
- [x] 단위 테스트 추가 (PortfolioServiceTest)
- [x] Postman으로 API 수동 테스트
- [x] 이름 중복 시나리오 테스트
- [x] JWT 인증 테스트

## API Specification
```json
POST /api/portfolios
Authorization: Bearer <token>

Request:
{
  \"name\": \"내 포트폴리오\",
  \"description\": \"장기 투자용\"
}

Response: 201 Created
{
  \"id\": 1,
  \"userId\": 123,
  \"name\": \"내 포트폴리오\",
  \"description\": \"장기 투자용\",
  \"isDefault\": false,
  \"createdAt\": \"2025-12-07T00:00:00\"
}
```

## Checklist
- [x] 코드 스타일 가이드 준수
- [x] 자체 코드 리뷰 완료
- [x] 복잡한 로직에 주석 추가
- [x] 테스트 작성 및 통과
- [x] JWT 인증 적용

## Generated by
Claude Code (Claude 3.5 Sonnet)
"
```

#### Step 6: GitHub에서 PR 생성

1. GitHub 저장소로 이동
2. "Pull requests" 탭 클릭
3. "New pull request" 버튼
4. base: `main` ← compare: `feature/add-portfolio-api` 선택
5. Title: `feat: Add portfolio creation API`
6. Description: Claude가 작성한 내용 붙여넣기
7. Reviewers 지정
8. "Create pull request" 클릭

### 6.4 Claude Code의 장점

✅ **코드 생성 속도**
- 전통: 1-2일 → Claude: 10분

✅ **일관성**
- 프로젝트 스타일 자동 유지

✅ **테스트 코드 자동 생성**
- 귀찮은 테스트 코드도 함께 생성

✅ **문서 작성 지원**
- PR 설명, API 문서 자동 생성

### 6.5 Claude Code 사용 팁

#### Tip 1: 명확한 요구사항

❌ 나쁜 프롬프트:
```
"포트폴리오 기능 만들어줘"
```

✅ 좋은 프롬프트:
```
"포트폴리오 생성 API를 만들어줘.

요구사항:
1. REST API: POST /api/portfolios
2. 인증: JWT 필요 (SecurityContext에서 userId 가져오기)
3. 입력: name (필수, 최대 100자), description (선택, 최대 500자)
4. 검증: 동일 사용자의 포트폴리오 이름 중복 불가
5. 응답: 생성된 Portfolio 객체 (201 Created)
6. 기술: Spring Boot, MyBatis
7. 테스트: JUnit 5, Mockito
8. 스타일: 기존 StockController와 동일하게

참고 파일:
- StockController.java
- StockService.java
- StockMapper.java"
```

#### Tip 2: 단계적 개선

```
1차 요청: "기본 CRUD 만들어줘"
Claude: [기본 코드 생성]

2차 요청: "validation 추가해줘"
Claude: [validation 추가]

3차 요청: "예외 처리 강화해줘"
Claude: [예외 처리 추가]

4차 요청: "테스트 커버리지 80% 이상으로"
Claude: [테스트 추가]
```

#### Tip 3: 기존 코드 참조

```
"@StockController.java의 스타일을 따라서
 PortfolioController를 만들어줘."
```

---

## 7. Cursor로 PR 만들기

### 7.1 Cursor 소개

**Cursor:**
- VSCode fork
- GPT-4 통합
- 프로젝트 전체 인덱싱
- 인라인 코드 편집
- $20/월

### 7.2 Cursor 설치 및 설정

```bash
# macOS
brew install --cask cursor

# Windows
# https://cursor.sh 에서 다운로드
```

**초기 설정:**
1. Cursor 실행
2. Settings → Cursor Settings
3. API Keys → OpenAI API Key 입력
4. 프로젝트 열기 → 자동 인덱싱

### 7.3 Cursor로 PR 만드는 전체 과정

#### Step 1: 새 브랜치 생성

Cursor 터미널 (Ctrl+`):
```bash
git checkout -b feature/add-portfolio-api
```

#### Step 2: Cursor Composer로 코드 생성

**Cmd+I (macOS) 또는 Ctrl+I (Windows)**

```
프롬프트:
"포트폴리오 생성 API를 구현해줘.

필요한 파일:
1. Portfolio.java (model)
2. PortfolioMapper.java + XML
3. PortfolioService.java
4. PortfolioController.java
5. PortfolioServiceTest.java

요구사항:
- POST /api/portfolios
- JWT 인증 필요
- 이름 중복 체크
- MyBatis 사용
- 기존 StockController 스타일 유지"
```

Cursor가 5개 파일 모두 생성!

#### Step 3: 인라인 수정 (Cmd+K)

특정 코드 블록 선택 → **Cmd+K**

```
"이 메서드에 validation 추가해줘.
 - name은 필수, 최대 100자
 - description은 선택, 최대 500자"
```

Cursor가 해당 부분만 수정!

#### Step 4: AI Chat으로 리뷰 (Cmd+L)

**Cmd+L** → Chat 열기

```
나: "방금 작성한 PortfolioService를 리뷰해줘.
     보안 취약점이나 버그가 있는지 확인해."

Cursor: "코드를 검토한 결과:

✅ 좋은 점:
- @Transactional 적절히 사용
- 이름 중복 체크 로직 정확
- 예외 처리 적절

⚠️ 개선 필요:
1. Line 45: userId가 null일 수 있음
   → Optional.ofNullable() 사용 권장
   
2. Line 67: 동시성 문제 가능
   → 데이터베이스 Unique 제약조건 추가 권장

수정하시겠습니까?"

나: "네, 수정해줘"

Cursor: [코드 자동 수정]
```

#### Step 5: Git 커밋 (Source Control)

Cursor 왼쪽 사이드바 → Source Control (Ctrl+Shift+G)

```
1. "+" 버튼으로 변경 파일 모두 스테이징
2. Commit message 입력:
   "feat: Add portfolio creation API
   
   - Implement POST /api/portfolios endpoint
   - Add PortfolioService with duplicate check
   - Add MyBatis mapper
   - Write unit tests
   
   Generated with Cursor AI"
3. ✓ 버튼 클릭 (Commit)
4. "..." → Push 클릭
```

#### Step 6: Cursor로 PR 설명 생성

**Cmd+L** → Chat

```
나: "GitHub PR 설명을 작성해줘. 
     Markdown 형식으로."

Cursor: [PR 설명 생성]
```

#### Step 7: GitHub에서 PR 생성

Cursor에서 생성한 PR 설명을 GitHub에 붙여넣기

### 7.4 Cursor의 고급 기능

#### ① @-mentions로 파일 참조

```
Cmd+K

"@StockController.java의 스타일을 따라서
 @PortfolioController.java를 수정해줘."
```

#### ② Codebase 검색

```
Cmd+K

"이 프로젝트에서 JWT 인증 관련 코드를 찾아서
 PortfolioController에 동일하게 적용해줘."
```

#### ③ 실시간 코드 완성

타이핑하면 자동으로 AI가 다음 코드를 제안

```java
public void createPortfolio(Long userId, String name) {
    // 커서가 여기 있으면 Cursor가 자동 제안:
    // Portfolio portfolio = Portfolio.builder()
    //     .userId(userId)
    //     .name(name)
    //     .build();
    // portfolioMapper.insert(portfolio);
}
```

Tab 키로 제안 수락!

#### ④ 디버깅 지원

```
Cmd+L

"이 에러를 분석해줘:
java.lang.NullPointerException
    at PortfolioService.createPortfolio(PortfolioService.java:45)"

Cursor: "PortfolioService.java의 45번 라인에서
         userId가 null입니다. 
         
해결 방법:
1. userId null 체크 추가
2. @AuthenticationPrincipal 제대로 설정됐는지 확인

수정하시겠습니까?"
```

### 7.5 Cursor 사용 팁

#### Tip 1: .cursorrules 파일

프로젝트 루트에 `.cursorrules` 파일 생성:

```
# Stock Prediction System - Cursor Rules

## 기술 스택
- Backend: Spring Boot 3.2, MyBatis 3.0, Java 17
- Frontend: React 18
- Database: PostgreSQL 15

## 코딩 스타일
- Java: Google Java Style Guide
- Indentation: 4 spaces
- Line length: 120

## 명명 규칙
- Controller: {Domain}Controller
- Service: {Domain}Service
- Mapper: {Domain}Mapper
- API: /api/{resource}

## 테스트
- All Service methods must have tests
- Use given-when-then pattern
- Minimum 80% coverage

## MyBatis
- Use snake_case for columns
- Use ResultMap for mapping
- @Param required for multiple parameters

## 금지 사항
- Don't use raw SQL in Java code
- Don't use System.out.println
- Don't commit commented code
```

#### Tip 2: 코드 리팩토링

코드 블록 선택 → **Cmd+K**

```
"이 중복 코드를 제거하고 공통 메서드로 추출해줘"
```

#### Tip 3: 전체 파일 리뷰

**Cmd+L**

```
"현재 프로젝트의 모든 Controller를 리뷰하고
 일관성 문제나 개선점을 찾아줘"
```

---

## 8. GitHub Copilot으로 PR 만들기

### 8.1 GitHub Copilot 소개

**GitHub Copilot:**
- Microsoft 제품
- GPT-4 기반
- VSCode, Visual Studio, JetBrains IDE 지원
- $10/월 (개인), $19/월 (비즈니스)

### 8.2 설치 및 설정

#### VSCode에 설치

1. Extensions (Ctrl+Shift+X)
2. "GitHub Copilot" 검색
3. Install
4. GitHub 계정으로 로그인
5. 구독 활성화

#### JetBrains IDE에 설치

1. Settings → Plugins
2. "GitHub Copilot" 검색
3. Install
4. GitHub 계정으로 로그인

### 8.3 Copilot으로 PR 만들기

#### Step 1: 브랜치 생성

```bash
git checkout -b feature/add-portfolio-api
```

#### Step 2: 주석으로 의도 표현

```java
// PortfolioController.java

/**
 * Portfolio creation API
 * POST /api/portfolios
 * Request: { name: string, description: string }
 * Response: Portfolio object
 * Auth: JWT required
 * Validation: name is required, max 100 chars
 */
@PostMapping
public ResponseEntity<Portfolio> createPortfolio() {
    // Copilot이 자동으로 제안:
    // @AuthenticationPrincipal UserDetails userDetails,
    // @RequestBody @Valid PortfolioDTO dto
}
```

Tab으로 제안 수락!

#### Step 3: Copilot Chat으로 코드 생성

**Ctrl+I** (인라인 Chat)

```
프롬프트:
"Create PortfolioService with createPortfolio method.
 Use MyBatis mapper.
 Check for duplicate portfolio names."

Copilot: [전체 Service 클래스 생성]
```

#### Step 4: 테스트 코드 생성

```java
// PortfolioServiceTest.java

// 주석만 작성하면 Copilot이 테스트 생성:

// Test createPortfolio with valid input
@Test
void createPortfolio_WithValidInput_ShouldCreateSuccessfully() {
    // Copilot이 given-when-then 자동 생성
}
```

#### Step 5: Copilot Chat으로 PR 설명 생성

**Ctrl+Shift+I** (Chat 패널)

```
프롬프트:
"Generate GitHub PR description for the portfolio creation feature.
 Include description, changes, testing, and checklist."

Copilot: [PR 설명 생성]
```

### 8.4 GitHub Copilot의 특징

#### 장점

✅ **IDE 통합**
- VSCode, IntelliJ, Visual Studio 모두 지원

✅ **빠른 코드 완성**
- 타이핑하면 실시간 제안

✅ **다국어 지원**
- 주석을 한글로 써도 코드 생성 가능

```java
// 포트폴리오 생성 서비스
public class PortfolioService {
    // Copilot이 한글 주석을 이해하고 코드 생성
}
```

✅ **GitHub 통합**
- GitHub Actions, Issues와 연동

#### 단점

⚠️ **컨텍스트 제한**
- Cursor나 Claude Code보다 프로젝트 이해도 낮음

⚠️ **비용**
- Cursor와 Claude Code보다 비쌈

### 8.5 Copilot 사용 팁

#### Tip 1: 명확한 주석

```java
// ❌ 나쁜 주석
// create

// ✅ 좋은 주석
// Create a new portfolio for the authenticated user
// Validate that portfolio name is unique
// Return 201 Created with portfolio object
```

#### Tip 2: Ghost Text 활용

타이핑 중 Copilot의 회색 제안(Ghost Text)을 확인:
- **Tab**: 전체 제안 수락
- **Ctrl+→**: 단어 단위로 수락
- **Esc**: 제안 거부

#### Tip 3: Copilot Labs

```
1. Copilot Labs extension 설치
2. 추가 기능:
   - Explain: 코드 설명
   - Translate: 언어 간 변환
   - Readable: 가독성 개선
   - Fix: 버그 수정
   - Debug: 디버깅
   - Test: 테스트 생성
```

---

## 9. PR 작성 베스트 프랙티스

### 9.1 작은 PR

#### ❌ 나쁜 예: 거대한 PR

```
Title: Implement entire user management system

Changes:
- 50 files changed
- 3,500 lines added
- 1,200 lines deleted

리뷰어 반응:
"이거 리뷰하는 데 3시간 걸렸어요... 😭"
```

#### ✅ 좋은 예: 작은 PR들

```
PR 1: Add User model and database schema
- 3 files changed, 150 lines

PR 2: Add UserService business logic
- 5 files changed, 250 lines

PR 3: Add AuthController endpoints
- 4 files changed, 200 lines

PR 4: Add JWT authentication filter
- 6 files changed, 300 lines

각 PR을 30분 안에 리뷰 가능!
```

### 9.2 명확한 제목과 설명

#### 제목 작성 규칙

```
<type>(<scope>): <subject>

예시:
feat(portfolio): Add portfolio creation API
fix(auth): Fix JWT token expiration bug
refactor(stock): Extract duplicate code in StockService
docs(api): Update API documentation for predictions
test(portfolio): Add integration tests for portfolio
```

#### 설명 구조

```markdown
## 🎯 What (무엇을)
이 PR이 무엇을 하는지 한 문장으로

## 🤔 Why (왜)
왜 이 변경이 필요한지

## 🔨 How (어떻게)
어떻게 구현했는지

## 📸 Screenshots (선택)
UI 변경 시 스크린샷

## ✅ Testing
어떻게 테스트했는지

## 📝 Checklist
- [ ] 테스트 추가
- [ ] 문서 업데이트
- [ ] 코드 리뷰 완료
```

### 9.3 자체 리뷰 먼저

PR 생성 전에 자신이 먼저 리뷰:

```
체크리스트:
1. [ ] 불필요한 console.log 제거
2. [ ] 주석 처리된 코드 제거
3. [ ] TODO 주석 확인
4. [ ] 오타 확인
5. [ ] 커밋 메시지 확인
6. [ ] 테스트 전부 통과
7. [ ] Lint 에러 없음
8. [ ] 의도치 않은 변경사항 없음
```

### 9.4 의미 있는 커밋 메시지

#### ❌ 나쁜 커밋

```bash
git commit -m "update"
git commit -m "fix bug"
git commit -m "changes"
git commit -m "asdfasdf"
```

#### ✅ 좋은 커밋

```bash
git commit -m "feat: Add portfolio creation API

- Implement POST /api/portfolios endpoint
- Add PortfolioService with duplicate name check
- Add MyBatis mapper interface and XML
- Write unit tests with 85% coverage

The portfolio creation feature allows users to organize
their stock holdings into multiple portfolios.

Closes #123"
```

**커밋 메시지 구조:**
```
<type>: <subject>  ← 50자 이내

<body>  ← 72자마다 줄바꿈
        ← 왜 변경했는지, 어떻게 변경했는지 설명

<footer>  ← Issue 번호, Breaking changes
```

### 9.5 리뷰어 배려

#### ① 컨텍스트 제공

```markdown
## Context
사용자들이 여러 포트폴리오를 관리하고 싶다는 요청이 많았습니다.
기존에는 하나의 전역 목록만 제공했는데, 이제 개인별 포트폴리오를
만들 수 있게 됩니다.

## Design Decisions
1. **이름 중복 체크**: 동일 사용자 내에서만 체크
   - 다른 사용자는 같은 이름 사용 가능
   
2. **기본 포트폴리오**: 첫 생성 시 자동으로 기본 설정
   - 이후 사용자가 변경 가능

3. **Soft Delete**: 삭제 시 실제로 지우지 않고 플래그만 변경
   - 데이터 복구 가능성 고려
```

#### ② 리뷰 요청 영역 표시

```java
// PortfolioService.java

public Portfolio createPortfolio(Long userId, String name) {
    // ⚠️ REVIEW NEEDED: 이 중복 체크 로직이 충분한지 확인해주세요
    Optional<Portfolio> existing = portfolioMapper
        .findByUserIdAndName(userId, name);
    
    if (existing.isPresent()) {
        throw new DuplicatePortfolioException(
            "Portfolio with name '" + name + "' already exists");
    }
    
    // ...
}
```

#### ③ 스크린샷 첨부 (UI 변경 시)

```markdown
## Before
![image](before.png)

## After
![image](after.png)

## Mobile View
![image](mobile.png)
```

---

## 10. PR 리뷰 가이드

### 10.1 리뷰어의 역할

#### 확인 사항

1. **기능성**
   - 요구사항을 충족하는가?
   - 버그가 없는가?
   - Edge case를 고려했는가?

2. **코드 품질**
   - 가독성이 좋은가?
   - 중복 코드가 없는가?
   - 네이밍이 명확한가?

3. **테스트**
   - 테스트가 충분한가?
   - 테스트가 실제로 의미 있는가?

4. **보안**
   - SQL Injection 취약점
   - XSS 취약점
   - 인증/인가 적절한가?

5. **성능**
   - N+1 쿼리 문제
   - 불필요한 DB 호출
   - 메모리 누수

### 10.2 리뷰 코멘트 작성법

#### ① 명확하고 친절하게

❌ 나쁜 코멘트:
```
"이거 잘못됐어요"
"왜 이렇게 했어요?"
"이해가 안 돼요"
```

✅ 좋은 코멘트:
```
"여기서 null 체크가 필요할 것 같습니다. 
 userId가 null일 경우 NullPointerException이 발생할 수 있습니다.
 
 제안:
 if (userId == null) {
     throw new IllegalArgumentException("User ID cannot be null");
 }"

"이 로직이 복잡해서 이해하기 어려울 것 같습니다.
 메서드로 추출하면 어떨까요?
 
 예시:
 private boolean isDuplicatePortfolio(Long userId, String name) {
     // ...
 }"
```

#### ② 코드로 제안

```java
Suggestion:

// 기존 코드
if (portfolio == null || portfolio.getName() == null) {
    return false;
}

// 제안 코드
if (portfolio == null || StringUtils.isBlank(portfolio.getName())) {
    return false;
}
```

#### ③ 우선순위 표시

```
🔴 Blocker: 이 문제는 반드시 수정되어야 합니다.
SQL Injection 취약점이 있습니다.

🟡 Major: 중요한 이슈입니다.
이 로직은 동시성 문제를 일으킬 수 있습니다.

🟢 Minor: 작은 개선 사항입니다.
변수명을 더 명확하게 바꾸면 좋을 것 같습니다.

💡 Nit: 사소한 스타일 문제입니다.
들여쓰기가 일관되지 않습니다.
```

### 10.3 Approve 기준

#### ✅ Approve 조건

```
1. 기능이 제대로 작동함
2. 테스트가 충분함
3. 코드 품질이 팀 기준을 만족함
4. 보안 취약점이 없음
5. 성능 문제가 없음
6. 문서가 업데이트됨
```

#### ⚠️ Changes Requested 조건

```
1. 버그가 있음
2. 보안 취약점
3. 성능 문제
4. 테스트 부족
5. 코드 품질 문제 (심각한 경우)
```

### 10.4 리뷰 템플릿

```markdown
## Summary
전체적으로 잘 작성되었습니다! 
포트폴리오 생성 기능이 요구사항을 충족합니다.

## 👍 Good
- 이름 중복 체크 로직이 명확합니다
- 테스트 커버리지가 85%로 높습니다
- API 문서가 잘 작성되어 있습니다

## 🔍 Issues
### 🔴 Blocker
- [ ] Line 45: SQL Injection 취약점 수정 필요

### 🟡 Major
- [ ] Line 67: 동시성 문제 가능성
- [ ] Line 89: null 체크 누락

### 🟢 Minor
- [ ] Line 102: 변수명 개선 (p → portfolio)
- [ ] Line 156: 주석 추가 권장

## 💡 Suggestions
1. PortfolioService 메서드가 너무 깁니다 (50줄)
   → 작은 메서드로 분리 권장
   
2. 에러 메시지를 상수로 관리하면 좋을 것 같습니다

## Questions
1. 포트폴리오 개수 제한이 있나요?
2. 삭제된 포트폴리오 복구 기능을 고려하셨나요?

## Verdict
Major 이슈들이 수정되면 Approve 하겠습니다.
```

---

## 11. 실전 시나리오

### 11.1 시나리오 1: 단순 기능 추가

**상황:** 주식 목록에 "즐겨찾기" 기능 추가

#### Claude Code 사용

```
Step 1: Claude에게 요청
"주식 목록에 즐겨찾기 기능을 추가해줘.
 - 사용자별로 즐겨찾기 관리
 - POST /api/favorites/{stockId} (추가)
 - DELETE /api/favorites/{stockId} (제거)
 - GET /api/favorites (목록)
 - MyBatis 사용"

Step 2: Claude가 생성
- FavoriteController.java
- FavoriteService.java
- FavoriteMapper.java + XML
- CREATE TABLE favorites 스크립트
- FavoriteServiceTest.java

Step 3: 테스트
mvn test

Step 4: Git 커밋
git checkout -b feature/add-favorites
git add .
git commit -m "feat: Add stock favorites feature"
git push origin feature/add-favorites

Step 5: PR 생성
GitHub에서 PR 생성
```

**소요 시간:** 30분

### 11.2 시나리오 2: 버그 수정

**상황:** 로그인 시 간헐적으로 500 에러 발생

#### Cursor 사용

```
Step 1: 에러 로그 확인
java.lang.NullPointerException
    at AuthService.generateToken(AuthService.java:67)

Step 2: Cursor Chat (Cmd+L)
"AuthService.java:67에서 NullPointerException이 발생해.
 원인을 찾고 수정해줘."

Step 3: Cursor 분석
"Line 67에서 user.getEmail()을 호출하는데,
 user가 null일 수 있습니다.
 
 원인:
 - userMapper.findByUsername()이 Optional을 반환하지 않음
 - null 체크 누락
 
 수정 제안:
 User user = userMapper.findByUsername(username)
     .orElseThrow(() -> new UserNotFoundException());
     
 수정하시겠습니까?"

Step 4: 수정 적용
Tab으로 제안 수락

Step 5: 테스트 추가
Cmd+K
"null user 시나리오 테스트 추가해줘"

Step 6: Git 커밋
git checkout -b bugfix/fix-auth-null-pointer
git commit -m "fix: Fix NullPointerException in AuthService

- Add null check for user object
- Add test for null user scenario

Fixes #234"
git push origin bugfix/fix-auth-null-pointer

Step 7: PR 생성
Urgent: 🔥로 표시
```

**소요 시간:** 20분

### 11.3 시나리오 3: 대규모 리팩토링

**상황:** 중복 코드 제거 및 아키텍처 개선

#### Cursor + Claude Code 병행

```
Step 1: Cursor로 분석
Cmd+L
"프로젝트 전체에서 중복 코드를 찾아줘"

Cursor: [중복 코드 리스트 제공]

Step 2: Claude에게 리팩토링 계획 요청
"다음 중복 코드를 어떻게 리팩토링하면 좋을지
 단계별 계획을 작성해줘:
 
 [중복 코드 붙여넣기]"

Claude: [상세 리팩토링 계획 제공]

Step 3: Cursor로 단계별 실행
각 단계마다:
- Cmd+K로 코드 수정
- 테스트 실행
- 통과하면 다음 단계

Step 4: PR 분리
git checkout -b refactor/extract-common-validation
# 첫 번째 리팩토링만 커밋
git commit -m "refactor: Extract common validation logic"
git push origin refactor/extract-common-validation
# PR 생성

git checkout main
git checkout -b refactor/remove-duplicate-error-handling
# 두 번째 리팩토링만 커밋
...
```

**소요 시간:** 2-3시간 (단계별 PR로 분리)

### 11.4 시나리오 4: 팀 협업

**상황:** 3명이 동시에 같은 기능 개발

#### 역할 분담 + PR 전략

```
개발자 A: 백엔드 API
  ↓
  PR 1: "feat(backend): Add portfolio API"
  
개발자 B: 프론트엔드 UI
  ↓
  대기 (PR 1이 먼저 머지되어야 함)
  ↓
  PR 2: "feat(frontend): Add portfolio UI"
  base: PR 1이 머지된 main
  
개발자 C: 테스트 코드
  ↓
  PR 3: "test: Add integration tests for portfolio"
  base: PR 1이 머지된 main

순서:
PR 1 머지 → PR 2 머지 → PR 3 머지
```

---

## 12. FAQ

### Q1: PR을 언제 만들어야 하나요?

**A:** 기능/버그픽스가 완성되고 테스트가 통과한 후

```
❌ 너무 이른 PR:
코드를 한 줄만 작성하고 PR 생성

✅ 적절한 시점:
- 기능이 완성됨
- 로컬 테스트 통과
- 자체 리뷰 완료
- 문서 업데이트 완료

💡 Draft PR:
작업 중이지만 피드백이 필요한 경우
"Draft" PR로 생성 가능
```

### Q2: Draft PR이 뭔가요?

**A:** 작업 중임을 표시하는 PR

```
Draft PR 사용 시기:
1. 큰 기능 개발 시 초기 피드백 필요
2. 설계 리뷰 필요
3. 진행 상황 공유

특징:
- 병합 불가능
- CI/CD 실행됨
- 리뷰 가능
- "Ready for review"로 전환 가능
```

### Q3: PR이 너무 커지면 어떻게 하나요?

**A:** 여러 개의 작은 PR로 분리

```
큰 PR (2000줄):
"Implement user management system"

↓ 분리 ↓

작은 PR들:
1. "Add User model and database schema" (200줄)
2. "Add UserService business logic" (300줄)
3. "Add AuthController endpoints" (250줄)
4. "Add JWT authentication" (400줄)
5. "Add user profile UI" (350줄)
6. "Add integration tests" (500줄)

팁:
- 각 PR은 독립적으로 동작 가능하게
- 또는 명확한 의존성 순서 표시
```

### Q4: 리뷰어가 승인을 안 해줘요

**A:** 원인 파악 및 대응

```
원인 1: 리뷰어가 바쁨
→ 다른 리뷰어 추가 또는 다음 날 리마인드

원인 2: PR이 너무 큼
→ 작은 PR로 분리 제안

원인 3: 설명이 불충분
→ PR 설명 보강

원인 4: 테스트/문서 누락
→ 추가 후 리뷰 재요청

원인 5: 코드 품질 문제
→ 피드백 반영 후 재요청
```

### Q5: 다른 사람이 같은 파일을 수정했어요 (Conflict)

**A:** Git Merge Conflict 해결

```bash
# Step 1: main의 최신 코드 가져오기
git checkout main
git pull origin main

# Step 2: 내 브랜치로 돌아가기
git checkout feature/my-feature

# Step 3: main을 내 브랜치에 병합
git merge main

# 충돌 발생 시:
# CONFLICT (content): Merge conflict in PortfolioService.java

# Step 4: 충돌 파일 열기
# VSCode가 자동으로 충돌 표시:
<<<<<<< HEAD (내 변경)
내가 작성한 코드
=======
다른 사람이 작성한 코드
>>>>>>> main

# Step 5: 충돌 해결
# 둘 중 하나를 선택하거나 둘 다 유지

# Step 6: 해결 완료 표시
git add PortfolioService.java
git commit -m "Merge main into feature/my-feature"

# Step 7: Push
git push origin feature/my-feature
```

### Q6: PR을 잘못 만들었어요

**A:** 상황별 대응

```
상황 1: 잘못된 브랜치로 PR
→ PR 닫고 올바른 브랜치로 새 PR 생성

상황 2: 제목/설명 오타
→ PR 화면에서 Edit 버튼으로 수정

상황 3: 불필요한 커밋 포함
→ git rebase -i로 커밋 정리 후 force push

상황 4: 실수로 민감 정보 커밋
→ 즉시 PR 닫기
→ git filter-branch로 히스토리에서 제거
→ 비밀번호/키 즉시 교체

상황 5: 리뷰 중 새로운 요구사항
→ 새로운 PR로 분리 제안
```

### Q7: CI/CD 테스트가 계속 실패해요

**A:** 실패 원인 분석

```
1. GitHub PR 화면에서 "Details" 클릭
2. 실패한 단계 확인
3. 로그 읽기

일반적인 원인:
- 테스트 실패: 테스트 코드 수정
- 빌드 실패: 컴파일 오류 수정
- Lint 실패: 코드 스타일 수정
- 환경 문제: CI 설정 확인

로컬에서 동일 명령 실행:
mvn clean test
npm test
```

### Q8: 리뷰어가 너무 많은 피드백을 줘요

**A:** 긍정적으로 받아들이기

```
좋은 리뷰어의 신호:
- 상세한 피드백 = 코드를 꼼꼼히 봄
- 많은 질문 = 더 나은 이해를 원함
- 제안 = 성장 기회

대응 방법:
1. 감사 표현: "상세한 리뷰 감사합니다"
2. 우선순위 확인: "어떤 것부터 수정할까요?"
3. 토론: 의견이 다르면 토론
4. 학습: 피드백을 통해 배움

Blocker만 먼저 수정:
"🔴 Blocker 이슈들을 먼저 수정했습니다.
 🟢 Minor 이슈들은 다음 PR에서 개선하겠습니다."
```

### Q9: 바이브코딩으로 만든 코드도 리뷰가 필요한가요?

**A:** 당연히 필요합니다!

```
AI 코드의 일반적인 문제:
1. 비즈니스 로직 오류
   → AI는 요구사항을 완벽히 이해 못 함
   
2. 보안 취약점
   → AI는 보안을 간과할 수 있음
   
3. 성능 문제
   → AI는 최적화를 놓칠 수 있음
   
4. 프로젝트 특성 무시
   → AI는 프로젝트 컨텍스트를 모를 수 있음

리뷰 체크리스트:
- [ ] 요구사항 충족 확인
- [ ] 보안 취약점 확인
- [ ] 성능 문제 확인
- [ ] 프로젝트 스타일 확인
- [ ] 테스트 충분성 확인
```

### Q10: PR 크기의 적정선은?

**A:** 300-500줄이 이상적

```
경험적 기준:

🟢 작은 PR (< 300줄)
- 리뷰 시간: 15-30분
- 버그 발견율: 높음
- 승인 속도: 빠름

🟡 중간 PR (300-500줄)
- 리뷰 시간: 30-60분
- 버그 발견율: 중간
- 승인 속도: 보통

🔴 큰 PR (> 500줄)
- 리뷰 시간: 1-2시간
- 버그 발견율: 낮음
- 승인 속도: 느림

🚨 거대한 PR (> 1000줄)
- 리뷰 시간: 3+ 시간
- 버그 발견율: 매우 낮음
- 승인 속도: 매우 느림
- 대부분 제대로 리뷰 안 됨

최선의 전략:
작은 PR을 자주 만들기
```

---

## 📝 요약

### PR의 핵심

1. **PR = 코드 병합 요청**
   - 내 브랜치 → main 브랜치
   
2. **왜 사용하나?**
   - 코드 품질 보장
   - 버그 조기 발견
   - 지식 공유
   - 협업 효율 증대

3. **생명주기**
   - 브랜치 생성 → 코드 작성 → Commit → Push → PR 생성 → 리뷰 → 수정 → 승인 → 병합

### 바이브코딩 도구별 PR 만들기

| 도구 | 강점 | PR 생성 방법 |
|------|------|--------------|
| **Claude Code** | 빠른 전체 프로젝트 생성 | 브라우저, 자연어 대화 |
| **Cursor** | IDE 통합, 정교한 편집 | Cmd+K, Composer |
| **GitHub Copilot** | 실시간 코드 완성 | VSCode/IDE 통합 |

### 베스트 프랙티스

1. ✅ 작은 PR (300-500줄)
2. ✅ 명확한 제목과 설명
3. ✅ 자체 리뷰 먼저
4. ✅ 테스트 포함
5. ✅ 리뷰어 배려

### 리뷰 가이드

- 친절하고 명확하게
- 코드로 제안
- 우선순위 표시
- 건설적 피드백

### 핵심 메시지

> **AI가 코드를 작성해도,**
> **인간의 리뷰는 여전히 필수입니다!**

PR은 단순한 코드 병합이 아니라:
- 지식 공유의 장
- 배움의 기회
- 품질 보증의 관문
- 팀 협업의 핵심

---

**작성자**: Claude  
**작성일**: 2025-12-07  
**문서 버전**: 1.0  
**대상 독자**: 바이브코딩 입문자, 주니어 개발자

---

**다음 읽을 문서:**
- Git 기초 가이드
- 코드 리뷰 체크리스트
- GitHub Actions CI/CD 설정
- 팀 협업 워크플로우

---

**작성 일시**: 2025-12-07 01:25:43 (한국 시간 기준)
