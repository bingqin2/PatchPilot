package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_acceptance_share_package_archive")
public class DemoFinalAcceptanceSharePackageArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("send_ready")
    private Boolean sendReady;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("launch_certificate_archive_id")
    private String launchCertificateArchiveId;

    @TableField("task_certificate_archive_id")
    private String taskCertificateArchiveId;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("recommended_recipients_json")
    private String recommendedRecipientsJson;

    @TableField("required_attachments_json")
    private String requiredAttachmentsJson;

    @TableField("pre_send_checks_json")
    private String preSendChecksJson;

    @TableField("message_subject")
    private String messageSubject;

    @TableField("message_body")
    private String messageBody;

    @TableField("evidence_notes_json")
    private String evidenceNotesJson;

    @TableField("side_effect_contract")
    private String sideEffectContract;

    @TableField("report")
    private String report;

    @TableField("generated_at")
    private Instant generatedAt;

    @TableField("archived_at")
    private Instant archivedAt;
}
