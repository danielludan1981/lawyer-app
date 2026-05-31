# 前端基于pure-admin-thin(i18n)框架的最佳实践规则

## 1. 项目初始化

### 1.1 克隆仓库
```bash
# 克隆国际化精简版前端代码
git clone -b i18n https://github.com/pure-admin/pure-admin-thin.git
```

### 1.2 安装依赖
```bash
# 进入项目目录
cd pure-admin-thin

# 安装依赖
pnpm install
```

### 1.3 开发环境运行
```bash
pnpm dev
```

### 1.4 生产环境构建
```bash
pnpm build
```

## 2. 项目目录结构规划

### 2.1 核心目录结构
```
pure-admin-thin/
├── public/              # 静态资源目录
├── src/
│   ├── api/             # API请求封装
│   ├── assets/          # 资源文件（图片、图标等）
│   ├── components/      # 公共组件
│   ├── directives/      # 自定义指令
│   ├── hooks/           # 自定义Hook
│   ├── i18n/            # 国际化配置
│   ├── layouts/         # 布局组件
│   ├── plugins/         # 插件配置
│   ├── router/          # 路由配置
│   ├── stores/          # Pinia状态管理
│   ├── styles/          # 样式文件
│   ├── utils/           # 工具函数
│   ├── views/           # 页面组件
│   ├── App.vue          # 根组件
│   ├── main.ts          # 入口文件
│   └── vite-env.d.ts    # Vite环境类型定义
├── .env.development     # 开发环境变量
├── .env.production      # 生产环境变量
├── .eslintrc.js         # ESLint配置
├── .prettierrc.js       # Prettier配置
├── index.html           # HTML入口文件
├── package.json         # 项目依赖配置
├── tsconfig.json        # TypeScript配置
└── vite.config.ts       # Vite配置
```

### 2.2 目录使用规范

#### 2.2.1 api目录
- 按业务模块组织API文件
- 每个模块对应一个API文件
- 使用axios封装请求
- 示例：
  ```
  api/
  ├── user.ts     # 用户相关API
  ├── menu.ts     # 菜单相关API
  └── article.ts  # 文章相关API
  ```

#### 2.2.2 components目录
- 按功能或业务模块组织组件
- 公共基础组件放在根目录
- 业务组件按模块划分
- 示例：
  ```
  components/
  ├── BaseButton/      # 基础按钮组件
  ├── BaseDialog/      # 基础对话框组件
  ├── BaseTable/       # 基础表格组件
  └── UserComponents/  # 用户模块相关组件
  ```

#### 2.2.3 views目录
- 按路由结构组织页面组件
- 每个页面组件对应一个路由
- 复杂页面拆分为多个子组件
- 示例：
  ```
  views/
  ├── dashboard/       # 仪表盘
  ├── user/            # 用户管理
  │   ├── list.vue     # 用户列表
  │   └── edit.vue     # 用户编辑
  └── article/         # 文章管理
  ```

#### 2.2.4 stores目录
- 按业务模块组织Pinia状态
- 每个模块对应一个store文件
- 使用TypeScript定义state类型
- 示例：
  ```
  stores/
  ├── user.ts       # 用户状态管理
  ├── menu.ts       # 菜单状态管理
  └── setting.ts    # 系统设置状态管理
  ```

## 3. 组件使用规则

### 3.1 Vue3 Composition API
- 优先使用Composition API，避免使用Options API
- 使用`<script setup lang="ts">`语法糖
- 合理使用`ref`、`reactive`、`computed`、`watch`等响应式API
- 示例：
  ```vue
  <template>
    <div>{{ count }}</div>
    <el-button @click="increment">Increment</el-button>
  </template>

  <script setup lang="ts">
  import { ref } from 'vue'

  const count = ref(0)
  const increment = () => {
    count.value++
  }
  </script>
  ```

### 3.2 Element Plus组件
- 优先使用Element Plus提供的组件
- 遵循Element Plus的使用规范
- 合理使用组件的属性、事件和插槽
- 示例：
  ```vue
  <template>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="date" label="Date" width="180" />
      <el-table-column prop="name" label="Name" width="180" />
      <el-table-column prop="address" label="Address" />
    </el-table>
  </template>

  <script setup lang="ts">
  interface TableItem {
    date: string
    name: string
    address: string
  }

  const tableData: TableItem[] = [
    {
      date: '2023-01-01',
      name: 'John Doe',
      address: 'New York'
    }
  ]
  </script>
  ```

### 3.3 自定义组件
- 组件命名使用大驼峰命名法（PascalCase）
- 组件文件使用与组件名相同的名称
- 合理使用Props和Emits
- 为Props和Emits添加类型定义
- 示例：
  ```vue
  <template>
    <div class="base-button">
      <el-button :type="type" :size="size" @click="handleClick">
        <slot></slot>
      </el-button>
    </div>
  </template>

  <script setup lang="ts">
  import { ElButton } from 'element-plus'

  interface Props {
    type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
    size?: 'large' | 'default' | 'small'
  }

  const props = withDefaults(defineProps<Props>(), {
    type: 'default',
    size: 'default'
  })

  const emit = defineEmits<{
    (e: 'click'): void
  }>()

  const handleClick = () => {
    emit('click')
  }
  </script>
  ```

### 3.4 国际化组件
- 使用`i18n`提供的`t`函数进行文本翻译
- 按模块组织语言包
- 示例：
  ```vue
  <template>
    <div>{{ t('user.title') }}</div>
  </template>

  <script setup lang="ts">
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  </script>
  ```

## 4. 编码规范

### 4.1 TypeScript规范
- 为所有变量、函数、组件添加类型定义
- 优先使用接口（interface）定义对象类型
- 合理使用泛型
- 避免使用`any`类型
- 示例：
  ```typescript
  interface User {
    id: number
    name: string
    email: string
  }

  const getUserById = (id: number): Promise<User> => {
    return api.getUser(id)
  }
  ```

### 4.2 命名规范
- 变量名：使用小驼峰命名法（camelCase）
- 函数名：使用小驼峰命名法（camelCase）
- 常量名：使用大写字母加下划线（UPPER_CASE_WITH_UNDERSCORES）
- 组件名：使用大驼峰命名法（PascalCase）
- 文件名：使用大驼峰命名法（PascalCase）或短横线命名法（kebab-case）

### 4.3 代码格式化
- 使用Prettier进行代码格式化
- 配置`.prettierrc.js`文件
- 示例配置：
  ```javascript
  module.exports = {
    printWidth: 120,
    tabWidth: 2,
    useTabs: false,
    semi: true,
    singleQuote: true,
    quoteProps: 'as-needed',
    trailingComma: 'es5',
    bracketSpacing: true,
    arrowParens: 'always',
    endOfLine: 'lf'
  }
  ```

### 4.4 ESLint规范
- 遵循ESLint规则
- 配置`.eslintrc.js`文件
- 示例配置：
  ```javascript
  module.exports = {
    root: true,
    env: {
      browser: true,
      node: true,
      es2021: true
    },
    extends: [
      'plugin:vue/vue3-recommended',
      '@vue/typescript/recommended',
      '@vue/prettier',
      '@vue/prettier/@typescript-eslint'
    ],
    parserOptions: {
      ecmaVersion: 2021,
      sourceType: 'module'
    },
    rules: {
      '@typescript-eslint/no-explicit-any': 'off',
      'vue/multi-word-component-names': 'off'
    }
  }
  ```

### 4.5 注释规范
- 为复杂逻辑添加注释
- 为函数添加JSDoc注释
- 为组件添加使用说明
- 示例：
  ```typescript
  /**
   * 获取用户列表
   * @param params 查询参数
   * @returns 用户列表数据
   */
  const getUserList = async (params: UserQueryParams): Promise<User[]> => {
    // 发送API请求
    const response = await api.get('/users', params)
    return response.data
  }
  ```

## 5. 状态管理规范

### 5.1 Pinia使用
- 使用Pinia进行状态管理
- 按业务模块组织store
- 使用TypeScript定义state类型
- 合理使用getters、actions
- 示例：
  ```typescript
  import { defineStore } from 'pinia'

  interface UserState {
    userInfo: User | null
    token: string | null
  }

  export const useUserStore = defineStore('user', {
    state: (): UserState => ({
      userInfo: null,
      token: null
    }),

    getters: {
      isLoggedIn: (state) => !!state.token
    },

    actions: {
      async login(credentials: LoginCredentials) {
        const response = await api.post('/login', credentials)
        this.token = response.data.token
        this.userInfo = response.data.user
      },

      logout() {
        this.token = null
        this.userInfo = null
      }
    }
  })
  ```

## 6. 路由配置规范

### 6.1 路由定义
- 使用TypeScript定义路由类型
- 按模块组织路由
- 合理使用路由守卫
- 示例：
  ```typescript
  import { RouteRecordRaw } from 'vue-router'

  const routes: RouteRecordRaw[] = [
    {
      path: '/',
      component: () => import('@/layouts/index.vue'),
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue')
        },
        {
          path: '/user',
          name: 'User',
          component: () => import('@/views/user/index.vue')
        }
      ]
    }
  ]
  ```

### 6.2 路由守卫
- 使用全局路由守卫进行权限控制
- 在路由守卫中处理页面跳转逻辑
- 示例：
  ```typescript
  import { router } from './index'
  import { useUserStore } from '@/stores/user'

  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    // 不需要登录的页面直接放行
    if (to.meta.requireAuth === false) {
      next()
      return
    }

    // 已登录用户直接放行
    if (userStore.isLoggedIn) {
      next()
      return
    }

    // 未登录用户跳转到登录页
    next('/login')
  })
  ```

## 7. API请求规范

### 7.1 API封装
- 使用axios封装API请求
- 配置请求拦截器和响应拦截器
- 统一处理错误
- 示例：
  ```typescript
  import axios from 'axios'
  import { ElMessage } from 'element-plus'

  const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000
  })

  // 请求拦截器
  api.interceptors.request.use(
    (config) => {
      // 添加token
      const token = localStorage.getItem('token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  api.interceptors.response.use(
    (response) => {
      return response.data
    },
    (error) => {
      // 统一处理错误
      ElMessage.error(error.response?.data?.message || '请求失败')
      return Promise.reject(error)
    }
  )

  export default api
  ```

### 7.2 API调用
- 按业务模块组织API函数
- 使用async/await语法调用API
- 合理处理加载状态和错误
- 示例：
  ```vue
  <template>
    <el-button @click="fetchData" :loading="loading">获取数据</el-button>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-else-if="data">{{ data }}</div>
  </template>

  <script setup lang="ts">
  import { ref } from 'vue'
  import api from '@/api'

  const loading = ref(false)
  const error = ref<string | null>(null)
  const data = ref<any>(null)

  const fetchData = async () => {
    loading.value = true
    error.value = null

    try {
      const result = await api.get('/data')
      data.value = result
    } catch (err) {
      error.value = '获取数据失败'
    } finally {
      loading.value = false
    }
  }
  </script>
  ```

## 8. 性能优化

### 8.1 组件优化
- 使用`v-memo`优化长列表
- 使用`defineAsyncComponent`异步加载组件
- 合理使用`keep-alive`缓存组件
- 示例：
  ```vue
  <template>
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive" />
    </keep-alive>
    <router-view v-else />
  </template>
  ```

### 8.2 资源优化
- 使用Vite的动态导入功能
- 图片使用懒加载
- 合理使用CDN资源

### 8.3 代码分割
- 按路由分割代码
- 按组件分割代码
- 示例：
  ```typescript
  const routes: RouteRecordRaw[] = [
    {
      path: '/user',
      name: 'User',
      component: () => import('@/views/user/index.vue') // 动态导入
    }
  ]
  ```

## 9. 测试规范

### 9.1 单元测试
- 使用Vitest进行单元测试
- 为组件、工具函数编写测试用例
- 示例：
  ```typescript
  import { describe, it, expect } from 'vitest'
  import { mount } from '@vue/test-utils'
  import BaseButton from '@/components/BaseButton/index.vue'

  describe('BaseButton', () => {
    it('should render correctly', () => {
      const wrapper = mount(BaseButton)
      expect(wrapper.exists()).toBe(true)
    })

    it('should emit click event when clicked', () => {
      const wrapper = mount(BaseButton)
      wrapper.trigger('click')
      expect(wrapper.emitted('click')).toBeTruthy()
    })
  })
  ```

### 9.2 E2E测试
- 使用Cypress进行E2E测试
- 为核心业务流程编写测试用例

## 10. 部署规范

### 10.1 构建配置
- 配置不同环境的构建参数
- 优化构建产物

### 10.2 CI/CD配置
- 使用GitHub Actions或GitLab CI进行持续集成
- 配置自动化测试和部署流程

## 参考链接

1. pure-admin-thin(i18n)框架的官方文档：https://github.com/pure-admin/pure-admin-thin/tree/i18n
2. Vue3官方文档：https://v3.vuejs.org/
3. TypeScript官方文档：https://www.typescriptlang.org/
4. Element Plus官方文档：https://element-plus.org/
5. Pinia官方文档：https://pinia.vuejs.org/