# Java项目开发规范

## 项目结构规范

│ backend/                                                      # 后端根目录
│   └── {{module}}/                                             # 模块根目录，backend下面可以有多个模块
│        ├── README.md                                          # 模块说明文件
│        ├── script/                                            # 脚本目录，存放构建和部署脚本
│        └── src/
│            ├── main/
│            │   ├── java/                                       # 主代码目录
│            │   │   └── com/
│            │   │       └── {{company}}/                        # 公司名，请根据实际情况修改
│            │   │           └── {{project}}/                    # 项目名，请根据实际情况修改
│            │   │               └── {{module}}/                 # 模块名，请根据实际情况修改
│            │   │                    ├── api/                   # API层，包括提供接口的服务端和访问外部接口客户端
│            │   │                    │   ├── rest/              # REST API接口，服务端
│            │   │                    │   ├── rpc/               # RPC API接口，服务端
│            │   │                    │   └── client             # API客户端
│            │   │                    │         ├── rest/        # REST API接口，客户端
│            │   │                    │         └── rpc/         # RPC API接口，客户端
│            │   │                    ├── service/               # 服务层（服务接口放在service包，实现放在impl包）
│            │   │                    │     ├── impl/            # 服务层实现
│            │   │                    ├── dao/                   # 存储层
│            │   │                    │     ├── db/              # 关系型数据库的DAO层，主要负责数据库操作（如XXXRepository）
│            │   │                    │     ├── redis/           # redis数据库
│            │   │                    │     ├── es/              # elasticsearch数据库
│            │   │                    │     └── mongo/           # mongo数据库
│            │   │                    ├── message/               # 消息层
│            │   │                    │     └── mq/              # mq消息队列
│            │   │                    ├── config/                # 系统配置
│            │   │                    ├── scheduler/             # 定时任务
│            │   │                    ├── batch/                 # 批量处理
│            │   │                    ├── exception/             # 异常类
│            │   │                    ├── enums/                 # 枚举类
│            │   │                    ├── constant/              # 常量类
│            │   │                    ├── util/                  # 工具类
│            │   │                    ├── dto/                   # servicd和api包使用的数据对象，业务实体和与外部交互的数据结构合并
│            │   │                    └── po/                    # 持久化对象，主要为数据库实体
│            │   └── resources/                                  # 主代码资源目录
│            │        ├── db/                                    # 数据库脚本文件
│            │        │   ├── migration/                         # Flyway数据库迁移脚本目录
│            │        ├── application.yml                        # 主配置文件
│            │        ├── dev.yml                                # 开发环境配置文件
│            │        ├── int.yml                                # 继承测试环境配置文件
│            │        ├── prd.yml                                # 生产环境配置文件
│            │        ├── mapper/                                # MyBatis映射文件目录
│            │        └── static/                                # 静态资源目录
│            └── test/
│                ├── java/                                       # 测试代码目录
│                │   └── com/
│                │       └── {{company}}/
│                │           └── {{project}}/
│                │                └── {{module}}/
│                └── resources/                                  # 测试资源目录
├── docs/                                                        # 项目文档
│   ├── requirement/                                             # 需求文档
│   ├── design/                                                  # 详细设计文档
│   ├── api/                                                     # API设计文档，提供给外部的接口
│   └── architecture/                                            # 架构设计文档
├── pom.xml                                                      # 总项目pom文件（parent），所有module的pom文件都继承自这个文件
└── README.md                                                    # 总项目说明文件

## 层次调用关系规范

### 整体层次结构
```
Controller → Service → Dao/Message
```

### 各层职责和调用规则

| 层级       | 职责描述                                                                 | 允许调用的层级                | 禁止调用的层级                |
|------------|--------------------------------------------------------------------------|-------------------------------|-------------------------------|
| **Controller** | 接收和处理HTTP请求，参数校验，返回响应结果                               | Service                       | Dao、Message、其他Controller   |
| **Service**    | 实现业务逻辑，事务管理，数据转换（Entity ↔ DTO）                         | Service（同层）、Dao、Message、Api的client（远程调用） | Controller                    |
| **Dao**        | 数据持久化操作，与数据库交互                                             | 无                            | Service、Controller、Message  |
| **Message**    | 消息发送和接收，异步处理                                                 | 无                            | Service、Controller、Dao      |

### 数据转换规则
- Service层负责Entity和DTO之间的转换
- Controller层仅使用DTO，不直接操作Entity
- Dao层仅使用Entity，不直接操作DTO

## 主要技术栈

如以下技术栈没有指定版本，根据项目需求和实际情况选择最新的稳定版本
- JAVA：JDK {{jdkVersion}}
- 应用框架：Spring Boot {{springVersion}}
- 应用安全框架：Spring Security {{springSecurityVersion}}
- 应用日志框架：SLF4J {{slf4jVersion}} + Logback {{logbackVersion}}
- 应用ORM框架：Spring Data JPA {{springDataJpaVersion}}
- 应用ORM框架：Hibernate {{hibernateVersion}}
- 应用可观测性：OpenTelemetry
- 应用容器集成：spring-boot-docker-compose
- 定时任务：Quartz {{quartzVersion}}
- 批量处理: Spring Batch {{springBatchVersion}}
- 数据库版本迁移管理：Flyway {{flywayVersion}}
- API文档和交互测试：使用Swagger 3（SpringDoc OpenAPI）{{springDocOpenApiVersion}} + Knife4j {{knife4jVersion}}
- 代码生成：使用Lombok {{lombokVersion}}
- 数据库：MySQL {{mysqlVersion}} 或 PostgreSQL {{postgresqlVersion}}
- 缓存：Redis {{redisVersion}}
- 消息队列：RocketMQ {{rocketmqVersion}}
- 监控：Prometheus {{prometheusVersion}}, Grafana {{grafanaVersion}}

## 模块化设计

- 每个模块都有自己的目录结构，包括源代码、测试代码和资源文件
- 模块之间通过依赖关系进行通信，而不是直接访问其他模块的代码
- 每个模块都有自己的pom文件，用于管理依赖和构建配置
- 模块的功能边界清晰、独立、高内聚、低耦合，每个模块只负责自己的功能，不应该依赖其他模块的实现细节

## 面向接口设计

- service层：必须定义服务接口，包含业务逻辑方法

## 避免代码坏味道

- 避免使用魔法值（Magic Number）和魔法字符串（Magic String）
- 避免使用复杂的条件语句（如多个`if-else`或`switch-case`）
- 避免使用过长的方法或类
- 避免使用全局变量（Global Variable）
- 避免使用`try-catch`块捕获所有异常（Exception）
- 避免使用`System.out.println`或`System.err.println`打印调试信息
- 避免使用`Thread.sleep`或`TimeUnit`等阻塞操作
- 避免使用`System.exit`或`Runtime.getRuntime().exec`等终止应用程序的操作
- 避免相似的代码重复出现，如多个相似的方法或类，应该提取公共逻辑到单独的方法或类中

## 代码质量规范

### 编码规范

- 遵循Java语言规范
- 使用Google Java Style Guide
- 代码缩进：4个空格
- 每行代码不超过120个字符
- 常量
    - 常量名全部大写，单词之间用下划线分隔：`public static final int MAX_COUNT = 100;`
    - 代码中不要直接使用魔法值，应该使用常量代替：`if (count > MAX_COUNT) { ... }`
    - 代码中不要直接使用字符串字面量，应该使用常量代替：`if (name.equals(NAME)) { ... }`
    - 多个地方都使用的常量，应该定义在一个类中，例如：`public class Constants { public static final String NAME = "name"; }`

### 大括号规则

```java
// 正确
if (condition) {
    // code
} else {
    // code
}

// 错误
if (condition)
{
    // code
}
else
{
    // code
}
```

### 空格规则

- 操作符两侧添加空格：`int sum = a + b;`
- 逗号后添加空格：`method(a, b, c);`
- 方法参数括号内不加空格：`method(int param)`
- 控制语句关键字后加空格：`if (condition)`, `for (int i = 0; i < 10; i++)`

## 命名规范

### 包命名

- 全小写字母，使用公司域名反写：`com.company.project.module`
- 禁止使用下划线和连字符

### 类和接口命名

- 类和接口命名易懂，能够清晰表达其功能或角色，如果使用缩写必须是常用的缩写，例如：`XXXDTO`
- 使用大驼峰命名法（PascalCase）
- 接口名, 使用名词或名词短语: 符合以下分类条件时使用以下具体命名规则
    - XXXProcessor: 处理接口，当处理流程需要进行复杂逻辑或多个步骤时使用
    - XXXRepository: 数据访问接口，负责与数据库或其他存储系统交互
    - XXXSerializer: 序列化接口，负责将对象转换为字节流或字符串
    - XXXDeserializer: 反序列化接口，负责将字节流或字符串转换为对象
    - XXXFactory: 工厂接口，负责创建对象
    - XXXManager: 管理器接口，负责协调多个组件的工作
    - XXXDTO: 数据传输对象，用于在不同层之间传递数据
    - XXXPO: 数据库实体类，用于映射数据库表
    - XXXRequestDTO: 请求数据传输对象，用于接收外部请求数据
    - XXXResponseDTO: 响应数据传输对象，用于返回外部响应数据
    - XXXConverter: 转换器接口, 负责对象之间的转换
    - XXXValidator: 验证器接口, 负责验证对象的合法性
    - XXXService: 服务接口, 负责业务逻辑处理
    - XXXController: 控制器接口, 负责接收请求和返回响应
    - XXXException: 异常类, 负责自定义异常
- 标识接口，接口内没有申明任何方法，仅用于标识某种行为或状态
- 类名，和接口名保持一致，除此以外遵守以下规则
    - 抽象类以Abstract开头
    - 类继承于接口(标识类接口除外)时, 类名以Impl结尾


### 方法命名

- 方法命名易懂，能够清晰表达其功能，如果使用缩写必须是常用的缩写，例如：`findUserById()`
- 使用小驼峰命名法（camelCase）
- 方法名使用动词或动词短语：`getUserById()`, `calculateTotal()`
- 布尔方法使用`is`、`has`、`can`等前缀：`isActive()`, `hasPermission()`

### 变量命名

- 变量命名易懂，能够清晰表达其含义，如果使用缩写必须是常用的缩写，例如：`USER_ID`
- 使用小驼峰命名法
- 避免单字母命名（循环变量除外）
- 常量使用全大写字母，单词间用下划线分隔：`MAX_COUNT`, `DEFAULT_TIMEOUT`

## 注释规范

### 类注释

```java
/**
* 用户服务类，负责用户相关的业务逻辑处理
*
* @author 开发者姓名
* @since 2024-01-01
*/

public class UserService {
    // class content
}
```

### 方法注释
```java
/**
* 根据用户ID获取用户信息
*
* @param userId 用户ID
* @return 用户对象，如果不存在返回null
* @throws IllegalArgumentException 当userId为空时抛出
*/
public User getUserById(String userId) {
    // method implementation
}
```

### 行内注释

- 关键算法或复杂逻辑需要注释
- TODO注释格式：`// TODO(开发者姓名): 说明原因`
- FIXME注释格式：`// FIXME(开发者姓名): 说明原因和修复计划`

## 日志规范

### 日志级别

- `DEBUG`：调试信息
- `INFO`：重要业务流程信息
- `WARN`：警告信息，不影响系统运行
- `ERROR`：错误信息，需要关注和处理

### 日志输出文件类型

- 控制台输出：用于开发和调试，级别为DEBUG或INFO
- 文件输出：分为以下几类，需输出到不同的文件
    - 应用日志：记录应用运行时的重要事件和异常信息，日志名称为`app_<日期>_<分片序号>.log`
    - 访问日志：记录外部访问应用的记录，日志名称为`access_<日期>_<分片序号>.log`
    - 告警日志：记录应用的告警信息，该信息需要立即关注和处理，级别为ERROR或WARN，日志名称为`alarm_<日期>_<分片序号>.log`
    - 测量(Metric)日志：记录应用的运行指标，如请求数、响应时间、错误率等，日志名称为`metric_<日期>_<分片序号>.log`

### 日志格式
- 输出JSON格式的结构化日志

### 日志打印

- 日志打印时，包含必要的上下文信息，包括以下
    - 线程ID
    - 请求ID
    - 时间戳

- 日志打印时，使用占位符（如`{}`）而不是字符串拼接，避免性能问题
```java
// 正确
log.info("用户登录成功，用户名: {}", username);
log.error("数据库连接失败", e);

// 错误
log.info("用户登录成功，用户名: " + username);
```

## 单元测试规范

### 测试类型
- **单元测试**：覆盖所有业务逻辑，使用JUnit 5 + Mockito
- **集成测试**：测试模块间的交互
- **API测试**：测试所有API接口，使用RestAssured
- **性能测试**：关键接口性能验证，使用JMeter
- **安全测试**：API安全测试，使用OWASP ZAP

### 测试覆盖率
- 单元测试覆盖率：≥80%
- API测试覆盖率：100%


## 依赖管理

### 版本集中管理
- 所有依赖版本必须在父模块的`properties`标签中集中定义
- 版本号应按类别分组，并添加清晰的注释说明
- 格式示例：
  ```xml
  <properties>
      <!-- Spring Boot 版本 -->
      <spring-boot.version>4.0.1</spring-boot.version>

      <!-- 工具库版本 -->
      <lombok.version>1.18.40</lombok.version>
      <jmh.version>1.37</jmh.version>
      <opentelemetry.version>1.35.0</opentelemetry.version>

      <!-- 插件版本 -->
      <maven.compiler.plugin.version>3.14.0</maven.compiler.plugin.version>
  </properties>
  ```

### 依赖声明
- 在父模块的`dependencyManagement`标签中声明所有依赖
- 依赖声明应包含本项目模块和第三方依赖
- 格式示例：
  ```xml
  <dependencyManagement>
      <dependencies>
          <!-- Spring Boot 依赖管理 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-dependencies</artifactId>
              <version>${spring-boot.version}</version>
              <type>pom</type>
              <scope>import</scope>
          </dependency>

          <!-- 本项目模块 -->
          <dependency>
              <groupId>com.daniellu</groupId>
              <artifactId>lawyer-common</artifactId>
              <version>${project.version}</version>
          </dependency>

          <!-- 工具库 -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <version>${lombok.version}</version>
          </dependency>
      </dependencies>
  </dependencyManagement>
  ```

### 子模块依赖使用
- 子模块仅需声明依赖，不需要指定版本
- 子模块继承父模块的依赖版本
- 格式示例：
  ```xml
  <dependencies>
      <!-- 不需要指定版本 -->
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
      </dependency>

      <!-- 本项目模块 -->
      <dependency>
          <groupId>com.daniellu</groupId>
          <artifactId>lawyer-common</artifactId>
      </dependency>
  </dependencies>
  ```

## 构建与部署

### 构建流程
- 使用Maven进行构建
- 构建过程包含单元测试、集成测试、代码质量检查
- 构建产物：可执行JAR包

### 部署方式
- 容器化部署（Docker）
- 支持CI/CD流水线
- 灰度发布、蓝绿部署

## 安全规范

### 数据安全

- 敏感信息（密码、密钥）禁止硬编码
- 使用配置中心管理敏感配置
- 数据库连接使用连接池，避免连接泄露

### 代码安全

- 防止SQL注入：使用预编译语句
- 防止XSS攻击：对用户输入进行校验和转义
- 防止CSRF攻击：使用Token验证

## 性能规范

### 数据库操作

- 避免N+1查询问题
- 合理使用索引
- 大数据量查询使用分页
- 避免在循环中执行SQL

### 内存管理

- 及时关闭资源（Stream、Connection等）
- 避免内存泄漏
- 合理使用缓存
## 文档规范

### 项目文档
- 每个模块应有独立的README.md
- 详细说明模块功能、架构、API等
- 包含使用示例和配置说明

### API文档
- 使用OpenAPI 3.0规范
- 自动生成API文档（SpringDoc OpenAPI）
- 文档应包含请求参数、响应格式、示例等

## 变更管理

### 版本管理
- 使用语义化版本控制：MAJOR.MINOR.PATCH
- 主版本号：不兼容的API变更
- 次版本号：向下兼容的功能性新增
- 修订号：向下兼容的问题修正

### 变更流程
- 变更前需编写变更计划
- 变更需经过代码审查
- 变更需经过测试验证
- 变更需记录变更日志
