package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_tool_call")
public class FixTaskToolCallEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("tool_name")
    private String toolName;

    @TableField("input_summary")
    private String inputSummary;

    @TableField("output_summary")
    private String outputSummary;

    @TableField("success")
    private boolean success;

    @TableField("started_at")
    private Instant startedAt;

    @TableField("finished_at")
    private Instant finishedAt;

    @TableField("duration_ms")
    private long durationMs;
}
