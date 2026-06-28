package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_launch_acceptance_certificate_archive")
public class DemoLaunchAcceptanceCertificateArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("certified")
    private Boolean certified;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("archive_count")
    private Integer archiveCount;

    @TableField("latest_closeout_archive_id")
    private String latestCloseoutArchiveId;

    @TableField("latest_launch_evidence_archive_id")
    private String latestLaunchEvidenceArchiveId;

    @TableField("latest_delivery_receipt_id")
    private String latestDeliveryReceiptId;

    @TableField("latest_session_id")
    private String latestSessionId;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("latest_webhook_delivery_id")
    private String latestWebhookDeliveryId;

    @TableField("evaluation_run_id")
    private String evaluationRunId;

    @TableField("latest_delivery_target")
    private String latestDeliveryTarget;

    @TableField("latest_delivery_channel")
    private String latestDeliveryChannel;

    @TableField("delivery_receipt_freshness")
    private String deliveryReceiptFreshness;

    @TableField("latest_archived_at")
    private Instant latestArchivedAt;

    @TableField("generated_at")
    private Instant generatedAt;

    @TableField("archived_at")
    private Instant archivedAt;

    @TableField("download_actions_json")
    private String downloadActionsJson;

    @TableField("report")
    private String report;
}
