package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_timeline_event")
public class FixTaskTimelineEventEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("event_type")
    private String eventType;

    @TableField("message")
    private String message;

    @TableField("created_at")
    private Instant createdAt;
}
