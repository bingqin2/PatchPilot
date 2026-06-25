package io.patchpilot.backend.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "patchpilot.demo")
public class DemoProperties {

    private String repositoryOwner = "";
    private String repositoryName = "";
}
