# TDD(Test-Driven Development) 초급 개발자 가이드
## Stock Prediction System 프로젝트로 배우는 테스트 주도 개발

---

## 📚 목차

1. [TDD란 무엇인가?](#1-tdd란-무엇인가)
2. [왜 TDD가 필요한가?](#2-왜-tdd가-필요한가)
3. [TDD 개발 프로세스](#3-tdd-개발-프로세스)
4. [개발 환경 설정](#4-개발-환경-설정)
5. [JUnit 5 기초](#5-junit-5-기초)
6. [Mockito로 Mock 객체 만들기](#6-mockito로-mock-객체-만들기)
7. [실전 TDD 실습](#7-실전-tdd-실습)
8. [Spring Boot 테스트](#8-spring-boot-테스트)
9. [테스트 커버리지 측정](#9-테스트-커버리지-측정)
10. [VSCode에서 테스트 실행](#10-vscode에서-테스트-실행)
11. [좋은 테스트 작성법](#11-좋은-테스트-작성법)
12. [자주 하는 실수](#12-자주-하는-실수)

---

## 1. TDD란 무엇인가?

### 1.1 TDD의 정의

**TDD (Test-Driven Development)** = 테스트 주도 개발

**전통적인 개발 방식:**
```
요구사항 → 코드 작성 → 테스트 작성 → 버그 발견 → 수정
```

**TDD 방식:**
```
요구사항 → 테스트 작성 → 코드 작성 → 테스트 통과 → 리팩토링
```

### 1.2 간단한 비유

**전통적 방식** = 집을 짓고 나서 검사받기
- 문제 발견 시 수정 비용이 큼
- 전체를 다시 뜯어야 할 수도 있음

**TDD 방식** = 설계도(테스트)를 먼저 그리고 집 짓기
- 각 단계마다 검증
- 문제를 조기에 발견
- 안정적인 결과물

### 1.3 TDD의 3가지 법칙

#### ① 실패하는 테스트를 먼저 작성한다
```java
@Test
void findById_ShouldReturnStock() {
    // Given
    Long id = 1L;
    
    // When
    Stock stock = stockService.findById(id);
    
    // Then
    assertNotNull(stock);
    assertEquals("005930", stock.getStockCode());
}
// → 이 시점에서는 컴파일 에러! (findById 메서드가 없음)
```

#### ② 테스트를 통과할 만큼만 코드를 작성한다
```java
public Stock findById(Long id) {
    return null;  // 일단 null 반환 (테스트 실패)
}
```

#### ③ 테스트를 통과하면 리팩토링한다
```java
public Stock findById(Long id) {
    return stockMapper.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
}
// → 이제 테스트 통과! 그리고 코드 정리
```

---

## 2. 왜 TDD가 필요한가?

### 2.1 버그 발견 시점의 중요성

**비용 비교:**

| 발견 시점 | 수정 비용 | 비유 |
|-----------|----------|------|
| 개발 중 | 1배 | 집 짓는 중 발견 |
| 테스트 중 | 10배 | 집 완성 후 발견 |
| 배포 후 | 100배 | 입주 후 발견 |
| 운영 중 | 1000배 | 몇 년 살다가 발견 |

**실제 사례:**
```
개발 중 발견: "변수명 오타네? 2분이면 고치겠다."
배포 후 발견: "DB 롤백하고, 긴급 패치하고, 공지하고..."
```

### 2.2 TDD의 장점

#### ① 설계 품질 향상

**나쁜 설계 (테스트하기 어려움):**
```java
public class StockService {
    public Stock getStock(Long id) {
        // DB 직접 연결 (하드코딩)
        Connection conn = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/db", "user", "pass");
        // ...
    }
}
// → 테스트할 때마다 실제 DB 필요
```

**좋은 설계 (테스트하기 쉬움):**
```java
public class StockService {
    private final StockMapper stockMapper;  // 의존성 주입
    
    public StockService(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }
    
    public Stock getStock(Long id) {
        return stockMapper.findById(id);
    }
}
// → Mock으로 쉽게 테스트 가능
```

#### ② 회귀 버그 방지

**회귀 버그** = 이전에 잘 작동하던 기능이 새 기능 추가 후 망가지는 것

```
상황: "검색 기능 추가했더니 기존 목록 조회가 안 돼요!"

TDD 없는 경우:
- 사용자가 발견 (운영 중 장애)
- 긴급 롤백

TDD 있는 경우:
- 테스트가 실패 → 배포 전에 발견
- 안전하게 수정
```

#### ③ 리팩토링의 안전망

```java
// 리팩토링 전
public List<Stock> getAllStocks() {
    List<Stock> stocks = new ArrayList<>();
    for (Stock stock : stockMapper.findAll()) {
        if (stock.getMarket().equals("KOSPI")) {
            stocks.add(stock);
        }
    }
    return stocks;
}

// 리팩토링 후 (Stream 사용)
public List<Stock> getAllStocks() {
    return stockMapper.findAll().stream()
        .filter(stock -> stock.getMarket().equals("KOSPI"))
        .collect(Collectors.toList());
}

// 테스트가 통과하면 → 안전하게 리팩토링 완료!
```

#### ④ 문서화 효과

**좋은 테스트 = 살아있는 문서**

```java
@Test
void getStockByCode_WithValidCode_ShouldReturnStock() {
    // 이 테스트를 보면:
    // - getStockByCode 메서드가 있구나
    // - 종목 코드를 받는구나
    // - Stock 객체를 반환하는구나
}

@Test
void getStockByCode_WithInvalidCode_ShouldThrowException() {
    // 잘못된 코드를 넣으면 예외를 던지는구나
}
```

### 2.3 TDD의 단점 (솔직하게)

#### ① 초기 개발 시간 증가
- 테스트 작성 시간 필요
- 하지만 전체 개발 기간은 오히려 단축

#### ② 학습 곡선
- JUnit, Mockito 등 학습 필요
- 하지만 한 번 익히면 계속 사용

#### ③ 100% 커버리지는 비현실적
- 모든 코드를 테스트할 필요는 없음
- 핵심 로직 위주로 테스트

---

## 3. TDD 개발 프로세스

### 3.1 Red-Green-Refactor 사이클

```
🔴 Red (실패하는 테스트 작성)
   ↓
🟢 Green (테스트를 통과하는 최소한의 코드 작성)
   ↓
🔵 Refactor (코드 개선)
   ↓
   반복...
```

### 3.2 실전 예제: 주식 서비스 개발

#### Step 1: 🔴 Red - 실패하는 테스트 작성

```java
package com.stock.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockServiceTest {
    
    @Test
    void findByStockCode_WithValidCode_ShouldReturnStock() {
        // Given
        StockService service = new StockService();
        String stockCode = "005930";
        
        // When
        Stock stock = service.findByStockCode(stockCode);
        
        // Then
        assertNotNull(stock);
        assertEquals("005930", stock.getStockCode());
        assertEquals("삼성전자", stock.getStockName());
    }
}
```

**실행 결과:**
```
❌ 컴파일 에러!
- StockService 클래스가 없음
- findByStockCode 메서드가 없음
```

#### Step 2: 🟢 Green - 테스트를 통과하는 최소 코드

```java
package com.stock.service;

public class StockService {
    
    public Stock findByStockCode(String stockCode) {
        // 하드코딩으로 일단 통과시키기
        Stock stock = new Stock();
        stock.setStockCode("005930");
        stock.setStockName("삼성전자");
        return stock;
    }
}
```

**실행 결과:**
```
✅ 테스트 통과!
```

#### Step 3: 🔵 Refactor - 리팩토링

```java
package com.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMapper stockMapper;
    
    public Stock findByStockCode(String stockCode) {
        return stockMapper.findByStockCode(stockCode)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Stock not found: " + stockCode));
    }
}
```

**테스트는 그대로 통과!**

#### Step 4: 다음 테스트 추가 (🔴 Red)

```java
@Test
void findByStockCode_WithInvalidCode_ShouldThrowException() {
    // Given
    String invalidCode = "999999";
    
    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
        service.findByStockCode(invalidCode);
    });
}
```

**이 과정을 계속 반복!**

---

## 4. 개발 환경 설정

### 4.1 pom.xml에 테스트 의존성 추가

```xml
<dependencies>
    <!-- Spring Boot Test (JUnit 5 포함) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito (이미 포함되어 있지만 명시) -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ (더 읽기 쉬운 단언문) -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 4.2 프로젝트 구조

```
backend/
├── src/
│   ├── main/
│   │   └── java/com/stock/
│   │       ├── controller/
│   │       ├── service/
│   │       └── mapper/
│   └── test/  ← 테스트 코드 위치
│       └── java/com/stock/
│           ├── controller/
│           │   └── StockControllerTest.java
│           ├── service/
│           │   └── StockServiceTest.java
│           └── mapper/
│               └── StockMapperTest.java
└── pom.xml
```

### 4.3 VSCode 설정

#### Extension 설치

1. **Java Extension Pack** 설치
   - VSCode 좌측 Extensions 아이콘 클릭
   - "Java Extension Pack" 검색
   - Install 클릭

2. **Test Runner for Java** (포함됨)
   - 테스트 실행 및 디버깅
   - 커버리지 시각화

3. **Maven for Java** (포함됨)
   - Maven 명령 실행

#### settings.json 설정

```json
{
  "java.test.config": {
    "workingDirectory": "${workspaceFolder}/backend"
  },
  "java.test.defaultConfig": "default"
}
```

---

## 5. JUnit 5 기초

### 5.1 기본 어노테이션

```java
import org.junit.jupiter.api.*;

class BasicTest {
    
    @BeforeAll  // 모든 테스트 전에 1번 실행 (static 메서드)
    static void setupAll() {
        System.out.println("테스트 클래스 시작");
    }
    
    @BeforeEach  // 각 테스트 전에 실행
    void setup() {
        System.out.println("테스트 준비");
    }
    
    @Test  // 테스트 메서드
    void testMethod1() {
        System.out.println("테스트 1 실행");
    }
    
    @Test
    void testMethod2() {
        System.out.println("테스트 2 실행");
    }
    
    @AfterEach  // 각 테스트 후에 실행
    void tearDown() {
        System.out.println("테스트 정리");
    }
    
    @AfterAll  // 모든 테스트 후에 1번 실행 (static 메서드)
    static void tearDownAll() {
        System.out.println("테스트 클래스 종료");
    }
}
```

**실행 결과:**
```
테스트 클래스 시작
테스트 준비
테스트 1 실행
테스트 정리
테스트 준비
테스트 2 실행
테스트 정리
테스트 클래스 종료
```

### 5.2 Assertions (단언문)

#### ① assertEquals - 값 비교

```java
@Test
void testEquals() {
    String expected = "삼성전자";
    String actual = "삼성전자";
    
    assertEquals(expected, actual);
    assertEquals(expected, actual, "종목명이 일치해야 함");  // 메시지 추가
}
```

#### ② assertNotNull - null 체크

```java
@Test
void testNotNull() {
    Stock stock = stockService.findById(1L);
    
    assertNotNull(stock);  // stock이 null이면 실패
    assertNotNull(stock.getStockCode());
}
```

#### ③ assertTrue / assertFalse - 조건 체크

```java
@Test
void testCondition() {
    Stock stock = new Stock();
    stock.setStockCode("005930");
    
    assertTrue(stock.getStockCode().length() == 6);
    assertFalse(stock.getStockCode().isEmpty());
}
```

#### ④ assertThrows - 예외 발생 확인

```java
@Test
void testException() {
    assertThrows(ResourceNotFoundException.class, () -> {
        stockService.findById(999L);  // 존재하지 않는 ID
    });
}
```

#### ⑤ assertAll - 여러 검증 한 번에

```java
@Test
void testStock() {
    Stock stock = stockService.findById(1L);
    
    assertAll(
        () -> assertNotNull(stock),
        () -> assertEquals("005930", stock.getStockCode()),
        () -> assertEquals("삼성전자", stock.getStockName()),
        () -> assertEquals("KOSPI", stock.getMarket())
    );
    // 모든 검증이 실행되고, 실패한 것들을 한 번에 보여줌
}
```

### 5.3 AssertJ (더 읽기 쉬운 단언문)

**JUnit 기본:**
```java
assertEquals("삼성전자", stock.getStockName());
assertTrue(stock.getStockCode().startsWith("005"));
```

**AssertJ 사용:**
```java
import static org.assertj.core.api.Assertions.*;

assertThat(stock.getStockName()).isEqualTo("삼성전자");
assertThat(stock.getStockCode()).startsWith("005");
assertThat(stock.getStockCode()).hasSize(6);

// 체이닝 가능
assertThat(stock)
    .isNotNull()
    .extracting("stockCode", "stockName")
    .containsExactly("005930", "삼성전자");
```

**훨씬 읽기 쉽죠?**

---

## 6. Mockito로 Mock 객체 만들기

### 6.1 Mock이란?

**문제 상황:**
```java
@Service
public class StockService {
    private final StockMapper stockMapper;  // DB 접근
    
    public Stock findById(Long id) {
        return stockMapper.findById(id);  // 실제 DB 쿼리
    }
}
```

**테스트할 때 어려운 점:**
- 실제 DB 필요
- 데이터 준비 필요
- 느림 (네트워크, I/O)

**해결책: Mock 객체 사용**
```java
@Test
void testFindById() {
    // Mock 객체 생성 (가짜 StockMapper)
    StockMapper mockMapper = mock(StockMapper.class);
    
    // Mock 동작 정의
    Stock mockStock = new Stock();
    mockStock.setId(1L);
    mockStock.setStockCode("005930");
    when(mockMapper.findById(1L)).thenReturn(Optional.of(mockStock));
    
    // Service에 Mock 주입
    StockService service = new StockService(mockMapper);
    
    // 테스트
    Stock result = service.findById(1L);
    assertEquals("005930", result.getStockCode());
}
```

### 6.2 Mockito 기본 사용법

#### ① @Mock과 @InjectMocks

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)  // Mockito 활성화
class StockServiceTest {
    
    @Mock  // Mock 객체 생성
    private StockMapper stockMapper;
    
    @InjectMocks  // Mock을 주입받는 실제 객체
    private StockService stockService;
    
    @Test
    void testFindById() {
        // Given
        Stock mockStock = Stock.builder()
            .id(1L)
            .stockCode("005930")
            .stockName("삼성전자")
            .build();
        
        when(stockMapper.findById(1L))
            .thenReturn(Optional.of(mockStock));
        
        // When
        Stock result = stockService.findById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("005930", result.getStockCode());
    }
}
```

#### ② when-thenReturn (Mock 동작 정의)

```java
// 값 반환
when(stockMapper.findById(1L))
    .thenReturn(Optional.of(stock));

// 예외 던지기
when(stockMapper.findById(999L))
    .thenThrow(new ResourceNotFoundException("Not found"));

// 여러 번 호출 시 다른 값
when(stockMapper.findAll())
    .thenReturn(Arrays.asList(stock1))  // 첫 번째 호출
    .thenReturn(Arrays.asList(stock1, stock2));  // 두 번째 호출
```

#### ③ verify (호출 검증)

```java
@Test
void testDelete() {
    // Given
    Long id = 1L;
    
    // When
    stockService.deleteById(id);
    
    // Then
    verify(stockMapper).deleteById(id);  // 호출되었는지 확인
    verify(stockMapper, times(1)).deleteById(id);  // 1번 호출
    verify(stockMapper, never()).findAll();  // 호출 안 됨
}
```

#### ④ ArgumentCaptor (인자 캡처)

```java
@Test
void testCreate() {
    // Given
    StockDTO dto = new StockDTO("005930", "삼성전자", "KOSPI");
    
    // When
    stockService.create(dto);
    
    // Then
    ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);
    verify(stockMapper).insert(captor.capture());
    
    Stock captured = captor.getValue();
    assertEquals("005930", captured.getStockCode());
    assertEquals("삼성전자", captured.getStockName());
}
```

### 6.3 given-when-then 패턴

**가독성 좋은 테스트 구조:**

```java
@Test
void findByStockCode_WithValidCode_ShouldReturnStock() {
    // Given (준비): 테스트에 필요한 데이터와 Mock 설정
    String stockCode = "005930";
    Stock mockStock = Stock.builder()
        .stockCode(stockCode)
        .stockName("삼성전자")
        .build();
    
    when(stockMapper.findByStockCode(stockCode))
        .thenReturn(Optional.of(mockStock));
    
    // When (실행): 테스트할 메서드 호출
    Stock result = stockService.findByStockCode(stockCode);
    
    // Then (검증): 결과 확인
    assertNotNull(result);
    assertEquals(stockCode, result.getStockCode());
    assertEquals("삼성전자", result.getStockName());
    
    verify(stockMapper, times(1)).findByStockCode(stockCode);
}
```

---

## 7. 실전 TDD 실습

### 7.1 요구사항: 시장별 주식 조회 기능

**사용자 스토리:**
```
사용자는 시장(KOSPI/KOSDAQ)을 선택하여 
해당 시장의 주식 목록을 조회할 수 있다.
```

### 7.2 Step 1: 🔴 Red - 테스트 먼저 작성

```java
package com.stock.service;

import com.stock.mapper.StockMapper;
import com.stock.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest_Market {
    
    @Mock
    private StockMapper stockMapper;
    
    @InjectMocks
    private StockService stockService;
    
    @Test
    void findByMarket_WithKOSPI_ShouldReturnKOSPIStocks() {
        // Given
        List<Stock> kospiStocks = Arrays.asList(
            Stock.builder()
                .stockCode("005930")
                .stockName("삼성전자")
                .market("KOSPI")
                .build(),
            Stock.builder()
                .stockCode("000660")
                .stockName("SK하이닉스")
                .market("KOSPI")
                .build()
        );
        
        when(stockMapper.findByMarket("KOSPI"))
            .thenReturn(kospiStocks);
        
        // When
        List<Stock> result = stockService.findByMarket("KOSPI");
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("market")
            .containsOnly("KOSPI");
        
        verify(stockMapper, times(1)).findByMarket("KOSPI");
    }
    
    @Test
    void findByMarket_WithInvalidMarket_ShouldThrowException() {
        // Given
        String invalidMarket = "NASDAQ";
        
        // When & Then
        assertThatThrownBy(() -> stockService.findByMarket(invalidMarket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid market");
    }
}
```

**실행 결과:**
```
❌ 컴파일 에러!
- StockService.findByMarket() 메서드가 없음
- StockMapper.findByMarket() 메서드가 없음
```

### 7.3 Step 2: 🟢 Green - 최소한의 코드 작성

#### Mapper 인터페이스에 메서드 추가

```java
package com.stock.mapper;

import com.stock.model.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {
    
    List<Stock> findAll();
    
    // 새로 추가!
    List<Stock> findByMarket(@Param("market") String market);
}
```

#### Service에 메서드 구현

```java
package com.stock.service;

import com.stock.mapper.StockMapper;
import com.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMapper stockMapper;
    
    private static final List<String> VALID_MARKETS = 
        Arrays.asList("KOSPI", "KOSDAQ");
    
    public List<Stock> findByMarket(String market) {
        // 입력 검증
        if (!VALID_MARKETS.contains(market)) {
            throw new IllegalArgumentException(
                "Invalid market: " + market + 
                ". Must be KOSPI or KOSDAQ");
        }
        
        return stockMapper.findByMarket(market);
    }
}
```

#### Mapper XML에 쿼리 추가

```xml
<!-- resources/mapper/StockMapper.xml -->
<select id="findByMarket" resultType="com.stock.model.Stock">
    SELECT 
        id,
        stock_code AS stockCode,
        stock_name AS stockName,
        market,
        sector
    FROM stocks
    WHERE market = #{market}
    ORDER BY stock_code
</select>
```

**테스트 실행:**
```
✅ 모든 테스트 통과!
```

### 7.4 Step 3: 🔵 Refactor - 코드 개선

#### 상수를 Enum으로 변경

```java
package com.stock.model;

public enum Market {
    KOSPI("KOSPI"),
    KOSDAQ("KOSDAQ");
    
    private final String value;
    
    Market(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static Market fromString(String value) {
        for (Market market : Market.values()) {
            if (market.value.equals(value)) {
                return market;
            }
        }
        throw new IllegalArgumentException("Invalid market: " + value);
    }
}
```

#### Service 코드 개선

```java
public List<Stock> findByMarket(String marketStr) {
    Market market = Market.fromString(marketStr);  // Enum으로 검증
    return stockMapper.findByMarket(market.getValue());
}
```

**테스트 다시 실행:**
```
✅ 여전히 모든 테스트 통과!
→ 안전하게 리팩토링 완료
```

### 7.5 Step 4: 추가 테스트 케이스

```java
@Test
void findByMarket_WithKOSDAQ_ShouldReturnKOSDAQStocks() {
    // Given
    List<Stock> kosdaqStocks = Arrays.asList(
        Stock.builder()
            .stockCode("035720")
            .stockName("카카오")
            .market("KOSDAQ")
            .build()
    );
    
    when(stockMapper.findByMarket("KOSDAQ"))
        .thenReturn(kosdaqStocks);
    
    // When
    List<Stock> result = stockService.findByMarket("KOSDAQ");
    
    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMarket()).isEqualTo("KOSDAQ");
}

@Test
void findByMarket_WithEmptyResult_ShouldReturnEmptyList() {
    // Given
    when(stockMapper.findByMarket("KOSPI"))
        .thenReturn(Arrays.asList());
    
    // When
    List<Stock> result = stockService.findByMarket("KOSPI");
    
    // Then
    assertThat(result).isEmpty();
}
```

---

## 8. Spring Boot 테스트

### 8.1 단위 테스트 vs 통합 테스트

**단위 테스트 (Unit Test):**
- 작은 단위(메서드, 클래스) 테스트
- Mock 객체 사용
- 빠름 (수 밀리초)
- 의존성 격리

**통합 테스트 (Integration Test):**
- 여러 컴포넌트를 함께 테스트
- 실제 Spring Context 사용
- 느림 (수 초)
- 실제 환경과 유사

### 8.2 Controller 통합 테스트

```java
package com.stock.controller;

import com.stock.model.Stock;
import com.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)  // Controller만 로드
class StockControllerTest {
    
    @Autowired
    private MockMvc mockMvc;  // HTTP 요청 테스트
    
    @MockBean  // Service는 Mock으로
    private StockService stockService;
    
    @Test
    void getAllStocks_ShouldReturn200AndStockList() throws Exception {
        // Given
        Stock stock = Stock.builder()
            .stockCode("005930")
            .stockName("삼성전자")
            .market("KOSPI")
            .build();
        
        when(stockService.findAll())
            .thenReturn(Arrays.asList(stock));
        
        // When & Then
        mockMvc.perform(get("/api/stocks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].stockCode").value("005930"))
            .andExpect(jsonPath("$[0].stockName").value("삼성전자"));
        
        verify(stockService, times(1)).findAll();
    }
    
    @Test
    void getStockByCode_WithValidCode_ShouldReturn200() throws Exception {
        // Given
        Stock stock = Stock.builder()
            .stockCode("005930")
            .stockName("삼성전자")
            .build();
        
        when(stockService.findByStockCode("005930"))
            .thenReturn(stock);
        
        // When & Then
        mockMvc.perform(get("/api/stocks/005930"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stockCode").value("005930"));
    }
    
    @Test
    void getStockByCode_WithInvalidCode_ShouldReturn404() throws Exception {
        // Given
        when(stockService.findByStockCode("999999"))
            .thenThrow(new ResourceNotFoundException("Not found"));
        
        // When & Then
        mockMvc.perform(get("/api/stocks/999999"))
            .andExpect(status().isNotFound());
    }
}
```

### 8.3 Repository(Mapper) 테스트

```java
package com.stock.mapper;

import com.stock.model.Stock;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@MybatisTest  // MyBatis 테스트
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data.sql")  // 테스트 데이터 로드
class StockMapperTest {
    
    @Autowired
    private StockMapper stockMapper;
    
    @Test
    void findAll_ShouldReturnAllStocks() {
        // When
        List<Stock> stocks = stockMapper.findAll();
        
        // Then
        assertThat(stocks).isNotEmpty();
        assertThat(stocks).hasSizeGreaterThan(0);
    }
    
    @Test
    void findById_WithValidId_ShouldReturnStock() {
        // When
        Optional<Stock> stock = stockMapper.findById(1L);
        
        // Then
        assertThat(stock).isPresent();
        assertThat(stock.get().getId()).isEqualTo(1L);
    }
    
    @Test
    void insert_ShouldSaveStock() {
        // Given
        Stock newStock = Stock.builder()
            .stockCode("999999")
            .stockName("테스트주식")
            .market("KOSPI")
            .build();
        
        // When
        int result = stockMapper.insert(newStock);
        
        // Then
        assertThat(result).isEqualTo(1);
        assertThat(newStock.getId()).isNotNull();  // ID 자동 생성 확인
    }
}
```

**test-data.sql:**
```sql
INSERT INTO stocks (stock_code, stock_name, market, sector) 
VALUES 
    ('005930', '삼성전자', 'KOSPI', 'IT'),
    ('000660', 'SK하이닉스', 'KOSPI', 'IT'),
    ('035720', '카카오', 'KOSDAQ', 'IT');
```

---

## 9. 테스트 커버리지 측정

### 9.1 JaCoCo 설정

#### pom.xml에 JaCoCo 플러그인 추가

```xml
<build>
    <plugins>
        <!-- JaCoCo Plugin -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.10</version>
            <executions>
                <!-- 테스트 실행 전 Agent 연결 -->
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                
                <!-- 테스트 실행 후 리포트 생성 -->
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                
                <!-- 커버리지 체크 (최소 80%) -->
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 9.2 커버리지 리포트 생성

#### Maven 명령어

```bash
# 테스트 실행 및 커버리지 측정
mvn clean test

# 커버리지 리포트 생성
mvn jacoco:report

# 생성된 리포트 위치
# target/site/jacoco/index.html
```

#### 리포트 확인

```bash
# 브라우저에서 열기
open target/site/jacoco/index.html  # Mac
start target/site/jacoco/index.html  # Windows
```

**리포트 내용:**
```
Overall Coverage Summary
─────────────────────────────────────
Package          Line Coverage
─────────────────────────────────────
com.stock.service      87%  ✓
com.stock.controller   92%  ✓
com.stock.mapper       75%  ⚠️
─────────────────────────────────────
Total                  85%  ✓
```

### 9.3 커버리지 해석

**Line Coverage (라인 커버리지):**
- 전체 코드 라인 중 테스트된 라인 비율
- 가장 기본적인 지표

**Branch Coverage (브랜치 커버리지):**
- if/else, switch 등 분기문의 모든 경로 테스트 여부
- 더 엄격한 지표

**예시:**
```java
public String getMarketType(String market) {
    if (market.equals("KOSPI")) {
        return "대형주 시장";  // 브랜치 1
    } else {
        return "중소형주 시장";  // 브랜치 2
    }
}

// Line Coverage 100%를 위해서는:
// - 메서드를 한 번이라도 호출

// Branch Coverage 100%를 위해서는:
// - KOSPI로 테스트 (브랜치 1)
// - KOSDAQ으로 테스트 (브랜치 2)
```

**좋은 커버리지 기준:**
- Service Layer: 80% 이상
- Controller Layer: 70% 이상
- Util/Helper: 90% 이상
- 전체: 75% 이상

**주의:**
```
커버리지 100% ≠ 버그 없음
커버리지는 참고 지표일 뿐!
```

---

## 10. VSCode에서 테스트 실행

### 10.1 테스트 실행 방법

#### ① Test Explorer 사용

1. VSCode 좌측 **Testing** 아이콘 클릭 (플라스크 모양)
2. 테스트 파일/클래스/메서드 표시됨
3. 실행 버튼 클릭

```
📁 backend
  └─ 📁 src/test/java
      └─ 📁 com.stock
          └─ 📁 service
              └─ 📄 StockServiceTest.java
                  ├─ ▶️ testFindAll()
                  ├─ ▶️ testFindById()
                  └─ ▶️ testFindByMarket()
```

#### ② 코드 내에서 직접 실행

```java
public class StockServiceTest {
    
    @Test  // ← 옆에 "Run Test" 버튼 표시
    void testFindAll() {
        // ...
    }
}
```

클릭하면 바로 실행!

#### ③ 단축키 사용

**전체 테스트 실행:**
```
Ctrl+; A  (Windows/Linux)
Cmd+; A   (Mac)
```

**현재 테스트만 실행:**
- 커서를 테스트 메서드 안에 두고
- `Ctrl+; Ctrl+R` (Windows/Linux)
- `Cmd+; Cmd+R` (Mac)

### 10.2 테스트 디버깅

#### 중단점(Breakpoint) 설정

```java
@Test
void testFindById() {
    // Given
    Long id = 1L;  // ← 여기에 중단점 설정 (줄 번호 클릭)
    
    // When
    Stock stock = stockService.findById(id);
    
    // Then
    assertNotNull(stock);
}
```

#### 디버그 모드 실행

1. 테스트 옆 "Debug Test" 버튼 클릭
2. 또는 `Ctrl+; Ctrl+D`

**디버그 시 할 수 있는 것:**
- 변수 값 확인
- Step Over (F10): 한 줄씩 실행
- Step Into (F11): 메서드 안으로 들어가기
- Continue (F5): 다음 중단점까지 실행

### 10.3 커버리지 시각화

#### Coverage Gutters Extension 설치

1. Extensions에서 "Coverage Gutters" 검색
2. Install 클릭

#### 사용 방법

1. 터미널에서 커버리지 생성
   ```bash
   mvn clean test jacoco:report
   ```

2. VSCode 하단 "Watch" 버튼 클릭

3. 코드 옆에 색상 표시
   ```java
   public Stock findById(Long id) {  // 🟢 테스트됨
       if (id == null) {  // 🔴 테스트 안 됨
           throw new IllegalArgumentException();
       }
       return stockMapper.findById(id);  // 🟢 테스트됨
   }
   ```

**색상 의미:**
- 🟢 초록색: 테스트된 코드
- 🔴 빨간색: 테스트 안 된 코드
- 🟡 노란색: 부분적으로 테스트된 분기

---

## 11. 좋은 테스트 작성법

### 11.1 FIRST 원칙

**F - Fast (빠른):**
```java
// ✗ 나쁨: 실제 DB 사용 (느림)
@Test
void testWithRealDB() {
    Stock stock = stockRepository.save(new Stock());  // 수 초
}

// ✓ 좋음: Mock 사용 (빠름)
@Test
void testWithMock() {
    when(stockMapper.findById(1L)).thenReturn(...);  // 수 밀리초
}
```

**I - Independent (독립적):**
```java
// ✗ 나쁨: 테스트 순서에 의존
@Test
void test1_createStock() {
    stock = stockService.create(dto);
}

@Test
void test2_updateStock() {
    stockService.update(stock);  // test1에 의존!
}

// ✓ 좋음: 각 테스트가 독립적
@Test
void test1_createStock() {
    Stock stock = stockService.create(dto);
    assertNotNull(stock.getId());
}

@Test
void test2_updateStock() {
    Stock stock = createTestStock();  // 자체적으로 준비
    stockService.update(stock);
}
```

**R - Repeatable (반복 가능):**
```java
// ✗ 나쁨: 현재 시간에 의존
@Test
void testIsToday() {
    Stock stock = new Stock();
    stock.setCreatedAt(LocalDate.now());  // 매번 다름!
    assertTrue(stock.getCreatedAt().equals(LocalDate.now()));
}

// ✓ 좋음: 고정된 시간 사용
@Test
void testIsToday() {
    LocalDate fixedDate = LocalDate.of(2024, 1, 1);
    Stock stock = new Stock();
    stock.setCreatedAt(fixedDate);
    assertTrue(stock.getCreatedAt().equals(fixedDate));
}
```

**S - Self-Validating (자가 검증):**
```java
// ✗ 나쁨: 수동 확인 필요
@Test
void testPrint() {
    Stock stock = stockService.findById(1L);
    System.out.println(stock);  // 눈으로 확인?
}

// ✓ 좋음: 자동 검증
@Test
void testFindById() {
    Stock stock = stockService.findById(1L);
    assertEquals("005930", stock.getStockCode());  // 자동 검증
}
```

**T - Timely (적시에):**
```
✗ 나쁨: 개발 완료 후 테스트 작성
✓ 좋음: 개발하면서 또는 개발 전에 테스트 작성 (TDD)
```

### 11.2 테스트 이름 짓기

**패턴: `메서드명_조건_예상결과`**

```java
// ✓ 좋은 테스트 이름
@Test
void findById_WithValidId_ShouldReturnStock() { }

@Test
void findById_WithInvalidId_ShouldThrowException() { }

@Test
void findByMarket_WithKOSPI_ShouldReturnKOSPIStocks() { }

// ✗ 나쁜 테스트 이름
@Test
void test1() { }  // 무엇을 테스트하는지 불명확

@Test
void testStock() { }  // 너무 모호함

@Test
void 테스트() { }  // 영어 권장
```

### 11.3 하나의 테스트는 하나만 검증

```java
// ✗ 나쁨: 너무 많은 것을 한 번에 검증
@Test
void testEverything() {
    Stock stock = stockService.findById(1L);
    assertNotNull(stock);
    assertEquals("005930", stock.getStockCode());
    
    stockService.update(stock);  // 다른 기능 테스트
    
    stockService.delete(stock.getId());  // 또 다른 기능
}

// ✓ 좋음: 각각 분리
@Test
void findById_ShouldReturnStock() {
    Stock stock = stockService.findById(1L);
    assertNotNull(stock);
    assertEquals("005930", stock.getStockCode());
}

@Test
void update_ShouldUpdateStock() {
    Stock stock = createTestStock();
    stock.setStockName("새이름");
    stockService.update(stock);
    // ...
}

@Test
void delete_ShouldDeleteStock() {
    Long id = 1L;
    stockService.delete(id);
    // ...
}
```

---

## 12. 자주 하는 실수

### 12.1 실수 1: Mock을 사용했는데 실제 객체 사용

```java
// ✗ 잘못된 코드
@Mock
private StockMapper stockMapper;

@Test
void test() {
    // Mock을 설정하지 않음!
    Stock stock = stockService.findById(1L);
    // → NullPointerException 발생!
}

// ✓ 올바른 코드
@Mock
private StockMapper stockMapper;

@Test
void test() {
    // Mock 동작 정의
    when(stockMapper.findById(1L))
        .thenReturn(Optional.of(new Stock()));
    
    Stock stock = stockService.findById(1L);
    assertNotNull(stock);
}
```

### 12.2 실수 2: assertEquals의 인자 순서

```java
// ✗ 잘못된 순서
@Test
void test() {
    Stock stock = stockService.findById(1L);
    assertEquals(stock.getStockCode(), "005930");  // 실제값, 기대값 (X)
}

// ✓ 올바른 순서
@Test
void test() {
    Stock stock = stockService.findById(1L);
    assertEquals("005930", stock.getStockCode());  // 기대값, 실제값 (O)
}
```

**에러 메시지 차이:**
```
잘못된 순서: expected: <005931> but was: <005930>  (혼란스러움)
올바른 순서: expected: <005930> but was: <005931>  (명확함)
```

### 12.3 실수 3: @BeforeEach 없이 공통 설정 반복

```java
// ✗ 중복이 많은 코드
@Test
void test1() {
    Stock stock = new Stock();
    stock.setStockCode("005930");
    stock.setStockName("삼성전자");
    // 테스트...
}

@Test
void test2() {
    Stock stock = new Stock();
    stock.setStockCode("005930");  // 중복!
    stock.setStockName("삼성전자");  // 중복!
    // 테스트...
}

// ✓ @BeforeEach 활용
class StockServiceTest {
    private Stock testStock;
    
    @BeforeEach
    void setup() {
        testStock = new Stock();
        testStock.setStockCode("005930");
        testStock.setStockName("삼성전자");
    }
    
    @Test
    void test1() {
        // testStock 바로 사용
    }
    
    @Test
    void test2() {
        // testStock 바로 사용
    }
}
```

### 12.4 실수 4: 예외 테스트 시 메시지 확인 안 함

```java
// ✗ 예외만 확인
@Test
void test() {
    assertThrows(Exception.class, () -> {
        stockService.findById(null);
    });
    // 어떤 Exception이든 통과! (너무 광범위)
}

// ✓ 정확한 예외와 메시지 확인
@Test
void test() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class, 
        () -> stockService.findById(null)
    );
    
    assertTrue(ex.getMessage().contains("ID cannot be null"));
}
```

### 12.5 실수 5: 테스트 데이터 하드코딩

```java
// ✗ 매직 넘버
@Test
void test() {
    Stock stock = stockService.findById(1L);  // 1은 무엇?
    assertEquals("005930", stock.getStockCode());  // 005930은?
}

// ✓ 상수나 변수로 명시
@Test
void test() {
    // Given
    Long SAMSUNG_STOCK_ID = 1L;
    String SAMSUNG_CODE = "005930";
    
    // When
    Stock stock = stockService.findById(SAMSUNG_STOCK_ID);
    
    // Then
    assertEquals(SAMSUNG_CODE, stock.getStockCode());
}
```

---

## 📝 요약

### TDD의 핵심 3단계

1. **🔴 Red**: 실패하는 테스트 작성
2. **🟢 Green**: 테스트를 통과하는 최소 코드
3. **🔵 Refactor**: 코드 개선

### 테스트 작성 체크리스트

- [ ] 테스트 이름이 명확한가? (`메서드_조건_결과`)
- [ ] Given-When-Then 패턴을 따르는가?
- [ ] 하나의 테스트에 하나의 검증만 하는가?
- [ ] Mock 설정을 올바르게 했는가?
- [ ] 예외 테스트에서 메시지도 확인하는가?
- [ ] 테스트가 독립적인가?
- [ ] 테스트가 빠른가?

### VSCode에서 TDD 흐름

```
1. 테스트 작성 (StockServiceTest.java)
   ↓
2. Testing 탭에서 "Run Test" 클릭 (실패 확인)
   ↓
3. 구현 코드 작성 (StockService.java)
   ↓
4. "Run Test" 다시 클릭 (통과 확인)
   ↓
5. 리팩토링
   ↓
6. "Run Test" 다시 클릭 (여전히 통과 확인)
   ↓
7. mvn test → 커버리지 확인
   ↓
8. Coverage Gutters로 시각화
```

### 다음 학습 주제

- **Spring Boot Test Slices**: `@WebMvcTest`, `@DataJpaTest` 등
- **TestContainers**: Docker로 실제 DB 테스트
- **Cucumber**: BDD (Behavior-Driven Development)
- **Performance Testing**: JMeter, Gatling
- **E2E Testing**: Selenium, Cypress

---

## 🎓 실습 과제

### 과제 1: 기본 CRUD 테스트 (초급)
```
StockService의 다음 메서드에 대한 테스트 작성:
- create(StockDTO) - 주식 생성
- update(Long id, StockDTO) - 주식 수정
- delete(Long id) - 주식 삭제

각 메서드당 최소 2개 이상의 테스트 케이스 작성
(정상 케이스 + 예외 케이스)
```

### 과제 2: Controller 테스트 (중급)
```
StockController의 다음 API 테스트:
- POST /api/stocks - 주식 생성
- PUT /api/stocks/{id} - 주식 수정
- DELETE /api/stocks/{id} - 주식 삭제

MockMvc를 사용하여 HTTP 요청/응답 테스트
```

### 과제 3: 커버리지 80% 달성 (고급)
```
전체 프로젝트의 테스트 커버리지를 80% 이상 달성

확인 방법:
mvn clean test jacoco:report
target/site/jacoco/index.html 확인
```

---

**작성자**: Claude  
**작성일**: 2025-12-07  
**대상**: TDD를 배우는 초급 개발자  
**프로젝트**: Stock Prediction System  
**환경**: VSCode + Java 17 + JUnit 5 + Mockito

---

**작성 일시**: 2025-12-07 00:22:15 (한국 시간 기준)
