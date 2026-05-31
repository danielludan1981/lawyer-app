# 项目规则索引

## 规则结构说明

```
.trae/rules/
├── api/               # API设计规范
│   └── rest.md       # REST API设计规范
├── backend/          # 后端开发规范
│   ├── java.md                  # Java后端开发规范
│   ├── java.data.md             # Java数据层规范
│   ├── java.data.validation.md  # Java数据验证规范
│   ├── java.db.md               # Java数据库规范
│   ├── java.exception.md        # Java异常处理规范
│   ├── java.observability.md    # Java可观察性规范
│   ├── java.crawler.md          # Java爬虫规范
│   ├── Java.log.md              # Java日志规范
│   └── Java.docker.md           # Java Docker规范
├── frontend/         # 前端开发规范
│   └── javascript.md # JavaScript开发规范
├── shared/           # 共享规范
│   ├── project.md    # 项目管理和开发流程规范
│   ├── variables.md  # 项目变量配置和模板系统
│   └── ai.md         # AI相关规范
└── index.md          # 规则索引文件（本文件）
```

## 开发环境
- Windows 11
- Visual Studio Code
- Trae CN 插件
- Terminal: PowerShell

## 规则文件列表

### API规范 (api/)
- `api/rest.md` - REST API设计规范
  - **适用场景**: API设计、接口文档编写、前后端接口对接
  - **技术栈**: RESTful API、OpenAPI、JSON
  - **应用范围**: 所有API相关开发

### 后端规范 (backend/)
- `backend/java.md` - Java后端开发规范
  - **适用场景**: Java后端API开发、Spring Boot项目
  - **技术栈**: Java、Spring Boot、Spring MVC、Spring Data JPA
  - **应用范围**: Controller层、Service层、DAO层开发
- `backend/java.data.md` - Java数据层规范
  - **适用场景**: Java数据访问层开发
  - **技术栈**: Spring Data JPA、MyBatis、数据库操作
  - **应用范围**: 数据层接口设计、Repository实现
- `backend/java.data.validation.md` - Java数据验证规范
  - **适用场景**: Java数据验证
  - **技术栈**: Jakarta Validation、Spring Validation
  - **应用范围**: 请求参数验证、业务数据验证
- `backend/java.db.md` - Java数据库规范
  - **适用场景**: 数据库设计与操作
  - **技术栈**: MySQL、PostgreSQL、数据库设计
  - **应用范围**: 数据库表设计、SQL编写、事务管理
- `backend/java.crawler.md` - Java爬虫规范
  - **适用场景**: Java网络爬虫开发
  - **技术栈**: HttpClient、Jsoup、WebMagic
  - **应用范围**: 网络数据爬取、数据解析
- `backend/Java.log.md` - Java日志规范
  - **适用场景**: Java日志记录
  - **技术栈**: Logback、Log4j2、SLF4J
  - **应用范围**: 日志配置、日志级别、日志格式
- `backend/Java.docker.md` - Java Docker规范
  - **适用场景**: Java应用Docker容器化开发
  - **技术栈**: Docker、Docker Compose、容器编排
  - **应用范围**: Dockerfile编写、镜像构建、容器运行、服务编排
- `backend/Java.observability.md` - Java可观察性规范
  - **适用场景**: Java应用可观察性监控
  - **技术栈**: Prometheus、Grafana、Micrometer
  - **应用范围**: 指标监控、日志分析、性能分析
- `backend/java.exception.md` - Java异常处理规范
  - **适用场景**: Java异常处理
  - **技术栈**: 异常捕获、异常处理、自定义异常
  - **应用范围**: 全局异常处理、业务异常处理

### 前端规范 (frontend/)
- `frontend/javascript.md` - JavaScript开发规范
  - **适用场景**: 前端JavaScript开发、Vue/React项目
  - **技术栈**: JavaScript、TypeScript、Vue.js、React
  - **应用范围**: 前端组件开发、页面交互逻辑

### 共享规范 (shared/)
- `shared/project.md` - 项目管理和开发流程规范
  - **适用场景**: 项目管理、团队协作、开发流程
  - **内容**: 分支策略、提交规范、代码审查、部署流程
  - **应用范围**: 所有开发活动
- `shared/variables.md` - 项目变量配置和模板系统
  - **适用场景**: 项目模板生成、代码模板、目录结构生成
  - **内容**: 动态变量定义、变量替换规则、变量作用域
  - **应用范围**: 所有需要项目特定值的模板和代码生成
- `shared/ai.md` - AI相关规范
  - **适用场景**: AI功能开发、AI模型集成、AI数据处理
  - **技术栈**: 各种AI框架和工具
  - **应用范围**: 所有AI相关开发活动

## 规则应用场景映射

### Java后端开发
- **主要规则**: `backend/java.md`
- **辅助规则**: `api/rest.md`, `shared/project.md`, `backend/java.data.md`, `backend/java.data.validation.md`, `backend/java.db.md`, `backend/Java.log.md`, `backend/Java.docker.md`, `backend/Java.observability.md`, `backend/java.exception.md`
- **使用示例**: "请参考backend/java.md中的Java编码规范，实现这个Service类"

### Java爬虫开发
- **主要规则**: `backend/java.crawler.md`
- **辅助规则**: `backend/java.md`, `shared/project.md`
- **使用示例**: "按照backend/java.crawler.md规范实现这个网络爬虫"

### AI开发
- **主要规则**: `shared/ai.md`
- **辅助规则**: 根据具体开发领域参考其他规范
- **使用示例**: "请遵循shared/ai.md中的规范开发这个AI功能模块"

### API设计
- **主要规则**: `api/rest.md`
- **辅助规则**: `backend/java.md`, `frontend/javascript.md`
- **使用示例**: "按照api/rest.md的规范设计用户管理API"

### 前端开发
- **主要规则**: `frontend/javascript.md`
- **辅助规则**: `api/rest.md`, `shared/project.md`
- **使用示例**: "根据frontend/javascript.md规范实现这个Vue组件"

### 项目管理
- **主要规则**: `shared/project.md`
- **辅助规则**: 根据具体情况参考其他规范
- **使用示例**: "按照shared/project.md的分支策略创建新功能分支"

## 规则优先级

1. **高优先级**: 领域特定规则（如backend/java.md用于Java开发）
2. **中优先级**: 共享规范（如shared/project.md用于接口设计）
3. **基础优先级**: 项目核心规范（shared/project.md）

## 规则维护指南

1. **添加新规则**:
   - 确定规则所属领域（api/backend/frontend/shared）
   - 创建对应的.md文件
   - 更新本索引文件

2. **修改现有规则**:
   - 确认修改范围和影响
   - 更新规则内容
   - 同步更新索引中的适用场景

3. **规则冲突处理**:
   - 领域特定规则优先于共享规则
   - 新规则优先于旧规则
   - 明确标注的规则优先于隐含规则

## 使用建议

1. **开发前**: 先查看相关领域的规范文件
2. **开发中**: 遵循规范中的最佳实践
3. **代码审查**: 以规范文件为审查标准
4. **问题解决**: 查阅规范文件中的解决方案

---

**最后更新**: 2026-01-02
**版本**: 1.1
**维护者**: 项目团队