package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_reviewer_handoff_delivery_receipt")
public class DemoFinalReviewerHandoffDeliveryReceiptEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("handoff_package_status")
    private String handoffPackageStatus;

    @TableField("latest_certificate_archive_id")
    private String latestCertificateArchiveId;

    @TableField("latest_delivery_finalization_archive_id")
    private String latestDeliveryFinalizationArchiveId;

    @TableField("latest_release_bundle_archive_id")
    private String latestReleaseBundleArchiveId;

    @TableField("latest_delivery_receipt_id")
    private String latestDeliveryReceiptId;

    @TableField("latest_package_certificate_archive_id")
    private String latestPackageCertificateArchiveId;

    @TableField("latest_package_archive_id")
    private String latestPackageArchiveId;

    @TableField("latest_package_delivery_receipt_id")
    private String latestPackageDeliveryReceiptId;

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
