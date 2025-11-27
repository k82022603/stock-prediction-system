# GitHub 사용 가이드

## 저장소 정보

**GitHub 저장소:** https://github.com/k82022603/stock-prediction-system

## 프로젝트 클론하기

### 1. Git 설치 확인

```bash
git --version
```

Git이 설치되어 있지 않다면:
- Windows: https://git-scm.com/download/win
- Mac: `brew install git`
- Linux: `sudo apt-get install git`

### 2. 프로젝트 클론

```bash
# HTTPS 방식 (추천)
git clone https://github.com/k82022603/stock-prediction-system.git

# SSH 방식 (SSH 키 설정이 되어 있는 경우)
git clone git@github.com:k82022603/stock-prediction-system.git

# 특정 디렉토리에 클론
git clone https://github.com/k82022603/stock-prediction-system.git my-project
```

### 3. 프로젝트 디렉토리로 이동

```bash
cd stock-prediction-system
```

## 브랜치 관리

### 브랜치 확인

```bash
# 로컬 브랜치 목록
git branch

# 원격 브랜치 포함 전체 목록
git branch -a
```

### 새 브랜치 생성 및 이동

```bash
# 새 브랜치 생성
git branch feature/new-feature

# 브랜치로 이동
git checkout feature/new-feature

# 생성과 동시에 이동
git checkout -b feature/new-feature
```

### 브랜치 전략

```
main (기본 브랜치)
  ├── feature/stock-search     (새 기능: 주식 검색)
  ├── feature/chart-view        (새 기능: 차트 뷰)
  ├── bugfix/api-error          (버그 수정)
  └── hotfix/critical-bug       (긴급 수정)
```

## 코드 변경 및 커밋

### 1. 변경사항 확인

```bash
# 변경된 파일 목록
git status

# 변경 내용 상세 보기
git diff
```

### 2. 변경사항 스테이징

```bash
# 특정 파일만 추가
git add <file>

# 전체 파일 추가
git add .
```

### 3. 커밋하기

```bash
# 커밋
git commit -m "Add new feature"
```

### 커밋 메시지 컨벤션

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 변경
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드 설정 등
```

## 원격 저장소와 동기화

### 1. 변경사항 가져오기 (Pull)

```bash
git pull origin main
```

### 2. 변경사항 푸시하기 (Push)

```bash
# 처음 푸시할 때
git push -u origin feature/new-feature

# 이후 푸시
git push
```

## 실수 복구하기

### 1. 스테이징 취소

```bash
git reset HEAD <file>
```

### 2. 마지막 커밋 수정

```bash
git commit --amend -m "새로운 메시지"
```

### 3. 변경사항 되돌리기

```bash
git checkout -- <file>
```

## 유용한 명령어

```bash
# 커밋 히스토리
git log --oneline

# 브랜치 삭제
git branch -d feature/branch-name

# 원격 저장소 확인
git remote -v
```

## 참고 자료

- [Git 공식 문서](https://git-scm.com/doc)
- [GitHub 가이드](https://guides.github.com/)
- [Pro Git Book (한글)](https://git-scm.com/book/ko/v2)

---

**프로젝트에 기여하고 싶으신가요?**

1. 저장소를 Fork하세요
2. 새 브랜치를 만드세요 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋하세요 (`git commit -m 'feat: Add amazing feature'`)
4. 브랜치에 푸시하세요 (`git push origin feature/amazing-feature`)
5. Pull Request를 열어주세요!
