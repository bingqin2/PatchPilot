package io.patchpilot.backend.language.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(310)
public class NodeYarnLanguageAdapter extends NodePackageManagerLanguageAdapter {

    public NodeYarnLanguageAdapter() {
        super(
                "yarn",
                "yarn.lock",
                List.of("yarn", "test"),
                "Detected yarn project with test script"
        );
    }
}
