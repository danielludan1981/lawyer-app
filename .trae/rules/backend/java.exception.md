# 异常处理

## Exception（异常类）

### 定义
Exception（异常类）用于表示程序运行过程中发生的异常情况，用于错误处理和异常传递。

### 分类
- **系统异常**：表示系统级别的错误，如数据库连接失败、文件IO错误等
- **业务异常**：表示业务逻辑上的错误，如参数校验失败、业务规则违反等

### 异常编码(errorCode)

#### 编码字符
- 只能包含数字和大写的英文字母

#### 编码结构
<异常分类><错误码>
- 异常大类，两个字符，包括
    - 01： 系统异常
    - 02： 业务异常
- 错误码，四个字符，包括
    - 系统异常大类下
        - 0001： 网络异常
        - 0002： 文件异常
        - 0003： 配置异常
        - 0004： 数据库异常
        - 0000： 其他系统异常
    - 业务异常大类下
        - 0001： 请求参数异常
        - 0002： 更新资源版本冲突异常
        - 0003： 请求超限异常
        - 0004： 请求超时异常
        - 0005： 请求资源不存在异常
        - 0006： 身份验证失败异常
        - 0007： 无授权异常
        - 0000： 其他业务异常

### 命名规范
- 系统异常：`SystemException`
- 业务异常：`BusinessException`

### 设计原则
- 继承自`RuntimeException`（非检查异常），避免强制异常处理
- 包含错误码和错误信息
- 可包含详细的错误详情
- 提供多种构造方法，方便使用

### 使用场景
- Service层业务逻辑校验失败
- 系统资源访问失败
- 参数校验失败
- 权限验证失败

### 示例

```java
// 基础异常类
public abstract class AbstractBaseException extends RuntimeException {
    private String errorCode;
    private Object details;

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BaseException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public BaseException(String errorCode, String message, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    // getter和setter方法
}

// 业务异常类
public class BusinessException extends AbstractBaseException {
    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public BusinessException(String errorCode, String message, Object details) {
        super(errorCode, message, details);
    }

    public BusinessException(String errorCode, String message, Object details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}

// 系统异常类
public class SystemException extends AbstractBaseException {
    public SystemException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SystemException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public SystemException(String errorCode, String message, Object details) {
        super(errorCode, message, details);
    }

    public SystemException(String errorCode, String message, Object details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}

// 特定业务异常示例
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", String.format("用户ID %d 不存在", userId), Map.of("userId", userId));
    }

    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND", String.format("邮箱 %s 对应的用户不存在", email), Map.of("email", email));
    }
}
```

### 异常包装和处理

- 其他检查型异常(继承自Exception)：包装为对应自定义系统异常或业务异常抛出。包装时，需要记录日志并包含原始异常信息。
- 其他运行时异常(继承自RuntimeException)：包装为对应自定义系统异常或业务异常抛出
- 自定义包装异常统一处理，包括记录日志、异常信息封装、返回给客户端

以下为异常处理示范：
```java
// 正确示范
try {
    // 可能抛出异常的代码
} catch (SpecificException e) {
    // 记录日志
    log.error("处理失败: {}", e.getMessage(), e);
    // 转换为包装异常，以下为业务异常示例
    throw new BusinessException("处理失败", e);
}

// 错误示范
try {
    // 可能抛出异常的代码
} catch (SpecificException e) {
    // 空catch块
}
```
