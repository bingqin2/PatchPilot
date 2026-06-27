package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_self_hosted_launch_readiness_archive")
public class DemoSelfHostedLaunchReadinessArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("ready_to_launch")
    private Boolean readyToLaunch;

    @TableField("summary")
    private String summary;

    @TableField("ready_check_count")
    private Integer readyCheckCount;

    @TableField("needs_attention_check_count")
    private Integer needsAttentionCheckCount;

    @TableField("blocked_check_count")
    private Integer blockedCheckCount;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("report")
    private String report;
}
