package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_session_archive")
public class DemoSessionArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("session_id")
    private String sessionId;

    @TableField("status")
    private String status;

    @TableField("summary")
    private String summary;

    @TableField("share_summary")
    private String shareSummary;

    @TableField("recent_pull_request_url")
    private String recentPullRequestUrl;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("report")
    private String report;
}
