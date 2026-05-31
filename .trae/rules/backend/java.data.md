# Java数据模型定义规范

## 概述

本文档定义了Java项目中通用数据模型的抽象定义和最佳实践，包括DTO（数据传输对象）、PO（Persistent Object，持久化对象）数据模型。

## DTO（数据传输对象）

### 定义
DTO（Data Transfer Object）是用于在不同层之间传输数据的对象，主要用于封装数据，不包含业务逻辑。

### 命名规范
- 基本命名：`XXXDTO`

### 设计原则
- 只包含必要的字段，避免传输冗余数据
- 字段类型应与业务需求匹配，避免过度包装
- 提供完整的getter和setter方法
- 实现`Serializable`接口（如果需要序列化）
- 可包含简单的校验逻辑（如`@NotNull`、`@Size`等注解）

### 使用场景
- Controller层接收请求参数
- Service层与Controller层之间的数据传输
- 远程服务调用（REST、RPC）的数据传输

### 示例

```java
// 基本DTO示例
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    // getter和setter方法
}

// 请求DTO示例
public class CreateUserRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String name;

    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    // getter和setter方法
}

// 响应DTO示例
public class ApiResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private T data;
    private PaginationDTO pagination;
    private ErrorDTO error;
    private LocalDateTime timestamp;

    // 构造方法和静态工厂方法
    private ApiResponseDTO() {
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDTO<T> success(T data, PaginationDTO pagination) {
        ApiResponseDTO<T> response = success(data);
        response.setPagination(pagination);
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String code, String message, Object details) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setError(new ErrorDTO(code, message, details));
        return response;
    }

    // getter和setter方法
}

// 查询DTO示例
public class UserQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String phone;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // getter和setter方法
}
```

## PO（持久化对象）

### 定义
PO（Persistent Object）是与数据库表结构对应的对象，用于表示业务领域中的核心概念。

### 命名规范
- 基本命名：`XXXPO`
- 示例：`UserPO`、`OrderPO`、`ProductPO`

### 设计原则
- 与数据库表结构一一对应
- 包含主键字段
- 使用JPA/Hibernate等ORM框架的注解进行映射
- 可包含简单的业务方法（如状态转换）
- 实现`Serializable`接口
- 提供合理的`equals()`和`hashCode()`方法

### 使用场景
- 数据库持久化操作
- Service层业务逻辑处理
- 与数据库表直接对应的领域对象

### 示例

```java
@Entity
@Table(name = "t_user")
public class UserPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // getter和setter方法

    // 简单业务方法示例
    public void activate() {
        this.status = UserStatusEnum.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatusEnum.INACTIVE;
    }

    // equals()和hashCode()方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPO user = (UserPO) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// 枚举类示例
public enum UserStatusEnum {
    ACTIVE,    // 激活
    INACTIVE,  // 未激活
    LOCKED,    // 锁定
    DELETED    // 删除
}
```



##  数据转换

### 转换原则
- **转换职责**：Service层负责PO和DTO之间的转换
- **转换工具**：可使用MapStruct、ModelMapper等工具进行转换
- **手动转换**：对于简单转换，也可使用手动转换方式

### 转换示例

```java
// 手动转换示例
public class UserConverter {
    public static UserDTO toDTO(UserPO user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

    public static List<UserDTO> toDTOList(List<UserPO> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(UserConverter::toDTO)
                .collect(Collectors.toList());
    }

    public static UserPO toEntity(CreateUserRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        UserPO user = new UserPO();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setStatus(UserStatusEnum.ACTIVE);

        return user;
    }
}

// MapStruct转换示例
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(UserPO user);

    List<UserDTO> toDTOList(List<UserPO> users);

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "password", ignore = true)
    UserPO toEntity(CreateUserRequestDTO requestDTO);

    void updateEntity(@MappingTarget UserPO user, UpdateUserRequestDTO requestDTO);
}
```

##  最佳实践总结

1. **明确职责**：DTO负责数据传输，PO负责数据持久化，Exception负责错误处理
2. **合理命名**：遵循命名规范，使类的用途一目了然
3. **避免混用**：Controller层只使用DTO，Service层可使用PO和DTO，Dao层只使用PO
4. **数据转换**：统一在Service层进行PO和DTO之间的转换
6. **序列化**：需要在网络中传输或持久化的对象应实现Serializable接口
7. **工具使用**：合理使用ORM框架和转换工具，提高开发效率和代码质量