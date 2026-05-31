# Docker Compose with Spring Boot

## 简介

本项目使用 `spring-boot-docker-compose` 依赖来自动管理 Docker 容器，简化了开发和测试环境的搭建。

## 配置说明

### 1. Docker Compose 配置

`src/main/docker-compose.yml` 文件包含了 PostgreSQL 17 容器的配置：

- **镜像**：`postgres:17`
- **容器名**：`lawyer-db`
- **用户**：`admin`
- **密码**：`123456`
- **数据库**：`lawyer_db`
- **端口映射**：`5432:5432`
- **数据卷**：`postgres-data`（持久化数据）
- **健康检查**：自动检测数据库是否就绪

### 2. 数据源配置

`src/main/resources/application-prod.yml` 文件配置了生产环境的数据源：

- **URL**：`jdbc:postgresql://localhost:5432/lawyer_db`
- **用户名**：`admin`
- **密码**：`123456`
- **驱动**：`org.postgresql.Driver`

### 3. Spring Boot Docker Compose 依赖

在 `pom.xml` 文件中添加了以下依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-docker-compose</artifactId>
</dependency>
```

## 使用方法

### 开发环境

当使用开发环境启动应用时（默认 `env_profiles=dev`），应用会使用 H2 内存数据库。

### 生产环境

当使用生产环境启动应用时（`env_profiles=prod`），Spring Boot 会自动：

1. 检测 `src/main/docker-compose.yml` 文件
2. 启动 PostgreSQL 容器
3. 等待容器健康检查通过
4. 配置并连接数据源
5. 启动应用

### 手动管理容器

如果需要手动管理容器，可以使用以下命令：

```bash
# 启动容器
docker-compose -f src/main/docker-compose.yml up -d

# 停止容器
docker-compose -f src/main/docker-compose.yml down

# 查看日志
docker-compose -f src/main/docker-compose.yml logs -f
```

## 注意事项

1. 确保已安装 Docker 和 Docker Compose
2. 确保 5432 端口没有被其他应用占用
3. 首次启动时，Spring Boot 会自动创建数据库表结构（`ddl-auto: update`）
4. 数据会持久化到 `postgres-data` 卷中，容器重启后数据不会丢失