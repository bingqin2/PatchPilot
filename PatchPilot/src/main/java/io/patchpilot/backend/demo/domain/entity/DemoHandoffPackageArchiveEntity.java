package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_handoff_package_archive")
public class DemoHandoffPackageArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("session_id")
    private String sessionId;

    @TableField("status")
    private String status;

    @TableField("summary")
    private String summary;

    @TableField("handoff_readiness_status")
    private String handoffReadinessStatus;

    @TableField("handoff_readiness_summary")
    private String handoffReadinessSummary;

    @TableField("handoff_readiness_next_action")
    private String handoffReadinessNextAction;

    @TableField("handoff_ready_check_count")
    private Integer handoffReadyCheckCount;

    @TableField("handoff_needs_attention_check_count")
    private Integer handoffNeedsAttentionCheckCount;

    @TableField("handoff_blocked_check_count")
    private Integer handoffBlockedCheckCount;

    @TableField("share_summary")
    private String shareSummary;

    @TableField("recent_pull_request_url")
    private String recentPullRequestUrl;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("report")
    private String report;
}
