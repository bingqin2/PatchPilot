package io.patchpilot.backend.github.webhook.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookDeliveryDiagnosticMigrationTests {

    @Test
    void should_create_webhook_delivery_diagnostic_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V16__create_webhook_delivery_diagnostic.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table webhook_delivery_diagnostic");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("delivery_id varchar(128) null");
        assertThat(sql).contains("event varchar(64) not null");
        assertThat(sql).contains("status varchar(64) not null");
        assertThat(sql).contains("task_id varchar(36) null");
        assertThat(sql).contains("repository_owner varchar(128) null");
        assertThat(sql).contains("repository_name varchar(128) null");
        assertThat(sql).contains("issue_number bigint null");
        assertThat(sql).contains("trigger_user varchar(128) null");
        assertThat(sql).contains("trigger_comment text null");
        assertThat(sql).contains("message varchar(512) not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("idx_webhook_delivery_diagnostic_created");
        assertThat(sql).contains("idx_webhook_delivery_diagnostic_delivery");
        assertThat(sql).contains("idx_webhook_delivery_diagnostic_repository");
    }
}
