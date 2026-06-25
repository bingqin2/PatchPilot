package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_pre_execution_decision")
public class FixTaskPreExecutionDecisionEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("source")
    private String source;

    @TableField("final_decision")
    private String finalDecision;

    @TableField("safety_allowed")
    private Boolean safetyAllowed;

    @TableField("safety_reason")
    private String safetyReason;

    @TableField("safety_category")
    private String safetyCategory;

    @TableField("active_task_allowed")
    private Boolean activeTaskAllowed;

    @TableField("active_task_reason")
    private String activeTaskReason;

    @TableField("active_task_category")
    private String activeTaskCategory;

    @TableField("quarantine_allowed")
    private Boolean quarantineAllowed;

    @TableField("quarantine_reason")
    private String quarantineReason;

    @TableField("quarantine_category")
    private String quarantineCategory;

    @TableField("rate_limit_allowed")
    private Boolean rateLimitAllowed;

    @TableField("rate_limit_reason")
    private String rateLimitReason;

    @TableField("rate_limit_category")
    private String rateLimitCategory;

    @TableField("trigger_intent_allowed")
    private Boolean triggerIntentAllowed;

    @TableField("trigger_intent_reason")
    private String triggerIntentReason;

    @TableField("trigger_intent_category")
    private String triggerIntentCategory;

    @TableField("issue_context_loaded")
    private Boolean issueContextLoaded;

    @TableField("created_at")
    private Instant createdAt;
}
