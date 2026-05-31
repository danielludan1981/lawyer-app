# API REST规范

## 设计原则

### 核心原则
- **Contract First**: 先定义API契约，再实现功能
- **资源导向**: 以资源为中心设计URL
- **无状态**: 每个请求包含处理所需的所有信息
- **统一接口**: 使用统一的接口约定和标准HTTP方法
- **可缓存**: 响应数据应明确标识是否可缓存

### RESTful约束
- 使用标准HTTP方法（GET, POST, PUT, PATCH, DELETE）
- 使用HTTP状态码表示操作结果
- 使用JSON作为数据交换格式
- 使用有意义的资源URL而非动作URL

## URL设计规范

### 基本格式
```
https://api.example.com/v1/resource
```

### 命名规范
- 使用小写字母
- 使用驼峰命名法（camelCase）
- 使用复数形式表示资源集合
- 使用名词而非动词

### URL示例
```
# 良好示例
GET    /users              # 获取用户列表
GET    /users/{id}         # 获取特定用户
POST   /users              # 创建用户
PUT    /users/{id}         # 完整更新用户
PATCH  /users/{id}         # 部分更新用户
DELETE /users/{id}         # 删除用户

# 嵌套资源
GET    /users/{id}/orders  # 获取用户的订单列表
POST   /users/{id}/orders  # 为用户创建订单
```


## HTTP方法使用规范

| 方法 | 用途 | 幂等性 | 安全性 |
|------|------|--------|--------|
| GET | 获取资源 | 是 | 是 |
| POST | 创建资源 | 否 | 否 |
| PUT | 完整替换资源 | 是 | 否 |
| PATCH | 部分更新资源 | 否 | 否 |
| DELETE | 删除资源 | 是 | 否 |

## HTTP状态码规范

为了使得客户端能够统一处理所有响应，本系统所有请求（包括成功和失败）都返回HTTP `200 OK`状态码。具体的响应状态信息通过响应体中的以下方式表示：

- **成功响应**：通过`success=true`标识，可选择在最外层增加`code`字段表示额外的成功状态（如201表示资源创建成功，204表示无返回内容等）
- **失败响应**：通过`success=false`标识，并在`error`字段中包含详细的错误信息

## 请求响应格式规范

### 请求头
```
Content-Type: application/json
Accept: application/json
Authorization: Bearer {token}
```

### 请求体格式

#### RequestDTO<T> 统一请求结构
为了保持请求格式的一致性，所有POST/PUT/PATCH请求的请求体应使用统一的`RequestDTO<T>`结构：

```json
{
  "data": {
    // 实际业务数据，类型为泛型T
  },
  "metadata": {
    // 元数据信息，类型为Map<String, Object>
  }
}
```

#### 字段说明
- **data**: 实际业务数据，类型为泛型T，用于传递核心业务信息
- **metadata**: 元数据信息，类型为`Map<String, Object>`，用于传递与业务数据相关的辅助信息（可选）

#### metadata 标准化key建议
为了提高系统间的互操作性，建议使用以下标准化的metadata key：
- `timestamp`: 请求时间戳
- `source`: 请求来源系统
- `traceId`: 分布式追踪ID
- `userId`: 操作用户ID
- `clientIp`: 客户端IP地址
- `locale`: 语言环境

#### 设计合理性分析
1. **与响应格式保持一致**: 与现有`ResponseDTO<T>`结构保持对称，统一前后端交互模式
2. **分离业务与元数据**: 清晰区分核心业务数据和辅助信息，提高代码可读性和维护性
3. **良好的扩展性**: 泛型设计支持各种业务数据类型，metadata可灵活扩展
4. **支持复杂场景**: 适用于需要传递额外上下文信息的跨系统集成场景
5. **便于审计和监控**: 统一的元数据结构便于实现日志审计和系统监控

#### 使用建议
1. **metadata 可选性**: 对于简单请求，metadata字段可以省略
2. **类型安全**: 在后端实现中应使用泛型确保类型安全
3. **验证支持**: 对data和metadata中的字段可添加适当的验证注解
4. **集合支持**: 支持`RequestDTO<List<T>>`格式处理批量请求

### 成功响应格式
```json
{
  "success": true,
  "code": 200,  // 可选，用于表示额外的成功状态码（如201、204等）
  "data": {
    // 实际数据
  },
  "pagination": {  // 仅在列表接口中包含
    "page": 1,
    "pageSize": 20,
    "total": 100,
    "totalPages": 5
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### 带可选状态码的成功响应示例
```json
{
  "success": true,
  "code": 201,  // 表示资源创建成功
  "data": {
    "id": 1,
    "name": "新资源"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 错误响应格式
```json
{
  "success": false,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "用户不存在",
    "details": {
      "field": "userId",
      "value": "123"
    }
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 验证错误响应格式
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "请求参数验证失败",
    "details": [
      {
        "field": "email",
        "message": "邮箱格式不正确",
        "rejectedValue": "invalid-email"
      },
      {
        "field": "age",
        "message": "年龄必须大于18岁",
        "rejectedValue": 16
      }
    ]
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 分页规范

### 分页参数
- `page`: 页码，从1开始，默认1
- `pageSize`: 每页数量，默认20，最大100

### 分页响应
```json
{
  "success": true,
  "code": 200,  // 可选，用于表示额外的成功状态码
  "data": [...],
  "pagination": {
    "page": 2,
    "pageSize": 20,
    "total": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrev": true
  }
}
```

## 排序和过滤规范

### 排序参数
```
sort=field:direction
```
- `field`: 排序字段
- `direction`: `asc`(升序)或`desc`(降序)

### 多字段排序
```
sort=createdAt:desc,name:asc
```

### 过滤参数

#### 基本格式
```
filter[field]=operator:value
```

#### 支持的操作符

| 操作符 | 说明 | 示例 |
|--------|------|------|
| `eq` | 等于 (默认) | `filter[name]=eq:John` 或简化为 `filter[name]=John` |
| `ne` | 不等于 | `filter[status]=ne:inactive` |
| `gt` | 大于 | `filter[age]=gt:18` |
| `gte` | 大于等于 | `filter[age]=gte:18` |
| `lt` | 小于 | `filter[salary]=lt:5000` |
| `lte` | 小于等于 | `filter[salary]=lte:10000` |
| `in` | 在集合中 | `filter[id]=in:1,2,3,4,5` |
| `nin` | 不在集合中 | `filter[status]=nin:active,pending` |
| `like` | 模糊匹配 (SQL LIKE) | `filter[name]=like:J%` |
| `not_like` | 不匹配模糊查询 | `filter[name]=not_like:%admin%` |
| `isNull` | 为空 | `filter[email]=isNull:true` |
| `notNull` | 不为空 | `filter[email]=notNull:true` |
| `between` | 在范围内 | `filter[createdAt]=between:2023-01-01,2023-12-31` |

#### 复杂查询示例
```
GET /users?sort=createdAt:desc&filter[status]=active&filter[age]=gte:18&filter[salary]=lt:50000&filter[name]=like:J%&filter[id]=in:1,2,3,4,5
```

#### 前端实现建议

##### 构建查询参数
在JavaScript中，可以使用以下方式构建复杂查询参数：

```javascript
// 构建过滤参数对象
const filters = {
  status: 'active',
  age: 'gte:18',
  salary: 'lt:50000',
  name: 'like:J%',
  id: 'in:1,2,3,4,5'
};

// 将过滤参数转换为URL查询字符串
const buildQueryParams = (filters, sort) => {
  const params = new URLSearchParams();

  // 处理排序参数
  if (sort) {
    params.append('sort', sort);
  }

  // 处理过滤参数
  Object.entries(filters).forEach(([field, value]) => {
    params.append(`filter[${field}]`, value);
  });

  return params.toString();
};

// 生成完整的API URL
const apiUrl = `/users?${buildQueryParams(filters, 'createdAt:desc')}`;
// 结果: /users?sort=createdAt:desc&filter[status]=active&filter[age]=gte:18&filter[salary]=lt:50000&filter[name]=like:J%&filter[id]=in:1,2,3,4,5
```

##### 使用RequestDTO发送POST请求

```javascript
// 构建RequestDTO对象
const createUserRequest = {
  data: {
    name: '张三',
    email: 'zhangsan@example.com',
    age: 30,
    status: 'active'
  },
  metadata: {
    timestamp: new Date().toISOString(),
    source: 'web-portal',
    traceId: 'uuid-1234567890',
    userId: 'current-user-id'
  }
};

// 发送POST请求
fetch('/api/v1/users', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer your-token-here'
  },
  body: JSON.stringify(createUserRequest)
})
.then(response => response.json())
.then(data => {
  console.log('创建用户成功:', data);
})
.catch(error => {
  console.error('创建用户失败:', error);
});

// 简化版：不带metadata的请求
const simpleCreateRequest = {
  data: {
    name: '李四',
    email: 'lisi@example.com',
    age: 25,
    status: 'active'
  }
};
```

##### RequestDTO构建工具函数

```javascript
// 构建RequestDTO的工具函数
const buildRequestDTO = (data, metadata = null) => {
  const request = { data };

  if (metadata) {
    request.metadata = metadata;
  }

  return request;
};

// 使用工具函数创建请求
const userData = {
  name: '王五',
  email: 'wangwu@example.com'
};

const userMetadata = {
  traceId: 'uuid-0987654321',
  locale: 'zh-CN'
};

const request = buildRequestDTO(userData, userMetadata);
```

#### 后端Spring Boot实现建议

在Spring Boot中，可以使用自定义参数解析器或第三方库（如Spring Data JPA Specifications或Querydsl）来处理复杂查询参数，并确保请求响应格式与REST规范完全对应：

```java
// 1. 创建统一请求DTO
public class RequestDTO<T> {
    private T data;
    private Map<String, Object> metadata;

    // 构造方法
    private RequestDTO() {
    }

    // 静态工厂方法
    public static <T> RequestDTO<T> of(T data) {
        RequestDTO<T> request = new RequestDTO<>();
        request.setData(data);
        return request;
    }

    public static <T> RequestDTO<T> of(T data, Map<String, Object> metadata) {
        RequestDTO<T> request = of(data);
        request.setMetadata(metadata);
        return request;
    }

    // 链式调用方法
    public RequestDTO<T> withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public RequestDTO<T> addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    // getter和setter
    // ...
}

// 2. 创建统一响应DTO
public class ResponseDTO<T> {
    private boolean success;
    private Integer code;  // 可选，用于表示额外的成功状态码（如201、204等）
    private T data;
    private PaginationDTO pagination;
    private ErrorDTO error;
    private LocalDateTime timestamp;

    // 构造方法
    private ResponseDTO() {
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
    }

    public static <T> ResponseDTO<T> success(T data) {
        return success(data, null, null);
    }

    public static <T> ResponseDTO<T> success(T data, Integer code) {
        return success(data, code, null);
    }

    public static <T> ResponseDTO<T> success(T data, PaginationDTO pagination) {
        return success(data, null, pagination);
    }

    public static <T> ResponseDTO<T> success(T data, Integer code, PaginationDTO pagination) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setSuccess(true);
        response.setData(data);
        response.setCode(code);
        response.setPagination(pagination);
        return response;
    }

    public static <T> ResponseDTO<T> error(String code, String message, Object details) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setSuccess(false);
        response.setError(new ErrorDTO(code, message, details));
        return response;
    }

    // getter和setter
}

public class PaginationDTO {
    private int page;
    private int pageSize;
    private long total;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrev;

    // 构造方法、getter和setter
    public PaginationDTO(Page<?> page) {
        this.page = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.total = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrev = page.hasPrevious();
    }
}

public class ErrorDTO {
    private String code;
    private String message;
    private Object details;

    // 构造方法、getter和setter
    public ErrorDTO(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}

// 2. 创建用户DTO（用于Controller和Service层之间的数据传输）
public class UserDTO {
    private Long id;
    private String name;
    private String status;
    private Integer age;
    private Double salary;
    // getter和setter
}

// 3. 创建查询条件DTO
public class UserQueryDTO {
    private String status;
    private String age;
    private String salary;
    private String name;
    private String id;
    // getter和setter
}

// 4. 创建用户服务接口（Service层）
public interface UserService {
    Page<UserDTO> getUsers(UserQueryDTO queryDTO, int page, int pageSize, String sort);
}

// 5. 创建用户服务实现类（Service层）
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserDTO> getUsers(UserQueryDTO queryDTO, int page, int pageSize, String sort) {
        // 将查询参数转换为查询条件
        Specification<UserPO> spec = buildSpecification(queryDTO);

        // 构建分页请求
        Pageable pageable = PageRequest.of(page - 1, pageSize, buildSort(sort));

        // 执行查询
        Page<UserPO> userPage = userRepository.findAll(spec, pageable);

        // Entity 转换为 DTO
        return userPage.map(this::convertToDTO);
    }

    private UserDTO convertToDTO(UserPO user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setStatus(user.getStatus());
        dto.setAge(user.getAge());
        dto.setSalary(user.getSalary());
        // 其他字段映射
        return dto;
    }

    private Specification<UserPO> buildSpecification(UserQueryDTO queryDTO) {
        return Specification.where(equalsFilter("status", queryDTO.getStatus()))
                           .and(rangeFilter("age", queryDTO.getAge()))
                           .and(rangeFilter("salary", queryDTO.getSalary()))
                           .and(likeFilter("name", queryDTO.getName()))
                           .and(inFilter("id", queryDTO.getId()));
    }

    private Specification<UserPO> equalsFilter(String field, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // 解析操作符和值
        String[] parts = value.split(":", 2);
        String operator = parts.length > 1 ? parts[0] : "eq";
        String actualValue = parts.length > 1 ? parts[1] : parts[0];

        if ("eq".equals(operator)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), actualValue);
        } else if ("ne".equals(operator)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), actualValue);
        }

        return null;
    }

    private Specification<UserPO> rangeFilter(String field, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] parts = value.split(":", 2);
        if (parts.length < 2) {
            return null;
        }

        String operator = parts[0];
        String actualValue = parts[1];

        switch (operator) {
            case "gt":
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(field), parseNumber(field, actualValue));
            case "gte":
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(field), parseNumber(field, actualValue));
            case "lt":
                return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(field), parseNumber(field, actualValue));
            case "lte":
                return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(field), parseNumber(field, actualValue));
            case "between":
                String[] rangeValues = actualValue.split(",", 2);
                if (rangeValues.length < 2) {
                    return null;
                }
                return (root, query, criteriaBuilder) -> criteriaBuilder.between(
                    root.get(field),
                    parseValue(field, rangeValues[0]),
                    parseValue(field, rangeValues[1])
                );
            default:
                return null;
        }
    }

    private Specification<UserPO> likeFilter(String field, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] parts = value.split(":", 2);
        if (parts.length < 2) {
            return null;
        }

        String operator = parts[0];
        String actualValue = parts[1];

        if ("like".equals(operator)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), actualValue);
        } else if ("not_like".equals(operator)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notLike(root.get(field), actualValue);
        }

        return null;
    }

    private Specification<UserPO# 修改前 (java.md)
└── entity/                # repository包使用的数据对象，数据库实体

# 修改后（方案1：调整目录名）
└── po/                   # repository包使用的数据对象，数据库持久化对象

# 修改后（方案2：保持目录名，明确说明）
└── entity/                # repository包使用的数据对象，数据库持久化对象(XXXPO)> inFilter(String field, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] parts = value.split(":", 2);
        if (parts.length < 2) {
            return null;
        }

        String operator = parts[0];
        List<String> values = Arrays.asList(parts[1].split(","));

        if ("in".equals(operator)) {
            return (root, query, criteriaBuilder) -> root.get(field).in(values);
        } else if ("nin".equals(operator)) {
            return (root, query, criteriaBuilder) -> root.get(field).in(values).not();
        }

        return null;
    }

    // 辅助方法：解析值
    private Object parseValue(String field, String value) {
        // 根据字段类型解析值
        // 示例实现，实际项目中应根据具体字段类型进行解析
        try {
            if (field.equals("age") || field.equals("salary")) {
                return Integer.parseInt(value);
            } else if (field.equals("createdAt")) {
                return LocalDate.parse(value);
            }
        } catch (Exception e) {
            // 解析失败，返回原始值
        }
        return value;
    }

    private Number parseNumber(String field, String value) {
        // 根据字段类型解析数字
        try {
            if (field.equals("age")) {
                return Integer.parseInt(value);
            } else if (field.equals("salary")) {
                return Double.parseDouble(value);
            }
        } catch (Exception e) {
            // 解析失败，返回0
            return 0;
        }
        return 0;
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        String[] parts = sort.split(":");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        return Sort.by(direction, field);
    }
}

// 6. 创建用户控制器（Controller层）
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) String sort,
        @ModelAttribute UserQueryDTO queryDTO) {

        try {
            // 调用Service层获取数据
            Page<UserDTO> userPage = userService.getUsers(queryDTO, page, pageSize, sort);

            // 构建分页信息
            PaginationDTO pagination = new PaginationDTO(userPage);

            // 构建响应
            ResponseDTO<List<UserDTO>> response = ResponseDTO.success(userPage.getContent(), pagination);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 异常处理
            ResponseDTO<List<UserDTO>> response = ResponseDTO.error(
                "INTERNAL_SERVER_ERROR",
                "服务器内部错误",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<UserDTO>> createUser(@RequestBody RequestDTO<UserDTO> request) {
        try {
            // 从请求中获取业务数据
            UserDTO userDTO = request.getData();

            // 获取元数据信息
            Map<String, Object> metadata = request.getMetadata();
            String traceId = metadata != null ? (String) metadata.get("traceId") : null;

            // 调用Service层创建用户
            UserDTO createdUser = userService.createUser(userDTO, metadata);

            // 构建响应
            ResponseDTO<UserDTO> response = ResponseDTO.success(createdUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidationException e) {
            ResponseDTO<UserDTO> response = ResponseDTO.error(
                "VALIDATION_ERROR",
                "请求参数验证失败",
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ResponseDTO<UserDTO> response = ResponseDTO.error(
                "INTERNAL_SERVER_ERROR",
                "服务器内部错误",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
```

#### 最佳实践说明

通过URL参数传递复杂查询条件是可行且已被广泛采用的最佳实践，原因如下：

1. **保持HTTP GET语义统一**：所有查询操作都使用GET方法，符合RESTful设计原则
2. **可缓存性**：GET请求可以被浏览器和中间层缓存，提高性能
3. **可书签化**：查询结果可以被保存为书签或分享给他人
4. **前端实现简单**：可以使用URLSearchParams等原生API轻松构建查询参数
5. **后端支持完善**：Spring Boot提供了丰富的支持，可以通过@RequestParam、@ModelAttribute等注解自动映射参数
6. **可扩展性**：可以根据需要扩展支持更多的操作符和查询条件

对于特别复杂的查询需求，可以考虑以下补充方案：

- 使用JSON格式的查询参数：`filter=JSON.stringify(complexQuery)`
- 对于极端复杂的查询，可以考虑使用POST方法并在请求体中传递查询条件（虽然这违反了GET语义，但在实际项目中是可以接受的折衷方案）

## 版本控制规范

### 版本策略
- 使用URL路径版本控制：`/api/v1/users`
- 主版本号变更时创建新版本
- 向后兼容的更改不增加版本号

### 版本生命周期
- `v1`: 当前稳定版本
- `v2`: 开发中版本
- 旧版本至少维护6个月

## 安全规范

### 认证
- 使用Bearer Token认证
- Token格式：`Authorization: Bearer {token}`
- Token有效期：24小时

### HTTPS
- 所有API调用必须使用HTTPS
- 禁止HTTP访问

### 敏感数据处理
- 密码等敏感字段不在响应中返回
- 日志中不记录敏感信息
- 使用掩码显示部分敏感信息

## OpenAPI规范

### 基本结构
```yaml
openapi: 3.0.3
info:
  title: API名称
  description: API描述
  version: 1.0.0
  contact:
    name: API支持
    email: api@example.com
servers:
  - url: https://api.example.com/v1
    description: 生产环境
  - url: https://staging-api.example.com/v1
    description: 测试环境
```

### 通用组件
```yaml
components:
  schemas:
    ErrorResponse:
      type: object
      properties:
        success:
          type: boolean
          example: false
        error:
          $ref: '#/components/schemas/ErrorDetail'
        timestamp:
          type: string
          format: date-time
    ErrorDetail:
      type: object
      properties:
        code:
          type: string
        message:
          type: string
        details:
          type: object
    Pagination:
      type: object
      properties:
        page:
          type: integer
          minimum: 1
        pageSize:
          type: integer
          minimum: 1
          maximum: 100
        total:
          type: integer
          minimum: 0
        totalPages:
          type: integer
          minimum: 0
```

### 路径定义示例
```yaml
paths:
  /users:
    get:
      summary: 获取用户列表
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            minimum: 1
            default: 1
        - name: pageSize
          in: query
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
      responses:
        '200':
          description: 成功返回用户列表
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                    example: true
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/User'
                  pagination:
                    $ref: '#/components/schemas/Pagination'
    post:
      summary: 创建用户
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'
      responses:
        '201':
          description: 用户创建成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                    example: true
                  data:
                    $ref: '#/components/schemas/User'
        '400':
          description: 请求参数错误
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
```

## 错误代码规范

### 错误代码格式
```
{DOMAIN}_{SPECIFIC_ERROR}
```

### 常见错误代码
- `AUTHENTICATION_FAILED`: 认证失败
- `AUTHORIZATION_DENIED`: 权限不足
- `RESOURCE_NOT_FOUND`: 资源不存在
- `VALIDATION_ERROR`: 参数验证失败
- `DUPLICATE_RESOURCE`: 资源重复
- `EXTERNAL_SERVICE_ERROR`: 外部服务错误
- `RATE_LIMIT_EXCEEDED`: 请求频率超限

## 性能规范

### 响应时间
- 简单查询：≤ 200ms
- 复杂查询：≤ 500ms
- 数据修改：≤ 300ms

### 数据限制
- 列表接口默认返回20条记录
- 单次请求最大返回100条记录
- 深度分页限制最大1000页

## 监控和日志规范

### 日志内容
- 请求方法、URL、参数
- 响应状态码、响应时间
- 用户标识、客户端IP
- 错误详情（不含敏感信息）

### 监控指标
- 请求量、成功率、响应时间
- 错误率、错误分布
- 系统资源使用情况

## 测试规范

### 测试类型
- 单元测试：覆盖所有业务逻辑
- 集成测试：覆盖所有API接口
- 性能测试：关键接口性能验证

### 测试环境
- 开发环境：开发人员自测
- 测试环境：QA团队测试
- 预发布环境：生产前验证

---

**规范执行说明**：

1. 所有API设计必须先定义OpenAPI规范文档
2. 代码实现必须与API规范保持一致
3. API变更必须先更新规范文档
4. 定期进行API规范审查和优化

**生效日期**：2025年12月27日

**版本号**：1.0

**最后更新**：2025年12月27日

