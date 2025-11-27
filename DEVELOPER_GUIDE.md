# 개발자 가이드 - 주식 예측 시스템

## 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [개발 환경 설정](#개발-환경-설정)
4. [아키텍처](#아키텍처)
5. [MyBatis 가이드](#mybatis-가이드)
6. [API 개발 가이드](#api-개발-가이드)
7. [프론트엔드 개발](#프론트엔드-개발)
8. [데이터베이스 설계](#데이터베이스-설계)
9. [테스트 작성](#테스트-작성)
10. [배포 가이드](#배포-가이드)
11. [트러블슈팅](#트러블슈팅)

---

## 프로젝트 개요

### 프로젝트 목적
한국 주식 시장의 내일 주가를 예측하는 시스템을 구축하여, 투자자들에게 AI 기반 예측 정보를 제공합니다.

### 주요 기능
- 주식 종목 관리 및 조회
- 주가 데이터 수집 및 저장
- AI 기반 주가 예측
- 예측 신뢰도 계산
- 실시간 데이터 시각화

### 프로젝트 특징
- **MyBatis 기반**: SQL을 직접 제어하여 성능 최적화
- **RESTful API**: 표준 REST 아키텍처
- **React SPA**: 단일 페이지 애플리케이션
- **JBoss 배포 지원**: 엔터프라이즈 환경 대응

---

## 기술 스택

### Backend
```
- Java 17
- Spring Boot 3.2.0
- MyBatis 3.0.3
- PostgreSQL 15+
- Maven 3.9+
- Lombok
```

### Frontend
```
- React 18
- Axios (HTTP Client)
- Recharts (차트 라이브러리)
- CSS3
```

### Database
```
- PostgreSQL 15
- HikariCP (Connection Pool)
```

### DevOps
```
- Git
- Maven
- npm
- JBoss/WildFly (optional)
```

---

## 개발 환경 설정

### 필수 소프트웨어

1. **JDK 17 설치**
```bash
# 확인
java -version

# OpenJDK 17 권장
https://openjdk.org/projects/jdk/17/
```

2. **Maven 설치**
```bash
# 확인
mvn -version

# 설치 위치
d:/tools/apache-maven-3.9.6
```

3. **PostgreSQL 설치**
```bash
# 확인
psql --version

# 설치 위치
d:/tools/pgsql
```

4. **Node.js 설치**
```bash
# 확인
node -version
npm -version

# LTS 버전 권장
https://nodejs.org/
```

### IDE 설정

#### IntelliJ IDEA (권장)

1. **프로젝트 열기**
   - File → Open → backend 폴더 선택
   - Maven 프로젝트로 자동 인식

2. **Lombok 플러그인 설치**
   - Settings → Plugins → "Lombok" 검색 및 설치
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - "Enable annotation processing" 체크

3. **MyBatis 플러그인 설치** (선택사항)
   - Settings → Plugins → "MyBatis" 검색 및 설치
   - Mapper와 XML 간 네비게이션 지원

4. **데이터베이스 연결**
   - Database Tool → + → Data Source → PostgreSQL
   - Host: localhost, Port: 5432
   - Database: stock_prediction
   - User: postgres, Password: postgres

#### VS Code

1. **확장 프로그램 설치**
```
- Java Extension Pack
- Spring Boot Extension Pack
- ES7+ React/Redux/React-Native snippets
- PostgreSQL
```

2. **설정**
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/path/to/jdk-17"
    }
  ]
}
```

---

## 아키텍처

### 전체 구조

```
┌─────────────┐      HTTP      ┌──────────────┐      JDBC      ┌──────────────┐
│   Browser   │ ←──────────────→ │ Spring Boot  │ ←─────────────→ │ PostgreSQL   │
│   (React)   │    REST API     │  + MyBatis   │   SQL Query    │   Database   │
└─────────────┘                 └──────────────┘                └──────────────┘
     :3000                            :8080                          :5432
```

### 백엔드 계층 구조

```
Controller Layer (REST API)
     ↓
Service Layer (Business Logic)
     ↓
Mapper Layer (MyBatis Interface)
     ↓
Mapper XML (SQL Queries)
     ↓
Database (PostgreSQL)
```

### 패키지 구조

```
com.stock
├── controller/          # REST API 엔드포인트
│   ├── StockController.java
│   └── PredictionController.java
├── service/             # 비즈니스 로직
│   ├── StockService.java
│   └── PredictionService.java
├── mapper/              # MyBatis Mapper 인터페이스
│   ├── StockMapper.java
│   ├── StockPriceMapper.java
│   └── PredictionMapper.java
├── model/               # 도메인 모델 (POJO)
│   ├── Stock.java
│   ├── StockPrice.java
│   └── Prediction.java
├── dto/                 # 데이터 전송 객체
│   ├── StockDTO.java
│   └── PredictionDTO.java
├── config/              # 설정 클래스
│   └── WebConfig.java
└── StockPredictionApplication.java  # 메인 클래스
```

---

## MyBatis 가이드

### Mapper 인터페이스 작성

```java
@Mapper
public interface StockMapper {

    // 전체 조회
    List<Stock> findAll();

    // ID로 조회
    Optional<Stock> findById(@Param("id") Long id);

    // 조건부 조회
    List<Stock> findByMarket(@Param("market") String market);

    // 삽입
    int insert(Stock stock);

    // 수정
    int update(Stock stock);

    // 삭제
    int deleteById(@Param("id") Long id);
}
```

**주의사항:**
- `@Mapper` 어노테이션 필수
- 파라미터가 2개 이상이면 `@Param` 필수
- 반환 타입을 명확히 지정

### Mapper XML 작성

**위치:** `src/main/resources/mapper/StockMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.stock.mapper.StockMapper">

    <!-- ResultMap 정의 -->
    <resultMap id="stockResultMap" type="com.stock.model.Stock">
        <id property="id" column="id"/>
        <result property="stockCode" column="stock_code"/>
        <result property="stockName" column="stock_name"/>
        <result property="market" column="market"/>
        <result property="sector" column="sector"/>
        <result property="createdAt" column="created_at"/>
        <result property="updatedAt" column="updated_at"/>
    </resultMap>

    <!-- 기본 SELECT -->
    <select id="findAll" resultMap="stockResultMap">
        SELECT id, stock_code, stock_name, market, sector,
               created_at, updated_at
        FROM stocks
        ORDER BY stock_code
    </select>

    <!-- 파라미터 바인딩 -->
    <select id="findById" resultMap="stockResultMap">
        SELECT id, stock_code, stock_name, market, sector,
               created_at, updated_at
        FROM stocks
        WHERE id = #{id}
    </select>

    <!-- 동적 SQL -->
    <select id="findByCondition" resultMap="stockResultMap">
        SELECT * FROM stocks
        <where>
            <if test="market != null">
                AND market = #{market}
            </if>
            <if test="sector != null">
                AND sector = #{sector}
            </if>
        </where>
    </select>

    <!-- INSERT -->
    <insert id="insert" parameterType="com.stock.model.Stock"
            useGeneratedKeys="true" keyProperty="id">
        INSERT INTO stocks (stock_code, stock_name, market, sector,
                           created_at, updated_at)
        VALUES (#{stockCode}, #{stockName}, #{market}, #{sector},
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    </insert>

    <!-- UPDATE -->
    <update id="update">
        UPDATE stocks
        SET stock_name = #{stockName},
            market = #{market},
            sector = #{sector},
            updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
    </update>

    <!-- DELETE -->
    <delete id="deleteById">
        DELETE FROM stocks WHERE id = #{id}
    </delete>

</mapper>
```

### Association (JOIN 처리)

```xml
<resultMap id="predictionResultMap" type="com.stock.model.Prediction">
    <id property="id" column="id"/>
    <result property="stockId" column="stock_id"/>
    <result property="predictedPrice" column="predicted_price"/>

    <!-- Stock 정보 조인 -->
    <association property="stock" javaType="com.stock.model.Stock">
        <id property="id" column="stock_id"/>
        <result property="stockCode" column="stock_code"/>
        <result property="stockName" column="stock_name"/>
    </association>
</resultMap>

<select id="findByTargetDate" resultMap="predictionResultMap">
    SELECT p.id, p.stock_id, p.predicted_price,
           s.stock_code, s.stock_name
    FROM predictions p
    INNER JOIN stocks s ON p.stock_id = s.id
    WHERE p.target_date = #{targetDate}
</select>
```

### 동적 SQL 예제

```xml
<!-- foreach: IN 절 -->
<select id="findByStockCodes" resultMap="stockResultMap">
    SELECT * FROM stocks
    WHERE stock_code IN
    <foreach collection="codes" item="code" open="(" close=")" separator=",">
        #{code}
    </foreach>
</select>

<!-- choose/when/otherwise: Switch 문 -->
<select id="findByType" resultMap="stockResultMap">
    SELECT * FROM stocks
    <where>
        <choose>
            <when test="type == 'IT'">
                AND sector = 'IT'
            </when>
            <when test="type == 'FINANCE'">
                AND sector = '금융'
            </when>
            <otherwise>
                AND 1=1
            </otherwise>
        </choose>
    </where>
</select>

<!-- set: UPDATE 동적 필드 -->
<update id="updateDynamic">
    UPDATE stocks
    <set>
        <if test="stockName != null">stock_name = #{stockName},</if>
        <if test="market != null">market = #{market},</if>
        <if test="sector != null">sector = #{sector},</if>
        updated_at = CURRENT_TIMESTAMP
    </set>
    WHERE id = #{id}
</update>
```

---

## API 개발 가이드

### Controller 작성

```java
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    /**
     * 전체 주식 목록 조회
     * GET /api/stocks
     */
    @GetMapping
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        List<StockDTO> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    /**
     * 특정 주식 조회
     * GET /api/stocks/{stockCode}
     */
    @GetMapping("/{stockCode}")
    public ResponseEntity<StockDTO> getStockByCode(
            @PathVariable String stockCode) {
        StockDTO stock = stockService.getStockByCode(stockCode);
        return ResponseEntity.ok(stock);
    }

    /**
     * 주식 생성
     * POST /api/stocks
     */
    @PostMapping
    public ResponseEntity<StockDTO> createStock(
            @RequestBody @Valid StockDTO stockDTO) {
        StockDTO created = stockService.createStock(stockDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * 주식 수정
     * PUT /api/stocks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockDTO> updateStock(
            @PathVariable Long id,
            @RequestBody @Valid StockDTO stockDTO) {
        StockDTO updated = stockService.updateStock(id, stockDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * 주식 삭제
     * DELETE /api/stocks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Service 작성

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockMapper stockMapper;

    public List<StockDTO> getAllStocks() {
        return stockMapper.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StockDTO getStockByCode(String stockCode) {
        Stock stock = stockMapper.findByStockCode(stockCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Stock not found: " + stockCode));
        return convertToDTO(stock);
    }

    @Transactional
    public StockDTO createStock(StockDTO dto) {
        Stock stock = convertToEntity(dto);
        stockMapper.insert(stock);
        return convertToDTO(stock);
    }

    @Transactional
    public StockDTO updateStock(Long id, StockDTO dto) {
        Stock existing = stockMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Stock not found: " + id));

        existing.setStockName(dto.getStockName());
        existing.setMarket(dto.getMarket());
        existing.setSector(dto.getSector());

        stockMapper.update(existing);
        return convertToDTO(existing);
    }

    @Transactional
    public void deleteStock(Long id) {
        stockMapper.deleteById(id);
    }

    private StockDTO convertToDTO(Stock stock) {
        return StockDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .market(stock.getMarket())
                .sector(stock.getSector())
                .build();
    }

    private Stock convertToEntity(StockDTO dto) {
        return Stock.builder()
                .stockCode(dto.getStockCode())
                .stockName(dto.getStockName())
                .market(dto.getMarket())
                .sector(dto.getSector())
                .build();
    }
}
```

### 예외 처리

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal server error")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
```

---

## 프론트엔드 개발

### 프로젝트 구조

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/          # React 컴포넌트
│   │   ├── StockList.js
│   │   └── PredictionCard.js
│   ├── services/            # API 통신
│   │   └── api.js
│   ├── types/               # TypeScript 타입 (선택)
│   ├── App.js               # 메인 앱
│   ├── App.css              # 스타일
│   └── index.js             # 진입점
└── package.json
```

### API 서비스 작성

```javascript
// services/api.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    // 인증 토큰이 있으면 추가
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 인증 실패 처리
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const stockApi = {
  getAllStocks: () => api.get('/stocks'),
  getStockByCode: (code) => api.get(`/stocks/${code}`),
  createStock: (data) => api.post('/stocks', data),
  updateStock: (id, data) => api.put(`/stocks/${id}`, data),
  deleteStock: (id) => api.delete(`/stocks/${id}`),
};

export const predictionApi = {
  getTomorrowPredictions: () => api.get('/predictions/tomorrow'),
  getPredictionsByStock: (code, date) =>
    api.get(`/predictions/stock/${code}`, { params: { targetDate: date } }),
  createPrediction: (data) => api.post('/predictions', data),
};

export default api;
```

### 컴포넌트 작성

```javascript
// components/StockList.js
import React, { useState, useEffect } from 'react';
import { stockApi } from '../services/api';

const StockList = ({ onSelectStock }) => {
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadStocks();
  }, []);

  const loadStocks = async () => {
    try {
      setLoading(true);
      const response = await stockApi.getAllStocks();
      setStocks(response.data);
      setError(null);
    } catch (err) {
      setError('주식 목록을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="stock-list">
      <h2>주식 목록</h2>
      {stocks.map(stock => (
        <div
          key={stock.id}
          className="stock-item"
          onClick={() => onSelectStock(stock)}
        >
          <div className="stock-code">{stock.stockCode}</div>
          <div className="stock-name">{stock.stockName}</div>
          <div className="stock-info">
            {stock.market} | {stock.sector}
          </div>
        </div>
      ))}
    </div>
  );
};

export default StockList;
```

### Custom Hooks

```javascript
// hooks/useStocks.js
import { useState, useEffect } from 'react';
import { stockApi } from '../services/api';

export const useStocks = () => {
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadStocks = async () => {
    try {
      setLoading(true);
      const response = await stockApi.getAllStocks();
      setStocks(response.data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStocks();
  }, []);

  return { stocks, loading, error, reload: loadStocks };
};
```

---

## 데이터베이스 설계

### ERD

```
stocks                  stock_prices              predictions
┌──────────────┐       ┌──────────────┐         ┌──────────────┐
│ id (PK)      │───┐   │ id (PK)      │         │ id (PK)      │
│ stock_code   │   │   │ stock_id(FK) │         │ stock_id(FK) │
│ stock_name   │   └──→│ trade_date   │         │ target_date  │
│ market       │       │ open_price   │         │ predicted_   │
│ sector       │       │ close_price  │         │  price       │
│ created_at   │       │ volume       │         │ confidence_  │
│ updated_at   │       │ created_at   │         │  level       │
└──────────────┘       └──────────────┘         └──────────────┘
```

### 마이그레이션 관리

새로운 테이블이나 컬럼 추가 시:

```sql
-- migrations/V002__add_user_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- migrations/V003__add_stock_favorite.sql
ALTER TABLE stocks ADD COLUMN is_favorite BOOLEAN DEFAULT FALSE;
CREATE INDEX idx_stocks_favorite ON stocks(is_favorite);
```

### 인덱스 최적화

```sql
-- 자주 조회되는 컬럼에 인덱스 추가
CREATE INDEX idx_stock_prices_trade_date ON stock_prices(trade_date);
CREATE INDEX idx_predictions_target_date ON predictions(target_date);
CREATE INDEX idx_predictions_stock_target ON predictions(stock_id, target_date);

-- 복합 인덱스
CREATE INDEX idx_stock_prices_stock_date
    ON stock_prices(stock_id, trade_date DESC);
```

---

## 테스트 작성

### 단위 테스트 (Service)

```java
@SpringBootTest
class StockServiceTest {

    @MockBean
    private StockMapper stockMapper;

    @Autowired
    private StockService stockService;

    @Test
    void getAllStocks_ShouldReturnStockList() {
        // Given
        List<Stock> mockStocks = Arrays.asList(
            Stock.builder().id(1L).stockCode("005930").build(),
            Stock.builder().id(2L).stockCode("000660").build()
        );
        when(stockMapper.findAll()).thenReturn(mockStocks);

        // When
        List<StockDTO> result = stockService.getAllStocks();

        // Then
        assertEquals(2, result.size());
        verify(stockMapper, times(1)).findAll();
    }
}
```

### 통합 테스트 (Controller)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllStocks_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getStockByCode_WithValidCode_ShouldReturnStock() throws Exception {
        mockMvc.perform(get("/stocks/005930"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockCode").value("005930"));
    }
}
```

---

## 배포 가이드

### JAR 배포

```bash
# 빌드
mvn clean package

# 실행
java -jar target/stock-prediction-system.war
```

### WAR 배포 (JBoss/WildFly)

```bash
# WAR 생성
mvn clean package

# JBoss 배포
cp target/stock-prediction-system.war $JBOSS_HOME/standalone/deployments/

# JBoss 시작
$JBOSS_HOME/bin/standalone.sh
```

### Docker 배포

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
```

```bash
# 빌드
docker build -t stock-prediction:latest .

# 실행
docker run -p 8080:8080 stock-prediction:latest
```

---

## 트러블슈팅

### MyBatis 관련

**문제: Mapper를 찾을 수 없음**
```
org.apache.ibatis.binding.BindingException: Invalid bound statement
```

**해결:**
1. XML namespace와 Mapper 인터페이스 경로 확인
2. mapper-locations 설정 확인
3. @MapperScan 어노테이션 확인

**문제: ResultMap 매핑 실패**
```
Error setting property values
```

**해결:**
1. 컬럼명과 프로퍼티명 매칭 확인
2. map-underscore-to-camel-case 설정 확인
3. ResultMap의 property/column 확인

### 성능 최적화

**N+1 문제 해결**
```xml
<!-- Association을 사용한 JOIN -->
<select id="findAllWithStock" resultMap="predictionWithStockMap">
    SELECT p.*, s.*
    FROM predictions p
    LEFT JOIN stocks s ON p.stock_id = s.id
</select>
```

**배치 INSERT**
```xml
<insert id="insertBatch">
    INSERT INTO stock_prices (stock_id, trade_date, close_price)
    VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.stockId}, #{item.tradeDate}, #{item.closePrice})
    </foreach>
</insert>
```

---

## 유용한 명령어

```bash
# 백엔드 빌드 및 실행
cd backend
mvn clean install
mvn spring-boot:run

# 프론트엔드 실행
cd frontend
npm install
npm start

# 테스트 실행
mvn test
npm test

# 코드 품질 검사
mvn checkstyle:check
npm run lint

# 프로덕션 빌드
mvn clean package
npm run build
```

---

## 참고 자료

- [Spring Boot 문서](https://spring.io/projects/spring-boot)
- [MyBatis 문서](https://mybatis.org/mybatis-3/)
- [React 문서](https://react.dev)
- [PostgreSQL 문서](https://www.postgresql.org/docs/)

---

**작성일**: 2025-11-28
**버전**: 1.0.0
**작성자**: Claude Code (바이브코딩)
