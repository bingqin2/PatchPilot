package io.patchpilot.backend.github.webhook.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("webhook_delivery_diagnostic")
public class WebhookDeliveryDiagnosticEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("delivery_id")
    private String deliveryId;

    @TableField("event")
    private String event;

    @TableField("status")
    private WebhookDeliveryDiagnosticStatus status;

    @TableField("task_id")
    private String taskId;

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

    @TableField("message")
    private String message;

    @TableField("created_at")
    private Instant createdAt;
}
