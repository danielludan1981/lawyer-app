<script setup lang="ts">
import { ref, reactive } from "vue";
import { ElMessage, ElLoading } from "element-plus";
import {
  createAggregationTask,
  queryTaskStatus,
  type CrawlResult
} from "@/api/crawler";

// 查询表单
const searchForm = reactive({
  publishedInDays: 30 // 默认30天
});

// 时间范围选项
const timeRangeOptions = [
  { label: "1天", value: 1 },
  { label: "7天", value: 7 },
  { label: "15天", value: 15 },
  { label: "30天", value: 30 },
  { label: "60天", value: 60 },
  { label: "90天", value: 90 },
  { label: "180天", value: 180 },
  { label: "365天", value: 365 }
];

// 查询结果
const crawlResults = ref<CrawlResult[]>([]);

// 加载状态
const loading = ref(false);
const queryLoading = ref(false);
const polling = ref(false);

// 轮询任务状态
const pollTaskStatus = async (taskId: string) => {
  polling.value = true;

  try {
    while (polling.value) {
      const status = await queryTaskStatus(taskId);

      if (!status.success) {
        ElMessage.error(status.error || "查询任务状态失败");
        polling.value = false;
        break;
      }

      if (status.data.status === "SUCCESS") {
        // 任务成功，获取结果
        crawlResults.value = status.data.crawlResults;
        ElMessage.success("查询成功");
        polling.value = false;
        break;
      } else if (status.data.status === "FAILED") {
        // 任务失败
        ElMessage.error("查询失败，请稍后重试");
        polling.value = false;
        break;
      } else if (status.data.status === "CANCELLED") {
        // 任务取消
        ElMessage.warning("任务已取消");
        polling.value = false;
        break;
      }

      // 等待5秒后再次查询
      await new Promise(resolve => setTimeout(resolve, 5000));
    }
  } catch (error) {
    ElMessage.error("轮询任务状态失败");
    polling.value = false;
  }
};

// 查询按钮点击事件
const handleQuery = async () => {
  loading.value = true;
  queryLoading.value = true;

  try {
    // 清空之前的结果
    crawlResults.value = [];

    // 创建任务
    const taskResponse = await createAggregationTask({
      publishedInDays: searchForm.publishedInDays
    });

    if (taskResponse.success) {
      // 轮询任务状态
      await pollTaskStatus(taskResponse.data);
    } else {
      ElMessage.error(taskResponse.error || "创建任务失败");
    }
  } catch (error) {
    ElMessage.error("创建任务失败，请稍后重试");
  } finally {
    loading.value = false;
    queryLoading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString();
};

// 打开链接
const openLink = (url: string) => {
  window.open(url, "_blank");
};
</script>

<template>
  <div class="crawler-article-query">
    <!-- 查询表单 -->
    <el-card shadow="never" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>法规文章查询</span>
        </div>
      </template>

      <el-form :model="searchForm" label-width="120px" class="mt-2">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最近多少天">
              <el-select
                id="published-in-days"
                v-model="searchForm.publishedInDays"
                placeholder="请选择时间范围"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="option in timeRangeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item>
              <el-button
                id="query-btn"
                type="primary"
                :loading="queryLoading"
                @click="handleQuery"
              >
                查询
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 查询结果 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>查询结果</span>
        </div>
      </template>

      <!-- 加载中 -->
      <el-loading v-if="polling" fullscreen text="加载中..." />

      <!-- 结果列表 -->
      <div v-if="crawlResults.length > 0" class="result-list">
        <div
          v-for="(result, index) in crawlResults"
          :key="index"
          class="result-item mb-6"
        >
          <!-- 表格标题 -->
          <h3 class="result-title mb-3">{{ result.metadata.taskName }}</h3>

          <!-- 表格 -->
          <el-table
            :data="Object.values(result.pageResultMap)[0]?.data || []"
            stripe
            style="width: 100%"
          >
            <el-table-column prop="title" label="文章标题" min-width="400">
              <template #default="scope">
                <a
                  :href="scope.row.url"
                  target="_blank"
                  class="article-title"
                  @click.prevent="openLink(scope.row.url)"
                >
                  {{ scope.row.title }}
                </a>
              </template>
            </el-table-column>

            <el-table-column prop="publishedAt" label="发布日期" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.publishedAt) }}
              </template>
            </el-table-column>

            <el-table-column prop="url" label="文章链接" min-width="200">
              <template #default="scope">
                <a
                  :href="scope.row.url"
                  target="_blank"
                  class="article-url"
                  @click.prevent="openLink(scope.row.url)"
                >
                  查看详情
                </a>
              </template>
            </el-table-column>
          </el-table>

          <!-- 无数据提示 -->
          <div
            v-if="
              (Object.values(result.pageResultMap)[0]?.data || []).length === 0
            "
            class="no-data"
          >
            暂无数据
          </div>
        </div>
      </div>

      <!-- 暂无结果 -->
      <div v-else-if="!polling" class="no-result">
        暂无查询结果，请点击查询按钮开始查询
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.crawler-article-query {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-list {
  margin-top: 20px;
}

.result-item {
  margin-bottom: 24px;
}

.result-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.article-title {
  color: #409eff;
  text-decoration: none;
}

.article-title:hover {
  text-decoration: underline;
}

.article-url {
  color: #67c23a;
  text-decoration: none;
}

.article-url:hover {
  text-decoration: underline;
}

.no-data,
.no-result {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  background-color: #fafafa;
  border-radius: 4px;
}
</style>
