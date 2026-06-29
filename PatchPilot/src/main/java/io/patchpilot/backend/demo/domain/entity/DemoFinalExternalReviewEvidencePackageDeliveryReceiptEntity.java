package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_external_review_evidence_package_delivery_receipt")
public class DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("final_external_review_package_archive_status")
    private String finalExternalReviewPackageArchiveStatus;

    @TableField("final_external_review_package_archive_id")
    private String finalExternalReviewPackageArchiveId;

    @TableField("closeout_archive_id")
    private String closeoutArchiveId;

    @TableField("completion_archive_id")
    private String completionArchiveId;

    @TableField("completion_evidence_delivery_receipt_id")
    private String completionEvidenceDeliveryReceiptId;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("delivery_channel")
    private String deliveryChannel;

    @TableField("delivery_target")
    private String deliveryTarget;

    @TableField("operator")
    private String operator;

    @TableField("notes")
    private String notes;

    @TableField("delivered_at")
    private Instant deliveredAt;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("markdown_report")
    private String markdownReport;
}
