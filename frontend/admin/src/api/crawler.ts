import { http } from "@/utils/http";

// 定义数据类型
export interface Article {
  title: string;
  publishedAt: string;
  url: string;
}

export interface PageResult {
  data: Article[];
  dataRecordCount: number;
}

export interface CrawlResult {
  metadata: {
    taskName: string;
  };
  pageResultMap: Record<string, PageResult>;
}

export interface TaskStatus {
  status: "CREATED" | "RUNNING" | "SUCCESS" | "FAILED" | "CANCELLED";
  crawlResults: CrawlResult[];
}

export interface TaskResponse {
  success: boolean;
  data: string;
  error?: string;
}

export interface TaskStatusResponse {
  success: boolean;
  data: TaskStatus;
  error?: string;
}

/**
 * 创建聚合爬虫任务
 * @param data 请求参数
 * @returns 任务ID
 */
export const createAggregationTask = (data: { publishedInDays: number }) => {
  return http.request<TaskResponse>("post", "/api/crawler/custom/aggregationTasks/govArticles", { data });
};

/**
 * 查询任务状态
 * @param taskId 任务ID
 * @returns 任务状态
 */
export const queryTaskStatus = (taskId: string) => {
  return http.request<TaskStatusResponse>("get", `/api/crawler/aggregationTasks/${taskId}`);
};