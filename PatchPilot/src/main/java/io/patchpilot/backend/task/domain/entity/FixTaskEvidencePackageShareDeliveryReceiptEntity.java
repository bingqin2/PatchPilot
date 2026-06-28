package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_evidence_share_delivery_receipt")
public class FixTaskEvidencePackageShareDeliveryReceiptEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("task_evidence_archive_id")
    private String taskEvidenceArchiveId;

    @TableField("task_id")
    private String taskId;

    @TableField("repository_owner")
    private String repositoryOwner;

    @TableField("repository_name")
    private String repositoryName;

    @TableField("issue_number")
    private Long issueNumber;

    @TableField("pull_request_url")
    private String pullRequestUrl;

    @TableField("delivery_channel")
    private String deliveryChannel;

    @TableField("delivery_target")
    private String deliveryTarget;

    @TableField("operator")
    private String operator;

    @TableField("notes")
    private String notes;

    @TableField("message_subject")
    private String messageSubject;

    @TableField("delivered_at")
    private Instant deliveredAt;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("markdown_report")
    private String markdownReport;
}
