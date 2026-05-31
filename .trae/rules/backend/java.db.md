# Java数据库版本管理规范

## 概述

本文档定义了Java项目中数据库版本管理的规范和最佳实践，采用 **Flyway** 方案，实现自动化的增量脚本生成、严格的版本控制和灵活的工作流。

## 技术选型

| 工具 | 版本 | 用途 |
|------|------|------|
| Flyway | {{flywayVersion}} | 数据库版本管理、自动执行迁移脚本和生成差异DDL脚本 |
| Spring Data JPA | {{springDataJpaVersion}} | Java对象与数据库表映射 |
| MySQL | {{mysqlVersion}} | 关系型数据库 |
| PostgreSQL | {{postgresqlVersion}} | 关系型数据库 |

## 核心原则

1. **Code-First**：先设计Java Entity，再生成数据库脚本
2. **自动化**：减少手动编写SQL的工作量和错误
3. **可追溯性**：所有数据库变更都有版本记录
4. **安全性**：通过开发者审查确保数据安全
5. **环境一致性**：开发、测试、生产环境保持数据库结构一致

## 项目结构

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       └── entity/      # Entity类定义
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/  # Flyway迁移脚本
│   │       │       ├── V20240101__initial_schema.sql
│   │       │       ├── V20240102__add_user_column.sql
│   │       │       └── V20240103__create_order_table.sql
│   │       ├── application.yml # 应用配置
│   │       └── liquibase.properties # Liquibase配置
```

## 配置指南

### 1. 依赖配置

```xml
<!-- Maven依赖配置 -->
<dependencies>
    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Driver 根据需要选择-->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- PostgreSQL Driver 根据需要选择-->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>

<!-- Maven插件配置 -->
<build>
    <plugins>
        <!-- Flyway Maven Plugin -->
        <plugin>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-maven-plugin</artifactId>
            <version>{{flywayVersion}}</version>
            <configuration>
                <url>jdbc:mysql://localhost:3306/dev_db</url>
                <user>root</user>
                <password>password</password>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. Flyway配置

在`application.yml`中配置Flyway：

```yaml
spring:
  # 数据源配置
  datasource:
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
#   datasource:
#     url: jdbc:postgresql://localhost:5432/demo
#     username: postgres
#     password: 123456
#     driver-class-name: org.postgresql.Driver

  # JPA配置
  jpa:
    hibernate:
      ddl-auto: none  # 禁用自动DDL生成，由Flyway管理
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        # dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        show_sql: true

  # Flyway配置
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true  # 首次运行时自动创建flyway_schema_history表
    validate-on-migrate: true  # 运行前验证脚本
    clean-disabled: true  # 禁用clean命令，防止误删数据
```



## 标准工作流

### 1. 设计 (Design)

开发者修改Java Entity类，包括：
- 新建Entity类
- 添加/修改/删除字段
- 调整字段类型或约束
- 重命名字段或表

**示例：添加用户昵称字段**

```java
@Entity
@Table(name = "t_user")
public class UserPO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    // 新增字段
    @Column(name = "nick_name", length = 50)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // getter和setter方法
}
```

### 2. 生成 (Generate)

开发者运行Flyway命令生成增量脚本：

```bash
# 使用Flyway Maven插件生成增量SQL脚本
mvn flyway:generate
```

**执行结果**：
- Flyway对比Entity和本地开发库的差异
- 在`src/main/resources/db/migration`目录下生成SQL文件，命名格式为：`V20240101120000__schema_update.sql`
- 生成的脚本包含所有Entity变更对应的DDL语句

### 3. 审查 (Review) - 关键步骤

开发者必须打开生成的SQL文件进行检查，确保：

**检查点**：
1. ✅ 是否有误删数据的操作（DROP TABLE, DROP COLUMN）
2. ✅ 重命名字段是否被识别为"先Drop再Add"，如果是需要手动调整为RENAME COLUMN
3. ✅ 新增字段是否设置了合理的默认值
4. ✅ 索引和约束是否正确生成
5. ✅ 语句顺序是否正确，是否存在依赖关系问题

**示例：审查生成的SQL文件**

```sql
-- 自动生成的脚本（可能存在问题）
ALTER TABLE t_user DROP COLUMN user_name;
ALTER TABLE t_user ADD COLUMN nick_name VARCHAR(50);

-- 手动调整后的脚本（正确版本）
ALTER TABLE t_user ADD COLUMN nick_name VARCHAR(50);
UPDATE t_user SET nick_name = user_name WHERE nick_name IS NULL;
```

### 4. 执行 (Apply)

本地启动Spring Boot应用，Flyway自动执行迁移：

```bash
# 启动Spring Boot应用
mvn spring-boot:run
```

**执行过程**：
1. Flyway扫描`db/migration`目录
2. 检测到未执行的SQL文件
3. 自动执行脚本，更新数据库结构
4. 更新`flyway_schema_history`表记录
5. 本地数据库与Entity定义保持同步

### 5. 提交 (Commit)

将Entity代码和生成的SQL文件一起提交到Git：

```bash
# 提交代码
git add src/main/java/com/example/demo/entity/UserPO.java
git add src/main/resources/db/migration/V20240101120000__schema_update.sql
git commit -m "Add nickname field to User entity"
```

### 6. 部署 (Deploy)

测试/生产环境部署时，Flyway自动执行迁移：

**部署流程**：
1. 部署应用到测试/生产环境
2. 应用启动时，Flyway自动检测未执行的脚本
3. 按版本顺序执行SQL脚本
4. 记录执行历史到数据库

## 最佳实践

### 1. Entity设计规范

#### 字段命名
```java
// 推荐：使用@Column明确指定列名，采用下划线命名法
@Column(name = "user_name", nullable = false, length = 50)
private String name;

// 不推荐：依赖默认命名策略
private String userName;
```

#### 字段类型
```java
// 推荐：使用明确的长度和约束
@Column(name = "email", nullable = false, unique = true, length = 100)
private String email;

// 推荐：使用Java 8日期时间类型
@Column(name = "created_at", nullable = false, updatable = false)
@CreationTimestamp
private LocalDateTime createdAt;
```

#### 表名
```java
// 推荐：使用@Table明确指定表名，添加前缀
@Entity
@Table(name = "t_user")
public class UserPO {
    // ...
}
```

### 2. 脚本管理规范

#### 命名规则
```
# 格式：V{时间戳}__{描述}.sql
V20240101120000__initial_schema.sql       # 初始架构
V20240102153000__add_user_nickname.sql    # 新增用户昵称字段
V20240103091500__create_order_table.sql   # 新建订单表
```

#### 版本号管理
- 使用时间戳作为版本号，确保全球唯一性
- 版本号格式：`YYYYMMDDHHmmss`
- 避免使用简单数字（如V1, V2），防止冲突

#### 脚本内容
```sql
-- 推荐：使用事务包裹脚本
START TRANSACTION;

-- 新增字段
ALTER TABLE t_user ADD COLUMN nick_name VARCHAR(50);

-- 设置默认值
UPDATE t_user SET nick_name = user_name WHERE nick_name IS NULL;

-- 添加约束
ALTER TABLE t_user MODIFY COLUMN nick_name VARCHAR(50) NOT NULL;

COMMIT;
```

### 3. 环境配置

#### 开发环境
- 允许使用`flyway:clean`重置数据库
- 可以设置`ddl-auto: create-drop`快速开发

#### 测试/生产环境
- 必须禁用`flyway:clean`命令
- 严格使用`ddl-auto: none`
- 启用`validate-on-migrate`验证脚本

### 4. 常见问题处理

#### 字段重命名
```sql
-- 错误：Liquibase可能生成的代码
ALTER TABLE t_user DROP COLUMN old_name;
ALTER TABLE t_user ADD COLUMN new_name VARCHAR(50);

-- 正确：手动调整为重命名
ALTER TABLE t_user RENAME COLUMN old_name TO new_name;
```

#### 数据迁移
```sql
-- 新增字段并迁移数据
ALTER TABLE t_user ADD COLUMN full_name VARCHAR(100);
UPDATE t_user SET full_name = CONCAT(first_name, ' ', last_name);
ALTER TABLE t_user MODIFY COLUMN full_name VARCHAR(100) NOT NULL;
```

#### 约束调整
```sql
-- 添加唯一约束
ALTER TABLE t_user ADD CONSTRAINT uk_user_email UNIQUE (email);

-- 删除约束
ALTER TABLE t_user DROP CONSTRAINT uk_user_email;
```

## 达成目标分析

| 目标 | 实现方式 | 状态 |
|------|----------|------|
| 自动化增量脚本生成 | Liquibase Diff对比Entity和数据库差异，自动生成脚本 | ✅ |
| 严格的版本控制 | Flyway强制版本号管理，Git记录所有变更 | ✅ |
| 与Java开发习惯匹配 | Code-First模式，开发者专注于Entity设计 | ✅ |
| 灵活的工作流 | 本地支持快速重置，生产环境严格增量 | ✅ |
| 轻量级实现 | 集成到Build工具链，无需独立服务器或GUI | ✅ |

## 监控与维护

### 1. 脚本执行状态

通过查询`flyway_schema_history`表查看脚本执行状态：

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_on DESC;
```

### 3. 常见命令

```bash
# 生成增量脚本
mvn flyway:generate

# 验证脚本
mvn flyway:validate

# 执行迁移
mvn flyway:migrate

# 查看迁移状态
mvn flyway:info

# 修复校验失败
mvn flyway:repair
```

### 4. 回滚策略

- 使用版本化脚本，避免直接修改已执行的脚本
- 如需回滚，创建新的回滚脚本（如`V20240104__rollback_user_nickname.sql`）
- 记录回滚原因和影响范围

## 总结

本规范采用 **Flyway + Liquibase Diff** 组合方案，实现了自动化、安全、可追溯的数据库版本管理。通过严格遵循标准工作流和设计规范，开发者可以：

1. 专注于Java Entity设计，减少SQL编写工作量
2. 自动生成增量脚本，降低人为错误
3. 通过审查确保数据安全
4. 保持开发、测试、生产环境的一致性
5. 实现完整的变更追溯和版本控制

该方案轻量级、易于集成，完全符合Java开发者的工作习惯，是现代Java项目数据库版本管理的最佳实践。