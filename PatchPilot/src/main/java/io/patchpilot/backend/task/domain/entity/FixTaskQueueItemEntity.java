package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_queue_item")
public class FixTaskQueueItemEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("status")
    private String status;

    @TableField("attempt_count")
    private int attemptCount;

    @TableField("last_error")
    private String lastError;

    @TableField("available_at")
    private Instant availableAt;

    @TableField("locked_at")
    private Instant lockedAt;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("updated_at")
    private Instant updatedAt;
}
