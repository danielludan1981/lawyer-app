# 项目变量配置

## 项目基础信息

### 变量定义

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `{{company}}` | `daniellu` | 公司域名反转的第一部分 |
| `{{project}}` | `lawyer` | 项目名称 |
| `{{module}}` | `common` | 模块名称 |
| `{{groupId}}` | `com.{{company}}` | Maven groupId |
| `{{artifactId}}` | `{{project}}-{{module}}` | Maven artifactId |
| `{{packageBase}}` | `{{groupId}}.{{project}}` | 基础包名 |
| `{{packageModule}}` | `{{packageBase}}.{{module}}` | 模块包名 |

## 项目技术栈信息

| 变量名 | 默认值 | 说明 | 技术参考链接 |
|--------|------|------|------|
| `{{javaVersion}}`  | 25 | JDK版本 ||
| `{{springBootVersion}}` | 4.0.1 | Spring Boot版本 | https://docs.spring.io/spring-boot/index.html , https://github.com/spring-projects/spring-boot/tree/v4.0.1 |
| `{{springVersion}}` | 7 | Spring Framework版本（和Spring Boot版本适配） | https://docs.spring.io/spring-framework/reference/overview.html |
| `{{mysqlVersion}}` | 8.0 | MySQL版本 ||
| `{{postgresqlVersion}}` | 17 | PostgreSQL版本 ||
| `{{redisVersion}}` | 8.4 | Redis版本 ||
| `{{rocketmqVersion}}` | 5.4.0 | RocketMQ版本 ||
| `{{prometheusVersion}}` | 3 | Prometheus版本 ||
| `{{grafanaVersion}}` | 11 | Grafana版本 ||
| `{{hibernateVersion}}` | 7.2 | Hibernate版本 | https://hibernate.org/orm/releases/7.2/ |
| `{{flywayVersion}}` | 11.20.0 | Flyway版本 | https://flywaydb.org/documentation/ , https://github.com/flyway/flyway|
| `{{springDocOpenApiVersion}}` | 3.1.0 | SpringDoc OpenAPI版本 | https://springdoc.org/ |
| `{{knife4jVersion}}` | 4.3.0 | Knife4j版本 | https://doc.xiaominfo.com/ |
| `{{springDataJpaVersion}}` | 4.1.0 | Spring Data JPA版本 | https://spring.io/projects/spring-data-jpa |
| `{{quartzVersion}}` | 2.4.0 | Quartz版本 | https://www.quartz-scheduler.org/ |
| `{{springBatchVersion}}` | 6.1.0 | Spring Batch版本 | https://spring.io/projects/spring-batch |
| `{{jsoupVersion}}` | 1.17.x | Jsoup版本 | https://jsoup.org/ |
| `{{seleniumVersion}}` | 4.16.x | Selenium版本 | https://www.selenium.dev/ |
| `{{webMagicVersion}}` | 0.7.x | WebMagic版本 | https://webmagic.io/ |
| `{{lombokVersion}}` | 1.18.40 | Lombok版本 | https://projectlombok.org/ |



---

**最后更新**: 2025-12-29
**版本**: 1.0
**维护者**: 项目团队