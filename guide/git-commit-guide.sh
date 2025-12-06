#!/bin/bash

# Stock Prediction System - Git Auto Commit Script
# 개발자 가이드를 GitHub에 자동으로 커밋하고 푸시합니다

echo "=========================================="
echo "Stock Prediction System - Git Auto Commit"
echo "=========================================="
echo ""

# 현재 위치 확인
echo "📁 현재 디렉토리:"
pwd
echo ""

# Git 저장소 확인
if [ ! -d ".git" ]; then
    echo "❌ Git 저장소가 아닙니다."
    echo "   프로젝트 루트 디렉토리에서 실행해주세요."
    exit 1
fi

# 파일 존재 확인
if [ ! -f "stock-prediction-system-developer-guide.md" ]; then
    echo "⚠️  stock-prediction-system-developer-guide.md 파일을 찾을 수 없습니다."
    echo "   파일을 프로젝트 루트에 복사해주세요."
    exit 1
fi

echo "✅ Git 저장소 확인 완료"
echo "✅ 개발자 가이드 파일 확인 완료"
echo ""

# 변경사항 추가
echo "📝 Git add 실행 중..."
git add stock-prediction-system-developer-guide.md

# 상태 확인
echo ""
echo "📊 현재 Git 상태:"
git status --short
echo ""

# 커밋
echo "💾 Git commit 실행 중..."
git commit -m "docs: Add comprehensive developer guide for stock prediction system

- Spring Boot 3.2 최신 기술 설명
- React 18 프론트엔드 가이드
- MyBatis 3.0 데이터베이스 연동
- 서술형 위주의 상세한 설명
- 소스코드 최소화, 개념 중심 설명
- 10개 챕터로 구성된 완전한 개발자 가이드"

# 커밋 결과 확인
if [ $? -eq 0 ]; then
    echo "✅ 커밋 성공!"
else
    echo "❌ 커밋 실패!"
    exit 1
fi

echo ""

# Push
echo "🚀 Git push 실행 중..."
git push origin main

# Push 결과 확인
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ 모든 작업 완료!"
    echo "=========================================="
    echo ""
    echo "📍 GitHub에서 확인:"
    echo "   https://github.com/k82022603/stock-prediction-system"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "⚠️  Push 실패!"
    echo "=========================================="
    echo ""
    echo "다음 사항을 확인해주세요:"
    echo "  1. GitHub 인증 정보가 올바른지 확인"
    echo "  2. 네트워크 연결 상태 확인"
    echo "  3. 원격 저장소 권한 확인"
    echo ""
    echo "수동으로 push하려면:"
    echo "  git push origin main"
    echo ""
fi
