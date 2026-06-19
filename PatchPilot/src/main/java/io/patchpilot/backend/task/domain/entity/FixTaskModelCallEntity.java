package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_model_call")
public class FixTaskModelCallEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("provider")
    private String provider;

    @TableField("model")
    private String model;

    @TableField("prompt_summary")
    private String promptSummary;

    @TableField("response_summary")
    private String responseSummary;

    @TableField("prompt_tokens")
    private int promptTokens;

    @TableField("completion_tokens")
    private int completionTokens;

    @TableField("total_tokens")
    private int totalTokens;

    @TableField("success")
    private boolean success;

    @TableField("error_message")
    private String errorMessage;

    @TableField("started_at")
    private Instant startedAt;

    @TableField("finished_at")
    private Instant finishedAt;

    @TableField("duration_ms")
    private long durationMs;

}
