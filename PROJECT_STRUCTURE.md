# 프로젝트 구조 상세 가이드

## 전체 디렉토리 구조

```
stock-prediction-system/
│
├── backend/                                    # Spring Boot + MyBatis 백엔드
│   ├── src/
│   │   └── main/
│   │       ├── java/com/stock/
│   │       │   │
│   │       │   ├── StockPredictionApplication.java    # 메인 애플리케이션 클래스
│   │       │   │                                       # @SpringBootApplication
│   │       │   │                                       # @MapperScan("com.stock.mapper")
│   │       │   │
│   │       │   ├── controller/                         # REST API 컨트롤러
│   │       │   │   ├── StockController.java           # GET /api/stocks
│   │       │   │   └── PredictionController.java      # GET /api/predictions
│   │       │   │
│   │       │   ├── service/                            # 비즈니스 로직
│   │       │   │   ├── StockService.java              # 주식 조회 서비스
│   │       │   │   └── PredictionService.java         # 예측 생성/조회 서비스
│   │       │   │
│   │       │   ├── mapper/                             # MyBatis Mapper 인터페이스
│   │       │   │   ├── StockMapper.java               # @Mapper
│   │       │   │   ├── StockPriceMapper.java          # @Mapper
│   │       │   │   └── PredictionMapper.java          # @Mapper
│   │       │   │
│   │       │   ├── model/                              # 도메인 모델 (POJO)
│   │       │   │   ├── Stock.java                     # 주식 정보
│   │       │   │   ├── StockPrice.java                # 주가 데이터
│   │       │   │   └── Prediction.java                # 예측 정보
│   │       │   │
│   │       │   ├── dto/                                # 데이터 전송 객체
│   │       │   │   ├── StockDTO.java
│   │       │   │   └── PredictionDTO.java
│   │       │   │
│   │       │   └── config/                             # 설정 클래스
│   │       │       └── WebConfig.java                 # CORS 설정
│   │       │
│   │       └── resources/
│   │           ├── application.yml                     # Spring Boot + MyBatis 설정
│   │           └── mapper/                             # MyBatis Mapper XML
│   │               ├── StockMapper.xml                # SQL + ResultMap
│   │               ├── StockPriceMapper.xml           # SQL + ResultMap
│   │               └── PredictionMapper.xml           # SQL + ResultMap + Association
│   │
│   └── pom.xml                                         # Maven 의존성 관리
│                                                       # - Spring Boot 3.2.0
│                                                       # - MyBatis 3.0.3
│                                                       # - PostgreSQL Driver
│
├── frontend/                                           # React 프론트엔드
│   ├── public/
│   │   └── index.html                                 # HTML 템플릿
│   │
│   ├── src/
│   │   ├── components/                                # React 컴포넌트
│   │   │   ├── StockList.js                          # 주식 목록 컴포넌트
│   │   │   └── PredictionCard.js                     # 예측 카드 컴포넌트
│   │   │
│   │   ├── services/
│   │   │   └── api.js                                # Axios API 통신
│   │   │
│   │   ├── App.js                                    # 메인 애플리케이션
│   │   ├── App.css                                   # 스타일시트
│   │   └── index.js                                  # React 진입점
│   │
│   └── package.json                                   # npm 의존성
│                                                      # - React 18
│                                                      # - Axios
│
├── database/
│   └── schema.sql                                     # PostgreSQL 스키마
│                                                      # - CREATE DATABASE
│                                                      # - CREATE TABLE
│                                                      # - INSERT 샘플 데이터
│
├── README.md                                          # 프로젝트 상세 문서
├── QUICKSTART.md                                      # 빠른 시작 가이드
├── EXECUTION_GUIDE.md                                 # 실행 가이드
├── CLAUDE.md                                          # 바이브코딩 가이드
├── PROJECT_STRUCTURE.md                               # 이 문서
└── .gitignore                                         # Git 제외 파일
```

## 핵심 파일 설명

### 1. Backend - StockPredictionApplication.java
```java
@SpringBootApplication
@MapperScan("com.stock.mapper")  // MyBatis Mapper 스캔
public class StockPredictionApplication extends SpringBootServletInitializer {
    // JBoss/WildFly 배포 지원
}
```

### 2. Backend - application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_prediction
    username: postgres
    password: postgres

mybatis:
  type-aliases-package: com.stock.model
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true  # snake_case → camelCase
```

### 3. MyBatis Mapper Interface Example
```java
@Mapper
public interface StockMapper {
    List<Stock> findAll();
    Optional<Stock> findById(@Param("id") Long id);
    int insert(Stock stock);
}
```

### 4. MyBatis Mapper XML Example
```xml
<mapper namespace="com.stock.mapper.StockMapper">
    <resultMap id="stockResultMap" type="com.stock.model.Stock">
        <id property="id" column="id"/>
        <result property="stockCode" column="stock_code"/>
        <result property="stockName" column="stock_name"/>
    </resultMap>

    <select id="findAll" resultMap="stockResultMap">
        SELECT * FROM stocks
    </select>
</mapper>
```

### 5. Service Layer
```java
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockMapper stockMapper;  // MyBatis Mapper 주입

    public List<StockDTO> getAllStocks() {
        return stockMapper.findAll()...
    }
}
```

### 6. Frontend - API Service
```javascript
const API_BASE_URL = 'http://localhost:8080/api';

export const stockApi = {
  getAllStocks: () => api.get('/stocks'),
  getStockByCode: (code) => api.get(`/stocks/${code}`)
};
```

## 데이터 흐름

### 조회 흐름
```
Browser → React Component → Axios API
    ↓
Spring Controller → Service → MyBatis Mapper
    ↓
Mapper XML → PostgreSQL
    ↓
ResultMap → Model Object → DTO
    ↓
JSON Response → React State → UI Render
```

### MyBatis 쿼리 흐름
```
1. Service에서 Mapper 메서드 호출
   stockMapper.findAll()

2. MyBatis가 해당 XML 찾기
   <select id="findAll" ...>

3. SQL 실행
   SELECT * FROM stocks

4. ResultMap으로 매핑
   <resultMap id="stockResultMap" ...>

5. List<Stock> 객체 반환
```

## 주요 API 엔드포인트

### 백엔드 API
| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/stocks | 전체 주식 목록 |
| GET | /api/stocks/{code} | 특정 주식 조회 |
| GET | /api/predictions/tomorrow | 내일 예측 전체 |
| GET | /api/predictions/stock/{code} | 특정 주식 예측 |
| POST | /api/predictions | 예측 생성 |

### 프론트엔드 라우팅
- http://localhost:3000 - 메인 페이지

## 데이터베이스 테이블

### stocks (주식 정보)
```sql
CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    stock_code VARCHAR(20) UNIQUE,
    stock_name VARCHAR(100),
    market VARCHAR(20),
    sector VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### stock_prices (주가 데이터)
```sql
CREATE TABLE stock_prices (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT REFERENCES stocks(id),
    trade_date DATE,
    open_price DECIMAL(10, 2),
    high_price DECIMAL(10, 2),
    low_price DECIMAL(10, 2),
    close_price DECIMAL(10, 2),
    volume BIGINT,
    trading_value BIGINT,
    created_at TIMESTAMP
);
```

### predictions (예측)
```sql
CREATE TABLE predictions (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT REFERENCES stocks(id),
    prediction_date DATE,
    target_date DATE,
    predicted_price DECIMAL(10, 2),
    confidence_level DECIMAL(5, 2),
    prediction_type VARCHAR(20),
    model_version VARCHAR(50),
    created_at TIMESTAMP
);
```

## MyBatis vs JPA 비교

### MyBatis (현재 프로젝트)
```java
// Mapper Interface
List<Stock> findAll();

// Mapper XML
<select id="findAll" resultMap="stockResultMap">
    SELECT * FROM stocks ORDER BY stock_code
</select>
```

**장점:**
- SQL을 직접 작성하여 최적화 가능
- 복잡한 JOIN 쿼리 자유롭게 작성
- XML에서 SQL 관리 (Java 코드와 분리)

### JPA (이전 버전)
```java
@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

// Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByStockCode(String stockCode);
}
```

**장점:**
- 객체 중심 설계
- SQL 자동 생성
- 영속성 컨텍스트

## 개발 시 주의사항

### MyBatis 개발 팁
1. **Mapper XML 위치**: `resources/mapper/*.xml`
2. **namespace**: 패키지명 포함 전체 경로
3. **ResultMap**: snake_case와 camelCase 매핑
4. **@Param**: 파라미터가 2개 이상일 때 필수

### 일반적인 실수
1. ❌ Mapper XML 경로 오류
   - 해결: `application.yml`의 `mapper-locations` 확인

2. ❌ @MapperScan 누락
   - 해결: Application 클래스에 추가

3. ❌ ResultMap ID 불일치
   - 해결: XML의 resultMap id와 select의 resultMap 일치

4. ❌ 파라미터 바인딩 오류
   - 해결: `@Param` 어노테이션 사용

## 확장 아이디어

### 1. 동적 SQL 추가
```xml
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
```

### 2. 페이징 추가
```java
List<Stock> findByPage(@Param("offset") int offset,
                       @Param("limit") int limit);
```

### 3. 배치 처리
```xml
<insert id="insertBatch" parameterType="java.util.List">
    INSERT INTO stocks (stock_code, stock_name, market)
    VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.stockCode}, #{item.stockName}, #{item.market})
    </foreach>
</insert>
```

## 참고 자료

- [MyBatis 공식 문서](https://mybatis.org/mybatis-3/)
- [Spring Boot 문서](https://spring.io/projects/spring-boot)
- [React 문서](https://react.dev)
- [PostgreSQL 문서](https://www.postgresql.org/docs/)

---

**프로젝트 구조에 대한 질문이 있다면 Claude Code에게 물어보세요!**
