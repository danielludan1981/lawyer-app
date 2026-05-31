# Lawyer Assistant

一个基于 Spring Boot 和 Vue3 的法律信息爬虫管理系统。

## 功能特性

- **爬虫管理**: 支持多种数据源的网页内容抓取和解析
- **聚合任务**: 支持自定义聚合内容爬虫任务
- **文章查询**: 提供法律文章的搜索和管理功能
- **权限管理**: 完善的前后端权限控制体系

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.x | 后端框架 |
| Spring Data JPA | 3.2.x | 数据访问层 |
| PostgreSQL | 17 | 生产环境数据库 |
| H2 | 2.2.x | 开发环境数据库 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.x | 前端框架 |
| TypeScript | 5.x | 类型安全 |
| Element Plus | 2.x | UI组件库 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Vite | 6.x | 构建工具 |

## 快速开始

### 后端启动

```bash
# 进入后端目录
cd backend

# 使用Maven构建
mvn clean compile

# 运行爬虫模块
cd lawyer-crawler
mvn spring-boot:run
```

### 前端启动

```bash
# 进入前端目录
cd frontend/admin

# 安装依赖
pnpm install

# 开发模式运行
pnpm dev
```

## 项目结构

```
lawyer-assistant/
├── backend/                    # 后端代码
│   ├── lawyer-crawler/         # 爬虫核心模块
│   ├── lawyer-spring-demo1/    # Spring Boot演示模块
│   ├── lawyer-spring-demo2/    # Spring Boot演示模块
│   ├── lawyer-spring-common/   # Spring通用模块
│   ├── lawyer-web-test/        # Web自动化测试模块
│   └── docker/                 # Docker配置
├── frontend/                   # 前端代码
│   └── admin/                  # 管理后台
├── requirement/                # 需求文档
└── .trae/                      # 开发工具配置
```

## API接口

### 爬虫任务

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/crawler/tasks` | POST | 创建爬虫任务 |
| `/api/crawler/tasks/{id}` | GET | 获取任务详情 |
| `/api/crawler/tasks` | GET | 获取任务列表 |
| `/api/crawler/custom/aggregationTasks/govArticles` | POST | 创建政府文章聚合任务 |

### 文章查询

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/articles` | GET | 查询文章列表 |
| `/api/articles/{id}` | GET | 获取文章详情 |

## 配置说明

### 开发环境

后端默认使用 H2 内存数据库，无需额外配置。

### 生产环境

配置 PostgreSQL 数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lawyer_crawler
    username: admin
    password: your_password
```

## 许可证

MIT License
