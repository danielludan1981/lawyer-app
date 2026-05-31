# Spring Boot 4爬虫开发规范

## 概述

本文档定义了基于Spring Boot 4框架开发爬虫模块的规范和最佳实践，旨在确保爬虫系统的可维护性、可扩展性、稳定性和合规性。

## 技术选型

| 技术/框架 | 版本 | 用途 |
|----------|------|------|
| Spring Boot | {{springBootVersion}} | 应用框架 |
| Jsoup | {{jsoupVersion}} | 轻量级HTML解析 |
| Selenium | {{seleniumVersion}} | 动态页面爬取，需要模拟浏览器行为时（尤其是爬取JavaScript渲染内容） |
| WebMagic | {{webMagicVersion}} | 爬虫框架，用于处理静态页面的爬取和解析，速度快 |

## 项目结构

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/company/project/
│   │   │       ├── crawler/
│   │   │       │   ├── config/          # 爬虫配置类
│   │   │       │   ├── controller/      # 爬虫管理接口
│   │   │       │   ├── entity/          # 实体类
│   │   │       │   ├── pipeline/        # 数据处理管道
│   │   │       │   ├── processor/       # 页面解析处理器
│   │   │       │   ├── scheduler/       # 任务调度器
│   │   │       │   ├── service/         # 业务逻辑层
│   │   │       │   ├── spider/          # 爬虫实现
│   │   │       │   └── util/            # 工具类
│   │   │       └── common/              # 公共模块
│   │   └── resources/
│   │       ├── application.yml          # 应用配置
│   │       └── crawler/                 # 爬虫相关配置
│   └── test/                            # 测试代码
└── pom.xml                              # Maven配置
```

## 核心组件设计

### 1. 爬虫配置

```java
@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerConfig {
    private String userAgent;
    private int retryTimes;
    private int timeout;
    private int threadCount;
    private boolean useProxy;

    // getter和setter方法
}
```

### 2. 页面处理器

```java
@Component
public class ArticlePageProcessor implements PageProcessor {

    @Autowired
    private CrawlerConfig config;

    @Override
    public void process(Page page) {
        // 解析页面内容
        String title = page.getHtml().xpath("//h1[@class='title']/text()").get();
        String content = page.getHtml().xpath("//div[@class='content']").get();

        // 提取数据
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);

        // 保存数据
        page.putField("article", article);

        // 发现新的URL
        List<String> urls = page.getHtml().xpath("//a[@class='next-page']/@href").all();
        page.addTargetRequests(urls);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setUserAgent(config.getUserAgent())
                .setRetryTimes(config.getRetryTimes())
                .setTimeOut(config.getTimeout());
    }
}
```

### 3. 数据处理管道

```java
@Component
public class ArticlePipeline implements Pipeline {

    @Autowired
    private ArticleService articleService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Article article = resultItems.get("article");
        if (article != null) {
            // 数据持久化
            articleService.save(article);
        }
    }
}
```

### 4. 爬虫服务

```java
@Service
public class CrawlerService {

    @Autowired
    private ArticlePageProcessor pageProcessor;

    @Autowired
    private ArticlePipeline pipeline;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void startCrawler(String startUrl) {
        Spider.create(pageProcessor)
                .addUrl(startUrl)
                .addPipeline(pipeline)
                .setScheduler(new RedisScheduler(redisTemplate))
                .thread(5)
                .run();
    }

    public void stopCrawler(String crawlerId) {
        // 停止爬虫逻辑
    }

    public CrawlerStatus getCrawlerStatus(String crawlerId) {
        // 获取爬虫状态
        return null;
    }
}
```

### 5. 任务调度

```java
@Component
public class CrawlerScheduler {

    @Autowired
    private CrawlerService crawlerService;

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void scheduleDailyCrawl() {
        String startUrl = "https://example.com/articles";
        crawlerService.startCrawler(startUrl);
    }
}
```

## 爬取策略

### 1. 增量爬取

- 使用Redis或数据库记录已爬取的URL
- 爬取前检查URL是否已存在
- 基于时间戳或版本号判断内容是否更新

### 2. 深度控制

- 设置最大爬取深度，避免无限循环
- 区分种子URL、列表页和详情页
- 优先爬取重要页面

### 3. 速率控制

- 设置合理的爬取间隔，避免对目标网站造成压力
- 使用随机延迟模拟人类行为
- 根据目标网站的robots.txt文件调整爬取策略

```java
// 随机延迟示例
public void randomDelay() {
    try {
        Thread.sleep((long) (Math.random() * 3000 + 1000));
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

## 数据处理

### 1. 数据清洗

- 去除HTML标签和多余空格
- 统一数据格式（日期、数字等）
- 验证数据完整性和合法性

### 2. 数据存储

- 使用JPA进行持久化存储
- 对于大量数据可考虑分库分表
- 重要数据进行备份

### 3. 数据去重

- 基于URL去重
- 基于内容哈希去重
- 使用布隆过滤器提高去重效率

## 配置与管理

### 1. 配置文件

```yaml
crawler:
  user-agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
  retry-times: 3
  timeout: 10000
  thread-count: 5
  use-proxy: false
  proxy-pool:
    - host: 127.0.0.1
      port: 8080
  sites:
    example:
      start-urls: ["https://example.com"]
      max-depth: 3
      allow-domains: ["example.com"]
```

### 2. 管理接口

```java
@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping("/start")
    public ResponseDTO<Void> startCrawler(@RequestBody CrawlerRequest request) {
        crawlerService.startCrawler(request.getStartUrl());
        return ResponseDTO.success();
    }

    @PostMapping("/stop/{id}")
    public ResponseDTO<Void> stopCrawler(@PathVariable String id) {
        crawlerService.stopCrawler(id);
        return ResponseDTO.success();
    }

    @GetMapping("/status/{id}")
    public ResponseDTO<CrawlerStatus> getCrawlerStatus(@PathVariable String id) {
        CrawlerStatus status = crawlerService.getCrawlerStatus(id);
        return ResponseDTO.success(status);
    }
}
```

## 性能与稳定性

### 1. 线程管理

- 合理设置线程数，避免资源耗尽
- 使用线程池管理爬虫线程
- 监控线程状态和资源使用情况

### 2. 异常处理

```java
@ControllerAdvice
public class CrawlerExceptionHandler {

    @ExceptionHandler(CrawlerException.class)
    public ResponseDTO<Void> handleCrawlerException(CrawlerException e) {
        // 记录异常日志
        log.error("爬虫异常: {}", e.getMessage(), e);
        return ResponseDTO.error("CRAWLER_ERROR", e.getMessage());
    }
}
```

### 3. 容错机制

- 实现自动重试功能
- 失败任务记录和手动重试
- 断点续爬功能

### 4. 监控与告警

- 使用Spring Boot Actuator监控应用状态
- 集成Prometheus和Grafana进行可视化监控
- 关键指标告警（爬取成功率、处理速度等）

### 5. 反爬机制应对

- **User-Agent轮换**：配置多个User-Agent，每次请求随机选择
- **Cookie管理**：模拟登录状态时的Cookie维护
- **IP代理池**：当检测到IP被封禁时自动切换代理
- **验证码处理**：集成验证码识别服务或人工处理接口
- **动态内容渲染**：使用Selenium或Playwright处理JavaScript渲染内容

```java
// User-Agent轮换示例
private static final List<String> USER_AGENTS = Arrays.asList(
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
);

public String getRandomUserAgent() {
    return USER_AGENTS.get(new Random().nextInt(USER_AGENTS.size()));
}
```

### 6. 分布式爬虫设计

- 使用Redis或消息队列（如RabbitMQ）实现任务分发
- 多实例部署，共享URL去重集合
- 中心化管理爬取状态和任务进度
- 数据分片存储，提高处理效率

## 合规性

### 1. Robots协议

- 遵守目标网站的robots.txt规则
- 使用RobotsTxtConfigurer检查访问权限

### 2. 访问频率控制

- 合理设置爬取间隔
- 避免对目标网站造成服务压力
- 尊重网站的带宽资源

### 3. 数据使用

- 遵守目标网站的使用条款
- 不得用于非法用途
- 注明数据来源

## 测试

### 1. 单元测试

```java
@SpringBootTest
public class ArticlePageProcessorTest {

    @Autowired
    private ArticlePageProcessor pageProcessor;

    @Test
    public void testProcess() {
        // 模拟页面内容
        String html = "<html><body><h1 class='title'>Test Title</h1><div class='content'>Test Content</div></body></html>";
        Page page = new Page();
        page.setHtml(new Html(html, "https://example.com"));

        // 测试页面解析
        pageProcessor.process(page);

        // 验证结果
        Article article = page.getResultItems().get("article");
        Assertions.assertEquals("Test Title", article.getTitle());
        Assertions.contains("Test Content", article.getContent());
    }
}
```

### 2. 集成测试

- 测试完整的爬取流程
- 验证数据处理和存储
- 测试异常场景处理

## 最佳实践

1. **模块化设计**：将爬虫拆分为多个独立组件，便于维护和扩展
2. **配置驱动**：使用配置文件管理爬虫参数，避免硬编码
3. **日志记录**：详细记录爬取过程和异常信息
4. **代码质量**：遵循Java编码规范，保持代码简洁清晰
5. **持续优化**：定期优化爬取策略和性能
6. **文档完善**：编写清晰的文档，便于团队协作
7. **安全防护**：防止反爬机制，避免IP被封禁
8. **资源管理**：及时释放资源，避免内存泄漏

## 总结

本规范基于Spring Boot 4框架，定义了爬虫开发的各个方面，包括技术选型、项目结构、核心组件、爬取策略、数据处理、配置管理、性能优化和合规性要求。遵循这些规范可以开发出高效、稳定、可维护的爬虫系统。