# playwright-java 技能

## 技能介绍

playwright-java技能提供了在Java项目中使用Playwright进行端到端测试的完整指南，包括环境配置、依赖管理、JUnit测试用例编写以及常用API使用方法。Playwright支持Chromium、Firefox和WebKit浏览器，可以在Windows、Linux和macOS上运行，支持无头和有头模式。

## 环境要求

- Java 8或更高版本
- Windows 11+、Windows Server 2019+ 或 Windows Subsystem for Linux (WSL)
- macOS 14 Ventura或更高版本
- Debian 12/13、Ubuntu 22.04/24.04（x86-64和arm64架构）
- Maven或Gradle构建工具

## Maven依赖配置

在`pom.xml`中添加以下依赖：

```xml
<dependencies>
    <!-- Playwright Java -->
    <dependency>
        <groupId>com.microsoft.playwright</groupId>
        <artifactId>playwright</artifactId>
        <version>1.57.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JUnit 5 API -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- Maven Surefire Plugin for running tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
        </plugin>

        <!-- Compiler Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>11</source>
                <target>11</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 基本用法

### 1. 安装浏览器

首次运行Playwright测试时，会自动下载所需的浏览器二进制文件。也可以手动安装：

```java
import com.microsoft.playwright.*;

public class InstallBrowsers {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            // 自动下载浏览器
            playwright.chromium().launch();
            playwright.firefox().launch();
            playwright.webkit().launch();
            System.out.println("浏览器安装完成！");
        }
    }
}
```

### 2. 第一个JUnit测试用例

创建一个简单的JUnit 5测试用例，测试页面标题：

```java
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstPlaywrightTest {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setUp() {
        // 创建Playwright实例
        playwright = Playwright.create();
        // 启动浏览器（无头模式）
        browser = playwright.chromium().launch();
        // 创建新页面
        page = browser.newPage();
    }

    @Test
    void testPageTitle() {
        // 导航到网页
        page.navigate("https://playwright.dev/");
        // 断言页面标题
        assertEquals("Fast and reliable end-to-end testing for modern web apps | Playwright", page.title());
    }

    @AfterEach
    void tearDown() {
        // 关闭浏览器和Playwright实例
        browser.close();
        playwright.close();
    }
}
```

### 3. 有头模式和慢动作

```java
@BeforeEach
void setUp() {
    playwright = Playwright.create();
    // 有头模式 + 慢动作（50ms）
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(50));
    page = browser.newPage();
}
```

## 常用API示例

### 1. 页面导航和截图

```java
@Test
void testNavigationAndScreenshot() {
    // 导航到页面
    page.navigate("https://example.com");

    // 截图
    page.screenshot(new Page.ScreenshotOptions()
            .setPath(java.nio.file.Paths.get("example.png"))
            .setFullPage(true));

    // 导航到另一个页面
    page.navigate("https://example.org");

    // 返回上一页
    page.goBack();

    // 前进到下一页
    page.goForward();

    // 刷新页面
    page.reload();
}
```

### 2. 元素定位和交互

```java
@Test
void testElementInteraction() {
    page.navigate("https://playwright.dev/java/docs/intro");

    // 按文本定位并点击
    page.locator("text=Get started").click();

    // 按CSS选择器定位
    page.locator("input[type='text']").fill("Hello Playwright");

    // 按XPath定位
    page.locator("//button[@id='submit']").click();

    // 检查元素是否可见
    page.waitForSelector(".success-message", new Page.WaitForSelectorOptions().setState(Page.WaitForSelectorOptions.State.VISIBLE));

    // 获取元素文本
    String message = page.locator(".success-message").textContent();
    assertEquals("Submit successful", message);
}
```

### 3. 表单处理

```java
@Test
void testFormSubmission() {
    page.navigate("https://example.com/form");

    // 填写表单
    page.locator("#name").fill("John Doe");
    page.locator("#email").fill("john@example.com");
    page.locator("#message").fill("Hello from Playwright!");

    // 选择下拉菜单
    page.locator("#country").selectOption(new Locator.SelectOptionValues().setLabel("China"));

    // 勾选复选框
    page.locator("#subscribe").check();

    // 提交表单
    page.locator("button[type='submit']").click();

    // 等待成功页面
    page.waitForURL("**/success");

    // 验证成功消息
    assertEquals("Form submitted successfully!", page.locator(".success").textContent());
}
```

### 4. 处理对话框

```java
@Test
void testDialogHandling() {
    page.navigate("https://example.com/dialog");

    // 监听对话框
    page.onDialog(dialog -> {
        System.out.println("对话框消息: " + dialog.message());
        dialog.accept(); // 接受对话框
        // dialog.dismiss(); // 拒绝对话框
    });

    // 触发对话框
    page.locator("#show-alert").click();
}
```

### 5. 网络请求处理

```java
@Test
void testNetworkRequest() {
    // 监听网络请求
    page.onRequest(request -> {
        System.out.println("请求URL: " + request.url());
    });

    // 监听网络响应
    page.onResponse(response -> {
        System.out.println("响应状态: " + response.status() + " for " + response.url());
    });

    // 导航到页面
    page.navigate("https://example.com");
}
```

## JUnit 5 参数化测试

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParameterizedPlaywrightTest {
    // ... 初始化和清理代码 ...

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "https://example.org", "https://example.net"})
    void testMultipleUrls(String url) {
        page.navigate(url);
        // 验证每个页面都有标题
        assert !page.title().isEmpty();
        System.out.println("页面 " + url + " 的标题: " + page.title());
    }
}
```

## 高级功能

### 1. 多个浏览器测试

```java
@Test
void testWithMultipleBrowsers() {
    // 测试Chromium
    try (Browser chromiumBrowser = playwright.chromium().launch()) {
        Page chromiumPage = chromiumBrowser.newPage();
        chromiumPage.navigate("https://example.com");
        System.out.println("Chromium标题: " + chromiumPage.title());
    }

    // 测试Firefox
    try (Browser firefoxBrowser = playwright.firefox().launch()) {
        Page firefoxPage = firefoxBrowser.newPage();
        firefoxPage.navigate("https://example.com");
        System.out.println("Firefox标题: " + firefoxPage.title());
    }

    // 测试WebKit
    try (Browser webkitBrowser = playwright.webkit().launch()) {
        Page webkitPage = webkitBrowser.newPage();
        webkitPage.navigate("https://example.com");
        System.out.println("WebKit标题: " + webkitPage.title());
    }
}
```

### 2. 移动端仿真

```java
@Test
void testMobileEmulation() {
    // 移动端设备配置
    Browser browser = playwright.chromium().launch();
    BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(375, 667)
            .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1")
            .setHasTouch(true));

    Page mobilePage = context.newPage();
    mobilePage.navigate("https://example.com");

    // 移动端滑动
    mobilePage.touchscreen().swipe(100, 500, 100, 100, new Touchscreen.SwipeOptions().setDuration(500));

    context.close();
    browser.close();
}
```

## 最佳实践

### 通用最佳实践

1. **使用Locator API**：优先使用`page.locator()`而不是旧的选择器方法，Locator API具有自动重试和等待机制
2. **避免硬等待**：使用`page.waitForSelector()`、`page.waitForURL()`等方法替代`Thread.sleep()`
3. **使用Page Object模式**：将页面元素和操作封装到Page类中，提高测试的可维护性
4. **清理资源**：始终在测试结束后关闭浏览器和Playwright实例
5. **并行测试**：使用JUnit 5的并行测试功能提高测试速度
6. **生成测试报告**：集成Allure或Surefire Report生成美观的测试报告
7. **使用Trace Viewer**：记录测试执行过程，便于调试失败的测试

### VS Code开发环境最佳实践

#### 1. 推荐插件

- **Playwright Test for VS Code**：
  - 提供测试运行、调试和录制功能
  - 支持测试用例的可视化运行和结果查看
  - 集成Playwright Codegen功能
  - 安装命令：`ext install ms-playwright.playwright`

- **Java Extension Pack**：
  - 提供Java语言支持、调试、代码补全等功能
  - 安装命令：`ext install vscjava.vscode-java-pack`

- **JUnit Test Explorer**：
  - 在VS Code中可视化运行和调试JUnit测试
  - 安装命令：`ext install donjayamanne.junit-test-adapter`

- **SonarLint**：
  - 实时代码质量分析和问题检测
  - 安装命令：`ext install sonarsource.sonarlint-vscode`

- **GitLens**：
  - 增强Git功能，便于代码版本管理
  - 安装命令：`ext install eamodio.gitlens`

#### 2. VS Code调试配置

在`.vscode/launch.json`中添加以下调试配置：

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Java: Playwright Test",
      "type": "java",
      "request": "launch",
      "mainClass": "org.junit.platform.console.ConsoleLauncher",
      "args": [
        "--select-class", "${file}",
        "--fail-if-no-tests", "false"
      ],
      "console": "integratedTerminal",
      "env": {
        "PLAYWRIGHT_HEADLESS": "false",
        "PLAYWRIGHT_SLOW_MO": "50"
      },
      "classPaths": [
        "${workspaceFolder}/target/test-classes",
        "${workspaceFolder}/target/classes",
        "${java.home}/lib/*"
      ],
      "modulePaths": [
        "${workspaceFolder}/target/test-classes",
        "${workspaceFolder}/target/classes"
      ]
    },
    {
      "name": "Java: Playwright Test (Headless)",
      "type": "java",
      "request": "launch",
      "mainClass": "org.junit.platform.console.ConsoleLauncher",
      "args": [
        "--select-class", "${file}",
        "--fail-if-no-tests", "false"
      ],
      "console": "integratedTerminal"
    }
  ]
}
```

#### 3. 测试运行

- **单个测试方法**：在测试方法上方点击运行按钮，或使用快捷键`Ctrl+Shift+D`打开调试面板选择配置运行
- **单个测试类**：在测试类文件中右键点击，选择"Run Test"或"Debug Test"
- **所有测试**：使用Maven命令`mvn test`或在VS Code的测试资源管理器中点击"Run All Tests"

#### 4. Playwright扩展功能

- **录制测试**：使用Playwright扩展的"Record new test"功能录制用户操作，自动生成测试代码
- **代码生成**：在VS Code中右键点击选择"Generate Playwright Code"，自动生成元素定位代码
- **测试可视化**：在测试资源管理器中查看测试结果，包括通过/失败状态、运行时间等
- **Trace Viewer集成**：在VS Code中直接打开和查看Playwright Trace文件

#### 5. 其他VS Code最佳实践

- **使用VS Code工作区设置**：在`.vscode/settings.json`中配置Java和Playwright相关设置
  ```json
  {
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.test.defaultConfig": "Java: Playwright Test",
    "playwright.showTraceViewerOnFailure": true,
    "playwright.headless": false,
    "playwright.slowMo": 50
  }
  ```

- **使用VS Code任务**：在`.vscode/tasks.json`中配置测试运行和构建任务
  ```json
  {
    "version": "2.0.0",
    "tasks": [
      {
        "label": "build",
        "type": "shell",
        "command": "mvn",
        "args": ["compile"],
        "group": "build"
      },
      {
        "label": "test",
        "type": "shell",
        "command": "mvn",
        "args": ["test"],
        "group": "test"
      },
      {
        "label": "test:clean",
        "type": "shell",
        "command": "mvn",
        "args": ["clean", "test"],
        "group": "test"
      }
    ]
  }
  ```

- **使用代码片段**：创建Playwright常用代码片段，提高开发效率
  ```json
  {
    "Playwright: Setup Test": {
      "prefix": "playwright-setup",
      "body": [
        "private Playwright playwright;",
        "private Browser browser;",
        "private Page page;",
        "",
        "@BeforeEach",
        "void setUp() {",
        "    playwright = Playwright.create();",
        "    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()",
        "            .setHeadless(false)",
        "            .setSlowMo(50));",
        "    page = browser.newPage();",
        "}",
        "",
        "@AfterEach",
        "void tearDown() {",
        "    browser.close();",
        "    playwright.close();",
        "}"
      ],
      "description": "Create Playwright test setup and teardown methods"
    }
  }
  ```

- **使用Live Share**：与团队成员共享VS Code会话，便于协作调试

#### 6. 性能优化

- **启用VS Code的Java语言服务器优化**：
  ```json
  {
    "java.server.launchMode": "Standard",
    "java.compile.nullAnalysis.mode": "automatic"
  }
  ```

- **使用VS Code的文件自动保存功能**：
  ```json
  {
    "files.autoSave": "afterDelay",
    "files.autoSaveDelay": 1000
  }
  ```

- **禁用不必要的插件**：只保留必要的插件，提高VS Code运行速度

#### 7. 代码质量和格式化

- **配置Java格式化规则**：在`.vscode/settings.json`中配置
  ```json
  {
    "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
    "java.format.settings.profile": "GoogleStyle"
  }
  ```

- **使用Prettier格式化非Java文件**：
  ```json
  {
    "[json]": {
      "editor.defaultFormatter": "esbenp.prettier-vscode"
    },
    "[xml]": {
      "editor.defaultFormatter": "redhat.vscode-xml"
    }
  }
  ```

- **启用自动格式化**：
  ```json
  {
    "editor.formatOnSave": true,
    "editor.formatOnType": true
  }
  ```

## 项目结构示例

```
project/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
│           ├── pages/
│           │   └── ExamplePage.java       # Page Object类
│           └── tests/
│               ├── FirstPlaywrightTest.java   # 测试用例
│               └── ParameterizedPlaywrightTest.java  # 参数化测试
└── pom.xml  # Maven依赖配置
```

## 运行测试

### 使用Maven运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn -Dtest=FirstPlaywrightTest test

# 运行特定测试方法
mvn -Dtest=FirstPlaywrightTest#testPageTitle test
```

### 使用IDE运行测试

1. 在IntelliJ IDEA或Eclipse中打开项目
2. 找到测试类或测试方法
3. 右键点击并选择"Run"或"Debug"

## 故障排除

1. **浏览器下载失败**：检查网络连接，或手动设置代理
2. **权限问题**：确保有足够的权限访问浏览器二进制文件
3. **测试超时**：增加超时时间，或优化测试逻辑
4. **元素定位失败**：使用更稳定的选择器，或添加适当的等待
5. **内存占用过高**：关闭不必要的浏览器实例，或增加JVM内存分配

## 参考资源

- [Playwright Java官方文档](https://playwright.dev/java/docs/intro)
- [Playwright Java API参考](https://playwright.dev/java/docs/api/class-playwright)
- [JUnit 5官方文档](https://junit.org/junit5/docs/current/user-guide/)
- [Playwright GitHub仓库](https://github.com/microsoft/playwright-java)

## 更新日志

- v1.0.0: 初始版本，包含基本配置和JUnit测试示例
- v1.1.0: 添加了高级功能示例（移动端仿真、多浏览器测试等）
- v1.2.0: 增加了最佳实践和故障排除指南
- v1.3.0: 增加了VS Code开发环境中的最佳实践，包括插件推荐、调试配置、测试运行等内容

## 贡献

欢迎提交Issues和Pull Requests来改进这个技能文档。