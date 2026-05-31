---
name: pure-admin
version: 1.0.1
description: 基于Pure Admin官方文档的技能，用于创建和开发中后台管理系统。支持Vue3、Vite、Element-Plus、TypeScript、Pinia、Tailwindcss等技术栈。
author: Pure Admin Team
official_url: https://pure-admin.cn/
---

# Pure Admin 技能

## 介绍

Pure Admin 是一款开源完全免费且开箱即用的中后台管理系统模板，采用最新的 Vue3、Vite、Element-Plus、TypeScript、Pinia、Tailwindcss 等主流技术开发。

## 技术栈

- **框架**: Vue 3
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **开发语言**: TypeScript
- **状态管理**: Pinia
- **样式框架**: Tailwind CSS
- **路由管理**: Vue Router
- **网络请求**: Axios

## 主要功能

### 1. 项目初始化

根据官方文档初始化 Pure Admin 项目，支持不同版本选择：
- 完整版本 (vue-pure-admin)
- 精简版本 (pure-admin-thin) - 包含国际化和非国际化两个版本
- JS 版本
- Max 版本

### 2. 页面开发

基于 Pure Admin 框架开发中后台页面，支持：
- 列表页开发
- 表单页开发
- 详情页开发
- 图表页开发
- 自定义组件开发

### 3. 组件使用

使用 Pure Admin 提供的内置组件：
- 表格组件 (@pureadmin/table)
- 详情组件 (@pureadmin/descriptions)
- 工具函数 (@pureadmin/utils)

### 4. 路由配置

配置 Pure Admin 路由，支持：
- 动态路由
- 路由守卫
- 路由懒加载
- 权限控制

### 5. 权限管理

实现基于角色的权限管理，支持：
- 菜单权限
- 按钮权限
- 数据权限

### 6. 主题定制

定制 Pure Admin 主题，支持：
- 颜色主题切换
- 暗黑模式
- 布局定制

### 7. 国际化

实现 Pure Admin 国际化，支持：
- 多语言切换
- 动态加载语言包

## 使用示例

### 创建 Pure Admin 项目

```bash
# 初始化精简版（非国际化）
pnpm create pure-admin-thin@latest my-project

# 初始化精简版（国际化）
pnpm create pure-admin-thin@latest my-project -- --i18n
```

### 开发页面

1. 创建页面组件
2. 配置路由
3. 实现业务逻辑
4. 样式设计

### 组件使用示例

```vue
<template>
  <div>
    <pure-table
      :columns="columns"
      :data-source="tableData"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #name="scope">
        <el-tag>{{ scope.row.name }}</el-tag>
      </template>
    </pure-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { PureTable } from '@pureadmin/table'

const tableData = ref([
  { id: 1, name: '张三', age: 18 },
  { id: 2, name: '李四', age: 20 }
])

const columns = ref([
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'name', label: '姓名', width: 120, slot: 'name' },
  { prop: 'age', label: '年龄', width: 80 }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})

const handlePageChange = (page: number) => {
  pagination.value.currentPage = page
  // 加载数据
}
</script>
```

### API服务层使用示例

```typescript
// src/api/user.ts
import { http } from "@/utils/http";

export type UserResult = {
  success: boolean;
  data: {
    avatar: string;
    username: string;
    nickname: string;
    roles: Array<string>;
    permissions: Array<string>;
    accessToken: string;
    refreshToken: string;
    expires: Date;
  };
};

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", "/login", { data });
};
```

## 最佳实践

### 1. 目录结构

遵循 Pure Admin 推荐的目录结构，将不同功能的代码组织到不同的目录中：

```
src/
├── api/             # API请求和数据类型定义
├── assets/          # 静态资源
├── components/      # 自定义组件
├── config/          # 配置文件
├── directives/      # 自定义指令
├── layout/          # 布局组件
├── plugins/         # 插件配置
├── router/          # 路由配置
├── store/           # 状态管理
├── style/           # 样式文件
├── utils/           # 工具函数
└── views/           # 页面组件
```

### 2. API服务层组织

将所有API请求和数据类型定义放在`src/api`目录下，每个功能模块对应一个单独的API文件：

- 为每个功能模块创建一个单独的API文件
- 在API文件中定义所有相关数据类型
- 封装API请求函数，统一处理API请求和响应
- 使用TypeScript接口定义数据类型，提高类型安全性

**示例**：

```typescript
// src/api/crawler.ts
import { http } from "@/utils/http";

export interface Article {
  title: string;
  publishedAt: string;
  url: string;
}

export interface CrawlResult {
  metadata: {
    taskName: string;
  };
  pageResultMap: Record<string, {
    data: Article[];
    dataRecordCount: number;
  }>;
}

/** 创建聚合爬虫任务 */
export const createAggregationTask = (data: { publishedInDays: number }) => {
  return http.request("post", "/api/crawler/custom/aggregationTasks/govArticles", { data });
};
```

### 3. 组件职责分离

组件只关注模板渲染和用户交互，将数据获取和业务逻辑分离到服务层：

- 组件专注于模板渲染和用户交互
- 服务层负责数据获取和业务逻辑处理
- 使用API服务层提供的函数获取数据
- 避免在组件中直接处理复杂的业务逻辑

**重构前后对比**：

**重构前**：组件直接处理API请求
```vue
<script setup lang="ts">
import { ref, reactive } from "vue";
import { http } from "@/utils/http";

// 直接在组件中处理API请求
const handleQuery = async () => {
  try {
    const res = await http.request("post", "/api/crawler/custom/aggregationTasks/govArticles", {
      data: { publishedInDays: searchForm.publishedInDays }
    });
    // 处理响应
  } catch (error) {
    // 处理错误
  }
};
</script>
```

**重构后**：组件使用API服务层函数
```vue
<script setup lang="ts">
import { ref, reactive } from "vue";
import { createAggregationTask } from "@/api/crawler";

// 使用API服务层函数
const handleQuery = async () => {
  try {
    const res = await createAggregationTask({
      publishedInDays: searchForm.publishedInDays
    });
    // 处理响应
  } catch (error) {
    // 处理错误
  }
};
</script>
```

### 4. 命名规范

- 使用 PascalCase 命名组件
- 使用 camelCase 命名变量和函数
- 使用 UPPER_CASE 命名常量
- 文件命名使用 kebab-case
- 接口命名使用 PascalCase，以 "I" 开头（可选）

### 5. 代码风格

- 使用 ESLint 和 Prettier 保持代码风格一致
- 使用 TypeScript 类型定义，提高代码的可维护性和类型安全性
- 编写清晰的注释，说明代码的功能和用途
- 保持代码简洁，避免不必要的复杂性

### 6. 性能优化

- 使用路由懒加载，减少初始加载时间
- 使用组件按需导入，减少打包体积
- 使用 keep-alive 缓存组件，提高页面切换性能
- 避免在模板中使用复杂表达式
- 使用 computed 缓存计算结果
- 合理使用 watch 和监听依赖

### 7. 权限设计

- 合理设计权限模型，避免权限泄露
- 使用路由守卫控制页面访问权限
- 使用自定义指令控制按钮和组件的显示/隐藏
- 实现基于角色的权限管理

### 8. 主题定制

- 使用 Pure Admin 提供的主题定制能力，保持视觉一致性
- 在 `src/style/theme.scss` 中定制主题变量
- 支持暗黑模式切换
- 考虑用户体验，提供友好的主题切换方式

## 常见问题

### 1. 如何添加新页面？

- 在 `src/views` 目录下创建页面组件
- 在 `src/router/modules` 目录下配置路由
- 重新启动开发服务器

### 2. 如何配置代理？

在 `vite.config.ts` 中配置 proxy：

```typescript
export default ({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 3. 如何定制主题？

在 `src/style/theme.scss` 中定制主题变量：

```scss
:root {
  --el-color-primary: #667eea;
  --el-color-success: #764ba2;
}
```

### 4. 如何分离组件的展示层和服务层？

- 创建 API 服务层文件，封装所有 API 请求
- 在组件中导入并使用 API 服务层函数
- 组件只关注模板渲染和用户交互
- 将业务逻辑和数据处理分离到服务层

## 官方资源

- **官方文档**: https://pure-admin.cn/
- **GitHub 仓库**: https://github.com/pure-admin/vue-pure-admin
- **在线预览**:
  - 完整版: https://vue-pure-admin.cn/
  - 精简版: https://pure-admin-thin.cn/
- **配套视频**: https://www.bilibili.com/video/BV1kg411v7QT/

## 更新日志

### v1.0.1 (2026-01-24)

- 更新了最佳实践部分，添加了API服务层组织和组件职责分离的内容
- 增加了API服务层使用示例
- 添加了组件重构前后对比示例

### v1.0.0 (2026-01-24)

- 初始化技能
- 支持 Pure Admin 主要功能
- 提供使用示例和最佳实践

## 贡献

欢迎贡献代码和文档，提交 Issue 和 Pull Request 到 [GitHub 仓库](https://github.com/pure-admin/vue-pure-admin)。

## 许可证

MIT License - 详见 [LICENSE](https://github.com/pure-admin/vue-pure-admin/blob/main/LICENSE) 文件。