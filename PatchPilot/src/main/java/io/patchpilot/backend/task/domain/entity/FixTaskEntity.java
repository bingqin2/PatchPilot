package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task")
public class FixTaskEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("repository_owner")
    private String repositoryOwner;

    @TableField("repository_name")
    private String repositoryName;

    @TableField("issue_number")
    private long issueNumber;

    @TableField("installation_id")
    private long installationId;

    @TableField("trigger_user")
    private String triggerUser;

    @TableField("trigger_comment")
    private String triggerComment;

    @TableField("delivery_id")
    private String deliveryId;

    @TableField("comment_id")
    private long commentId;

    @TableField("status")
    private String status;

    @TableField("failure_reason")
    private String failureReason;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("pull_request_url")
    private String pullRequestUrl;

    @TableField("completed_at")
    private Instant completedAt;

    @TableField("updated_at")
    private Instant updatedAt;

    @TableField("language")
    private String language;

    @TableField("build_system")
    private String buildSystem;

    @TableField("verification_command")
    private String verificationCommand;

    @TableField("adapter_detection_reason")
    private String adapterDetectionReason;

    @TableField("status_comment_id")
    private Long statusCommentId;

    @TableField("status_comment_url")
    private String statusCommentUrl;

    @TableField("risk_review_approved_at")
    private Instant riskReviewApprovedAt;

    @TableField("risk_review_approved_by")
    private String riskReviewApprovedBy;

    @TableField("risk_review_approval_reason")
    private String riskReviewApprovalReason;

    @TableField("retry_source_task_id")
    private String retrySourceTaskId;

    @TableField("retry_source_status")
    private String retrySourceStatus;

    @TableField("retry_source_failure_reason")
    private String retrySourceFailureReason;

    @TableField("retry_reason")
    private String retryReason;

    @TableField("retried_at")
    private Instant retriedAt;

}
