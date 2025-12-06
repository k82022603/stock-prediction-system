# Spring Boot 초급 개발자 교육 자료
## Stock Prediction System 프로젝트로 배우는 Spring Boot 3.2

---

## 📚 목차

1. [Spring Boot란 무엇인가?](#1-spring-boot란-무엇인가)
2. [Spring Boot의 작동 원리](#2-spring-boot의-작동-원리)
3. [Spring Boot 3.2의 새로운 기능](#3-spring-boot-32의-새로운-기능)
4. [프로젝트 구조 이해하기](#4-프로젝트-구조-이해하기)
5. [핵심 개념 실습](#5-핵심-개념-실습)
6. [실전 예제로 배우기](#6-실전-예제로-배우기)
7. [MyBatis 통합](#7-mybatis-통합)
8. [REST API 개발](#8-rest-api-개발)
9. [자주 하는 실수와 해결법](#9-자주-하는-실수와-해결법)
10. [다음 단계](#10-다음-단계)

---

## 1. Spring Boot란 무엇인가?

### 1.1 간단한 비유로 이해하기

**전통적인 Spring Framework** = 자동차를 직접 조립하기
- 엔진, 바퀴, 핸들 등을 하나하나 조립해야 함
- 모든 부품의 호환성을 직접 확인해야 함
- 설정 파일(XML)이 매우 복잡함

**Spring Boot** = 완성된 자동차 구매하기
- 키만 꽂으면 바로 시동이 걸림
- 대부분의 설정이 자동으로 완료됨
- 필요한 것만 추가/변경하면 됨

### 1.2 Spring Boot의 핵심 특징

#### ① Convention over Configuration (설정보다 관례)

**전통적인 방식:**
```xml
<!-- 100줄 이상의 XML 설정 필요 -->
<bean id="dataSource" class="...">
  <property name="driverClassName" value="..."/>
  <property name="url" value="..."/>
  ...
</bean>
```

**Spring Boot 방식:**
```yaml
# application.yml - 단 4줄!
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_prediction
    username: postgres
```

#### ② 내장 서버

**전통적인 방식:**
- Tomcat 별도 설치
- WAR 파일을 Tomcat에 배포
- Tomcat 설정 파일 수정

**Spring Boot 방식:**
```bash
# 그냥 실행하면 끝!
java -jar myapp.jar
```

#### ③ 의존성 관리 자동화

**전통적인 방식:**
```xml
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-web</artifactId>
  <version>5.3.10</version> <!-- 버전 관리 지옥 -->
</dependency>
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-webmvc</artifactId>
  <version>5.3.10</version> <!-- 같은 버전 유지 필수 -->
</dependency>
<!-- 20개 이상의 의존성... -->
```

**Spring Boot 방식:**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <!-- 버전은 parent가 관리! -->
</dependency>
```

---

## 2. Spring Boot의 작동 원리

### 2.1 마법의 시작: @SpringBootApplication

우리 프로젝트의 메인 클래스를 보겠습니다:

```java
package com.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication          // ← 이것이 핵심!
@MapperScan("com.stock.mapper") // MyBatis 설정
public class StockPredictionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StockPredictionApplication.class, args);
    }
}
```

#### @SpringBootApplication은 3개의 어노테이션 조합

```java
@SpringBootApplication = 
    @SpringBootConfiguration +  // Spring 설정 클래스임을 선언
    @EnableAutoConfiguration +  // 자동 설정 활성화 (마법의 핵심!)
    @ComponentScan              // 컴포넌트 스캔
```

### 2.2 자동 설정(Auto Configuration)의 비밀

**질문: Spring Boot는 어떻게 자동으로 설정을 해주나요?**

**답변: 조건부 설정(Conditional Configuration)을 사용합니다!**

#### 예시: 데이터베이스 자동 설정

```java
// Spring Boot 내부 코드 (개념 설명용)
@Configuration
@ConditionalOnClass(DataSource.class)  // DataSource 클래스가 있으면
@ConditionalOnProperty("spring.datasource.url")  // URL 설정이 있으면
public class DataSourceAutoConfiguration {
    
    @Bean
    public DataSource dataSource() {
        // 자동으로 DataSource를 생성!
        return new HikariDataSource(...);
    }
}
```

**실전 적용: 우리 프로젝트의 경우**

1. pom.xml에 `postgresql` 의존성이 있음 → PostgreSQL 드라이버 발견
2. application.yml에 `spring.datasource.url` 설정이 있음 → 데이터베이스 설정 발견
3. Spring Boot가 자동으로 DataSource, JdbcTemplate 등을 생성!

### 2.3 컴포넌트 스캔(Component Scan)

**Spring Boot는 어떻게 우리가 만든 클래스를 찾나요?**

```
com.stock (메인 클래스 위치)
  ├─ controller/
  │   └─ StockController.java (@RestController) ✓ 발견!
  ├─ service/
  │   └─ StockService.java (@Service) ✓ 발견!
  └─ mapper/
      └─ StockMapper.java (@Mapper) ✓ 발견!
```

**규칙:**
- 메인 클래스(`StockPredictionApplication`)와 같은 패키지 또는 하위 패키지만 스캔
- `@Component`, `@Service`, `@Repository`, `@Controller` 등이 붙은 클래스를 찾아서 Spring Bean으로 등록

---

## 3. Spring Boot 3.2의 새로운 기능

### 3.1 버전별 주요 변화

| 버전 | 주요 변화 |
|------|-----------|
| Spring Boot 1.x | 초기 버전, Java 6-8 지원 |
| Spring Boot 2.x | Java 8+ 필수, Reactive 지원 |
| **Spring Boot 3.x** | **Java 17+ 필수, Jakarta EE, Native 지원** |
| **Spring Boot 3.2** | **Virtual Threads, Observability 강화** |

### 3.2 Spring Boot 2.x → 3.x 주요 변경사항

#### ① Java 17 필수

**왜 Java 17?**
- Record 클래스 지원
- Text Blocks (멀티라인 문자열)
- Pattern Matching
- Sealed Classes

**실전 예제:**

```java
// Java 17의 Record 사용 (간단한 DTO)
public record StockDTO(
    String stockCode,
    String stockName,
    String market
) {}

// 이전 방식 (Lombok 사용)
@Data
@AllArgsConstructor
public class StockDTO {
    private String stockCode;
    private String stockName;
    private String market;
}
```

#### ② Jakarta EE로 전환

**변경 전 (Spring Boot 2.x):**
```java
import javax.servlet.http.HttpServletRequest;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
```

**변경 후 (Spring Boot 3.x):**
```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
```

**우리 프로젝트는 MyBatis를 사용하므로 영향이 적음!**

#### ③ Native Image 지원 강화

**Native Image란?**
- Java 애플리케이션을 네이티브 실행 파일로 컴파일
- JVM 없이도 실행 가능
- 시작 시간이 수십 밀리초로 단축
- 메모리 사용량 대폭 감소

**예시:**
```bash
# 기존 Spring Boot
java -jar myapp.jar  # 시작 시간: 5-10초

# Native Image
./myapp  # 시작 시간: 0.05초!
```

### 3.3 Spring Boot 3.2의 핵심 기능

#### ① Virtual Threads (가상 스레드)

**기존 Thread 방식:**
```java
// 1000개의 동시 요청 = 1000개의 OS Thread 필요
// → 메모리 부족 발생 가능
```

**Virtual Threads 방식:**
```java
// application.yml
spring:
  threads:
    virtual:
      enabled: true

// 수백만 개의 가상 스레드 생성 가능!
// OS Thread는 소수만 사용
```

**실전 효과:**
- 동시 처리 가능한 요청 수가 10배 이상 증가
- 메모리 사용량은 그대로

#### ② 향상된 Observability (관찰 가능성)

```java
// Spring Boot 3.2에서 자동으로 제공
// 별도 설정 없이 사용 가능!

// Actuator 의존성만 추가하면 됨
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**제공되는 정보:**
- HTTP 요청/응답 추적
- 데이터베이스 쿼리 모니터링
- 메모리 사용량 실시간 확인
- CPU 사용률 추적

---

## 4. 프로젝트 구조 이해하기

### 4.1 Maven 프로젝트 구조

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/stock/           ← Java 소스 코드
│   │   └── resources/
│   │       ├── application.yml      ← 설정 파일
│   │       └── mapper/              ← MyBatis XML
│   └── test/                        ← 테스트 코드
└── pom.xml                          ← Maven 설정
```

### 4.2 패키지 구조 (Layered Architecture)

```
com.stock
├── StockPredictionApplication.java  ← 메인 클래스
├── controller/                      ← Presentation Layer
│   ├── StockController.java         (REST API 엔드포인트)
│   └── PredictionController.java
├── service/                         ← Business Layer
│   ├── StockService.java            (비즈니스 로직)
│   └── PredictionService.java
├── mapper/                          ← Persistence Layer
│   ├── StockMapper.java             (데이터베이스 접근)
│   └── PredictionMapper.java
├── model/                           ← Domain Model
│   ├── Stock.java                   (도메인 객체)
│   └── Prediction.java
└── dto/                             ← Data Transfer Object
    └── StockDTO.java                (API 응답용 객체)
```

**각 Layer의 역할:**

| Layer | 역할 | 예시 |
|-------|------|------|
| Controller | HTTP 요청/응답 처리 | `@GetMapping("/stocks")` |
| Service | 비즈니스 로직 | 주가 예측 계산 |
| Mapper | 데이터베이스 작업 | SELECT, INSERT, UPDATE |
| Model | 데이터 구조 정의 | Stock, Prediction |

### 4.3 의존성 주입(Dependency Injection)

**나쁜 예 (강한 결합):**
```java
public class StockController {
    private StockService service = new StockService(); // ✗ 직접 생성
}
```

**좋은 예 (느슨한 결합):**
```java
@RestController
@RequiredArgsConstructor  // Lombok: final 필드의 생성자 자동 생성
public class StockController {
    private final StockService service; // ✓ Spring이 주입
}
```

**Spring이 하는 일:**
1. StockService 객체 생성
2. StockController 객체 생성
3. StockService를 StockController에 주입

---

## 5. 핵심 개념 실습

### 5.1 REST Controller 만들기

#### Step 1: 기본 Controller

```java
package com.stock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // ← REST API 컨트롤러임을 선언
public class HelloController {
    
    @GetMapping("/hello")  // ← GET /hello 요청 처리
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

**테스트:**
```bash
# 서버 실행 후
curl http://localhost:8080/hello
# 출력: Hello, Spring Boot!
```

#### Step 2: JSON 응답

```java
@RestController
public class StockController {
    
    @GetMapping("/api/stock")
    public Stock getStock() {
        Stock stock = new Stock();
        stock.setStockCode("005930");
        stock.setStockName("삼성전자");
        return stock;  // ← 자동으로 JSON 변환!
    }
}
```

**응답:**
```json
{
  "stockCode": "005930",
  "stockName": "삼성전자"
}
```

**왜 자동으로 JSON이 되나요?**
- Spring Boot가 Jackson 라이브러리를 자동으로 포함
- `@RestController`는 모든 메서드에 `@ResponseBody` 적용
- Java 객체 → JSON 자동 변환

#### Step 3: 실제 프로젝트 Controller

```java
package com.stock.controller;

import com.stock.dto.StockDTO;
import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")  // ← 기본 경로: /stocks
@RequiredArgsConstructor    // ← 생성자 주입
@CrossOrigin(origins = "*") // ← CORS 허용
public class StockController {

    private final StockService stockService;

    // GET /stocks - 전체 주식 목록
    @GetMapping
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        List<StockDTO> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    // GET /stocks/{stockCode} - 특정 주식 조회
    @GetMapping("/{stockCode}")
    public ResponseEntity<StockDTO> getStockByCode(
            @PathVariable String stockCode) {
        StockDTO stock = stockService.getStockByCode(stockCode);
        return ResponseEntity.ok(stock);
    }
}
```

**핵심 어노테이션 설명:**

| 어노테이션 | 역할 | 예시 |
|-----------|------|------|
| `@RestController` | REST API 컨트롤러 | JSON 자동 변환 |
| `@RequestMapping` | 기본 경로 설정 | `/stocks` |
| `@GetMapping` | GET 요청 처리 | 조회 |
| `@PostMapping` | POST 요청 처리 | 생성 |
| `@PathVariable` | URL 경로 변수 | `/{id}` |
| `@RequestBody` | HTTP Body → 객체 | JSON → Java |

### 5.2 Service Layer 만들기

```java
package com.stock.service;

import com.stock.dto.StockDTO;
import com.stock.mapper.StockMapper;
import com.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service  // ← Service Bean 등록
@RequiredArgsConstructor
@Transactional(readOnly = true)  // ← 읽기 전용 트랜잭션
public class StockService {

    private final StockMapper stockMapper;

    public List<StockDTO> getAllStocks() {
        // 1. Mapper로 DB에서 조회
        List<Stock> stocks = stockMapper.findAll();
        
        // 2. Model → DTO 변환
        return stocks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StockDTO getStockByCode(String stockCode) {
        Stock stock = stockMapper.findByStockCode(stockCode)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        return convertToDTO(stock);
    }

    private StockDTO convertToDTO(Stock stock) {
        StockDTO dto = new StockDTO();
        dto.setStockCode(stock.getStockCode());
        dto.setStockName(stock.getStockName());
        dto.setMarket(stock.getMarket());
        return dto;
    }
}
```

**@Transactional 이해하기:**

```java
@Transactional  // ← 트랜잭션 시작
public void updateStock(Stock stock) {
    stockMapper.update(stock);       // SQL 1
    priceMapper.updatePrice(price);  // SQL 2
    
    // 둘 다 성공 → COMMIT
    // 하나라도 실패 → ROLLBACK
}
```

---

## 6. 실전 예제로 배우기

### 6.1 새로운 API 추가하기

**요구사항: 시장별(KOSPI/KOSDAQ) 주식 조회 기능 추가**

#### Step 1: Mapper 인터페이스에 메서드 추가

```java
package com.stock.mapper;

import com.stock.model.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {
    
    List<Stock> findAll();
    
    // ← 새로 추가!
    List<Stock> findByMarket(@Param("market") String market);
}
```

#### Step 2: Mapper XML에 쿼리 추가

```xml
<!-- resources/mapper/StockMapper.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.stock.mapper.StockMapper">

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

</mapper>
```

#### Step 3: Service에 비즈니스 로직 추가

```java
@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMapper stockMapper;
    
    // ← 새로 추가!
    public List<StockDTO> getStocksByMarket(String market) {
        // 입력 검증
        if (!market.equals("KOSPI") && !market.equals("KOSDAQ")) {
            throw new IllegalArgumentException(
                "Market must be KOSPI or KOSDAQ");
        }
        
        return stockMapper.findByMarket(market)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
```

#### Step 4: Controller에 엔드포인트 추가

```java
@RestController
@RequestMapping("/stocks")
public class StockController {
    
    private final StockService stockService;
    
    // ← 새로 추가!
    // GET /stocks/market/KOSPI
    @GetMapping("/market/{market}")
    public ResponseEntity<List<StockDTO>> getStocksByMarket(
            @PathVariable String market) {
        List<StockDTO> stocks = stockService.getStocksByMarket(market);
        return ResponseEntity.ok(stocks);
    }
}
```

#### Step 5: 테스트

```bash
# KOSPI 주식 조회
curl http://localhost:8080/stocks/market/KOSPI

# KOSDAQ 주식 조회
curl http://localhost:8080/stocks/market/KOSDAQ
```

### 6.2 POST 요청 처리하기

**요구사항: 새로운 주식 추가 기능**

```java
// Controller
@PostMapping
public ResponseEntity<StockDTO> createStock(
        @RequestBody StockDTO stockDTO) {  // ← JSON → 객체 자동 변환
    StockDTO created = stockService.createStock(stockDTO);
    return ResponseEntity
            .status(HttpStatus.CREATED)  // ← 201 Created
            .body(created);
}

// Service
@Transactional  // ← 쓰기 작업은 readOnly = false
public StockDTO createStock(StockDTO dto) {
    Stock stock = new Stock();
    stock.setStockCode(dto.getStockCode());
    stock.setStockName(dto.getStockName());
    stock.setMarket(dto.getMarket());
    
    stockMapper.insert(stock);
    return convertToDTO(stock);
}
```

**테스트:**
```bash
curl -X POST http://localhost:8080/stocks \
  -H "Content-Type: application/json" \
  -d '{
    "stockCode": "000001",
    "stockName": "테스트주식",
    "market": "KOSPI"
  }'
```

---

## 7. MyBatis 통합

### 7.1 MyBatis란?

**MyBatis = SQL 매퍼 프레임워크**

- SQL을 Java 코드에서 분리
- XML에 SQL 작성
- 결과를 자동으로 Java 객체에 매핑

### 7.2 MyBatis 설정

#### pom.xml에 의존성 추가

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

#### application.yml 설정

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_prediction
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

mybatis:
  mapper-locations: classpath:mapper/**/*.xml  # XML 위치
  type-aliases-package: com.stock.model        # 별칭 패키지
  configuration:
    map-underscore-to-camel-case: true         # snake_case → camelCase
```

#### 메인 클래스에 @MapperScan 추가

```java
@SpringBootApplication
@MapperScan("com.stock.mapper")  // ← Mapper 인터페이스 스캔
public class StockPredictionApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockPredictionApplication.class, args);
    }
}
```

### 7.3 Mapper 인터페이스 작성

```java
package com.stock.mapper;

import com.stock.model.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper  // ← MyBatis Mapper임을 표시
public interface StockMapper {
    
    // 전체 조회
    List<Stock> findAll();
    
    // ID로 조회
    Optional<Stock> findById(@Param("id") Long id);
    
    // 종목 코드로 조회
    Optional<Stock> findByStockCode(@Param("stockCode") String stockCode);
    
    // 삽입
    int insert(Stock stock);
    
    // 수정
    int update(Stock stock);
    
    // 삭제
    int deleteById(@Param("id") Long id);
}
```

**@Param 언제 사용?**
- 파라미터가 1개: 생략 가능
- 파라미터가 2개 이상: 필수!

```java
// ✓ OK
Stock findById(Long id);

// ✗ 에러 발생!
Stock findByCodeAndMarket(String code, String market);

// ✓ OK
Stock findByCodeAndMarket(
    @Param("code") String code, 
    @Param("market") String market
);
```

### 7.4 Mapper XML 작성

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.stock.mapper.StockMapper">

    <!-- ResultMap 정의: DB 컬럼 → Java 객체 매핑 -->
    <resultMap id="stockResultMap" type="com.stock.model.Stock">
        <id property="id" column="id"/>
        <result property="stockCode" column="stock_code"/>
        <result property="stockName" column="stock_name"/>
        <result property="market" column="market"/>
        <result property="sector" column="sector"/>
    </resultMap>

    <!-- 전체 조회 -->
    <select id="findAll" resultMap="stockResultMap">
        SELECT * FROM stocks
        ORDER BY stock_code
    </select>

    <!-- ID로 조회 -->
    <select id="findById" resultMap="stockResultMap">
        SELECT * FROM stocks
        WHERE id = #{id}
    </select>

    <!-- 삽입 -->
    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO stocks (stock_code, stock_name, market, sector)
        VALUES (#{stockCode}, #{stockName}, #{market}, #{sector})
    </insert>

    <!-- 수정 -->
    <update id="update">
        UPDATE stocks
        SET stock_name = #{stockName},
            market = #{market},
            sector = #{sector}
        WHERE id = #{id}
    </update>

    <!-- 삭제 -->
    <delete id="deleteById">
        DELETE FROM stocks
        WHERE id = #{id}
    </delete>

</mapper>
```

**핵심 포인트:**

| 항목 | 설명 |
|------|------|
| `namespace` | Mapper 인터페이스의 전체 경로 (패키지명 포함) |
| `id` | Mapper 인터페이스의 메서드 이름 |
| `resultMap` | 복잡한 매핑 (컬럼명 ≠ 필드명) |
| `resultType` | 간단한 매핑 (직접 타입 지정) |
| `#{변수}` | PreparedStatement (SQL Injection 방지) |
| `${변수}` | 문자열 치환 (주의: SQL Injection 위험) |

### 7.5 ResultMap vs ResultType

#### ResultType (간단한 경우)

```xml
<!-- 컬럼명 = 필드명 (또는 자동 변환 가능) -->
<select id="findAll" resultType="com.stock.model.Stock">
    SELECT 
        id,
        stock_code AS stockCode,  <!-- AS로 이름 맞춤 -->
        stock_name AS stockName,
        market
    FROM stocks
</select>
```

#### ResultMap (복잡한 경우)

```xml
<!-- JOIN 결과 매핑 -->
<resultMap id="predictionWithStock" type="com.stock.model.Prediction">
    <id property="id" column="id"/>
    <result property="predictedPrice" column="predicted_price"/>
    
    <!-- 연관 객체 매핑 -->
    <association property="stock" javaType="com.stock.model.Stock">
        <id property="id" column="stock_id"/>
        <result property="stockCode" column="stock_code"/>
        <result property="stockName" column="stock_name"/>
    </association>
</resultMap>

<select id="findWithStock" resultMap="predictionWithStock">
    SELECT 
        p.id,
        p.predicted_price,
        s.id AS stock_id,
        s.stock_code,
        s.stock_name
    FROM predictions p
    JOIN stocks s ON p.stock_id = s.id
</select>
```

---

## 8. REST API 개발

### 8.1 HTTP 메서드와 상태 코드

#### HTTP 메서드

| 메서드 | 용도 | 예시 |
|--------|------|------|
| GET | 조회 | `GET /stocks/005930` |
| POST | 생성 | `POST /stocks` |
| PUT | 전체 수정 | `PUT /stocks/1` |
| PATCH | 부분 수정 | `PATCH /stocks/1` |
| DELETE | 삭제 | `DELETE /stocks/1` |

#### HTTP 상태 코드

| 코드 | 의미 | 사용 예 |
|------|------|---------|
| 200 | OK | 조회 성공 |
| 201 | Created | 생성 성공 |
| 204 | No Content | 삭제 성공 (본문 없음) |
| 400 | Bad Request | 잘못된 요청 |
| 404 | Not Found | 리소스 없음 |
| 500 | Internal Server Error | 서버 오류 |

### 8.2 RESTful API 설계

**좋은 API 설계:**

```java
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
    // ✓ 전체 조회
    @GetMapping
    public List<Stock> getAll() { ... }
    
    // ✓ 단건 조회
    @GetMapping("/{id}")
    public Stock getById(@PathVariable Long id) { ... }
    
    // ✓ 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Stock create(@RequestBody Stock stock) { ... }
    
    // ✓ 수정
    @PutMapping("/{id}")
    public Stock update(
        @PathVariable Long id, 
        @RequestBody Stock stock) { ... }
    
    // ✓ 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { ... }
}
```

**나쁜 API 설계:**

```java
// ✗ GET으로 삭제 (의미 불명확)
@GetMapping("/deleteStock")
public void delete(@RequestParam Long id) { ... }

// ✗ 동사 사용 (REST는 명사 중심)
@PostMapping("/createStock")
public Stock create(@RequestBody Stock stock) { ... }

// ✗ 복수형 불일치
@GetMapping("/stock/{id}")  // stock (단수)
@GetMapping("/stocks")      // stocks (복수)
```

### 8.3 ResponseEntity 사용하기

**기본 응답:**
```java
@GetMapping("/{id}")
public Stock getById(@PathVariable Long id) {
    return stockService.findById(id);
    // → 항상 200 OK
}
```

**ResponseEntity 사용 (권장):**
```java
@GetMapping("/{id}")
public ResponseEntity<Stock> getById(@PathVariable Long id) {
    Stock stock = stockService.findById(id);
    
    if (stock == null) {
        return ResponseEntity.notFound().build();  // 404
    }
    
    return ResponseEntity.ok(stock);  // 200
}
```

**다양한 상태 코드:**
```java
// 200 OK
ResponseEntity.ok(data);

// 201 Created
ResponseEntity.status(HttpStatus.CREATED).body(data);

// 204 No Content
ResponseEntity.noContent().build();

// 400 Bad Request
ResponseEntity.badRequest().body("Invalid data");

// 404 Not Found
ResponseEntity.notFound().build();
```

### 8.4 예외 처리

#### 커스텀 예외 클래스

```java
package com.stock.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

#### 전역 예외 처리

```java
package com.stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  // ← 모든 Controller에 적용
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}

// 에러 응답 DTO
@Data
@AllArgsConstructor
class ErrorResponse {
    private int status;
    private String message;
}
```

#### 사용 예시

```java
@Service
public class StockService {
    
    public StockDTO findById(Long id) {
        Stock stock = stockMapper.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Stock not found with id: " + id));
        
        return convertToDTO(stock);
    }
}
```

---

## 9. 자주 하는 실수와 해결법

### 9.1 실수 1: @Mapper 어노테이션 누락

**증상:**
```
org.apache.ibatis.binding.BindingException: 
Invalid bound statement (not found)
```

**원인:**
```java
// ✗ @Mapper 없음
public interface StockMapper {
    List<Stock> findAll();
}
```

**해결:**
```java
// ✓ @Mapper 추가
@Mapper
public interface StockMapper {
    List<Stock> findAll();
}
```

### 9.2 실수 2: Mapper XML namespace 불일치

**증상:**
```
org.apache.ibatis.binding.BindingException
```

**원인:**
```xml
<!-- ✗ 패키지 경로 오타 -->
<mapper namespace="com.stock.mappers.StockMapper">
```

**해결:**
```xml
<!-- ✓ 정확한 패키지 경로 -->
<mapper namespace="com.stock.mapper.StockMapper">
```

### 9.3 실수 3: @RequestBody 누락

**증상:**
```
POST 요청 시 파라미터가 null로 들어옴
```

**원인:**
```java
// ✗ @RequestBody 없음
@PostMapping
public Stock create(Stock stock) { ... }
```

**해결:**
```java
// ✓ @RequestBody 추가
@PostMapping
public Stock create(@RequestBody Stock stock) { ... }
```

### 9.4 실수 4: CORS 오류

**증상:**
```
Access to fetch at 'http://localhost:8080/api/stocks' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**해결 1: Controller에 @CrossOrigin**
```java
@RestController
@CrossOrigin(origins = "*")  // ← 모든 도메인 허용 (개발용)
public class StockController { ... }
```

**해결 2: 전역 설정 (권장)**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

### 9.5 실수 5: 트랜잭션 누락

**증상:**
```
데이터 변경이 커밋되지 않음
```

**원인:**
```java
// ✗ @Transactional 없음
public void update(Stock stock) {
    stockMapper.update(stock);
}
```

**해결:**
```java
// ✓ @Transactional 추가
@Transactional
public void update(Stock stock) {
    stockMapper.update(stock);
}
```

---

## 10. 다음 단계

### 10.1 학습 로드맵

#### Level 1: 기초 (현재 단계)
- ✓ Spring Boot 기본 개념
- ✓ REST API 개발
- ✓ MyBatis 연동
- ✓ 기본 CRUD 구현

#### Level 2: 중급
- [ ] Spring Security (인증/인가)
- [ ] JPA 학습
- [ ] 테스트 코드 작성 (JUnit, Mockito)
- [ ] 로깅 (Logback, SLF4J)

#### Level 3: 고급
- [ ] Spring Cloud (MSA)
- [ ] Redis 캐싱
- [ ] 메시지 큐 (Kafka, RabbitMQ)
- [ ] 모니터링 (Actuator, Prometheus)

### 10.2 실습 과제

#### 과제 1: 섹터별 주식 조회 API
```
GET /api/stocks/sector/{sector}
- IT, 금융, 제조업 등 섹터별로 주식 조회
```

#### 과제 2: 주식 검색 API
```
GET /api/stocks/search?keyword=삼성
- 종목명에 키워드가 포함된 주식 조회
```

#### 과제 3: 페이징 API
```
GET /api/stocks?page=1&size=10
- 페이지별로 주식 목록 조회
```

#### 과제 4: 주가 통계 API
```
GET /api/stocks/{id}/statistics
- 최고가, 최저가, 평균가 계산
```

### 10.3 추천 학습 자료

**공식 문서:**
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [MyBatis 공식 문서](https://mybatis.org/mybatis-3/)

**온라인 강의:**
- 인프런: "스프링 부트와 JPA 실무 완전 정복"
- Udemy: "Spring Framework Master Class"

**책:**
- "스프링 부트 실전 활용 마스터" (정운각)
- "자바 ORM 표준 JPA 프로그래밍" (김영한)

---

## 📝 요약

### Spring Boot 핵심 정리

1. **Spring Boot = Spring Framework + 자동 설정**
   - 설정 파일 최소화
   - 내장 서버 (Tomcat)
   - 의존성 자동 관리

2. **주요 어노테이션**
   - `@SpringBootApplication`: 메인 클래스
   - `@RestController`: REST API 컨트롤러
   - `@Service`: 비즈니스 로직
   - `@Mapper`: MyBatis 매퍼

3. **계층 구조**
   ```
   Controller → Service → Mapper → Database
   ```

4. **MyBatis 핵심**
   - 인터페이스 + XML로 SQL 관리
   - `#{변수}`: PreparedStatement (안전)
   - `ResultMap`: 복잡한 매핑

5. **REST API 설계**
   - GET: 조회
   - POST: 생성
   - PUT: 수정
   - DELETE: 삭제

### Spring Boot 3.2의 차별점

- Java 17 필수
- Jakarta EE 전환
- Virtual Threads 지원
- 성능 향상 및 메모리 최적화

---

**작성자**: Claude  
**작성일**: 2025-12-06  
**대상**: Spring Boot 초급 개발자  
**프로젝트**: Stock Prediction System (Spring Boot 3.2 + MyBatis 3.0)

**다음 학습**: [JPA로 전환하기], [Spring Security 적용하기], [테스트 코드 작성하기]

---

**작성 일시**: 2025-12-07 00:05:23 (한국 시간 기준)
