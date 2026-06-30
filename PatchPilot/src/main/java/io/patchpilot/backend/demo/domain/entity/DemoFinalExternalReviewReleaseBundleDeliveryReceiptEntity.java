package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_external_review_release_bundle_delivery_receipt")
public class DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("release_bundle_archive_status")
    private String releaseBundleArchiveStatus;

    @TableField("release_bundle_archive_id")
    private String releaseBundleArchiveId;

    @TableField("latest_certificate_archive_id")
    private String latestCertificateArchiveId;

    @TableField("latest_delivery_finalization_archive_id")
    private String latestDeliveryFinalizationArchiveId;

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
