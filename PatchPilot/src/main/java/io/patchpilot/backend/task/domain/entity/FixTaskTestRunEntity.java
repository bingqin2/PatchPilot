package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_test_run")
public class FixTaskTestRunEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("command")
    private String command;

    @TableField("exit_code")
    private int exitCode;

    @TableField("output")
    private String output;

    @TableField("started_at")
    private Instant startedAt;

    @TableField("finished_at")
    private Instant finishedAt;

    @TableField("duration_ms")
    private long durationMs;
}
