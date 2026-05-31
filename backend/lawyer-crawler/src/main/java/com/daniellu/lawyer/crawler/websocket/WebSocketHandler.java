package com.daniellu.lawyer.crawler.websocket;

import com.daniellu.lawyer.crawler.dto.CrawlResponse;
import com.daniellu.lawyer.crawler.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket处理器
 * 处理WebSocket连接和实时任务状态通知
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Component
@RequiredArgsConstructor
public class WebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * WebSocket主题前缀常量
     */
    private static final String TOPIC_TASKS_PREFIX = "/topic/tasks/";

    /**
     * 推送任务进度通知
     *
     * @param taskId     任务ID
     * @param percentage 进度百分比
     */
    public void sendProgressNotification(String taskId, int percentage) {
        String destination = TOPIC_TASKS_PREFIX + taskId;
        TaskProgress progress = TaskProgress.builder()
                .taskId(taskId)
                .status(TaskStatus.RUNNING)
                .progress(percentage)
                .build();
        messagingTemplate.convertAndSend(destination, progress);
    }

    /**
     * 推送任务完成通知
     *
     * @param result 爬取结果
     */
    public void sendCompletionNotification(CrawlResponse result) {
        if (result == null || result.getMetadata() == null) {
            return;
        }
        String destination = "/topic/tasks/" + result.getMetadata().getTaskId();
        messagingTemplate.convertAndSend(destination, result);
    }

    /**
     * 推送任务失败通知
     *
     * @param taskId      任务ID
     * @param errorMessage 错误信息
     */
    public void sendFailureNotification(String taskId, String errorMessage) {
        String destination = "/topic/tasks/" + taskId;
        TaskProgress progress = TaskProgress.builder()
                .taskId(taskId)
                .status(TaskStatus.FAILED)
                .progress(0)
                .errorMessage(errorMessage)
                .build();
        messagingTemplate.convertAndSend(destination, progress);
    }

    /**
     * 任务进度信息
     */
    public static class TaskProgress {
        private String taskId;
        private TaskStatus status;
        private int progress;
        private String errorMessage;

        // 构建器模式
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final TaskProgress progress = new TaskProgress();

            public Builder taskId(String taskId) {
                progress.taskId = taskId;
                return this;
            }

            public Builder status(TaskStatus status) {
                progress.status = status;
                return this;
            }

            public Builder progress(int progressValue) {
                progress.progress = progressValue;
                return this;
            }

            public Builder errorMessage(String errorMessage) {
                progress.errorMessage = errorMessage;
                return this;
            }

            public TaskProgress build() {
                return progress;
            }
        }

        // getter方法
        public String getTaskId() {
            return taskId;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public int getProgress() {
            return progress;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
