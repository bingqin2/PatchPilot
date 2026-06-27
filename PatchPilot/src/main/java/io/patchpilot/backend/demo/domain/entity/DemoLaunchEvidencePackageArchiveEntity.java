package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_launch_evidence_package_archive")
public class DemoLaunchEvidencePackageArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("ready_to_share")
    private Boolean readyToShare;

    @TableField("summary")
    private String summary;

    @TableField("session_id")
    private String sessionId;

    @TableField("launch_readiness_status")
    private String launchReadinessStatus;

    @TableField("evidence_bundle_status")
    private String evidenceBundleStatus;

    @TableField("handoff_finalization_status")
    private String handoffFinalizationStatus;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("latest_webhook_delivery_id")
    private String latestWebhookDeliveryId;

    @TableField("evaluation_run_id")
    private String evaluationRunId;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("report")
    private String report;
}
