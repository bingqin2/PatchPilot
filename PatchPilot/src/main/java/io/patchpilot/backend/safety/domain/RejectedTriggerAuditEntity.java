package io.patchpilot.backend.safety.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("rejected_trigger_audit")
public class RejectedTriggerAuditEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("source")
    private String source;

    @TableField("delivery_id")
    private String deliveryId;

    @TableField("repository_owner")
    private String repositoryOwner;

    @TableField("repository_name")
    private String repositoryName;

    @TableField("issue_number")
    private Long issueNumber;

    @TableField("trigger_user")
    private String triggerUser;

    @TableField("trigger_comment")
    private String triggerComment;

    @TableField("reason")
    private String reason;

    @TableField("category")
    private String category;

    @TableField("comment_id")
    private Long commentId;

    @TableField("comment_url")
    private String commentUrl;

    @TableField("retried_task_id")
    private String retriedTaskId;

    @TableField("retried_at")
    private Instant retriedAt;

    @TableField("created_at")
    private Instant createdAt;
}
