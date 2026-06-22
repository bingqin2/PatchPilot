package io.patchpilot.backend.language.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(320)
public class NodeNpmLanguageAdapter extends NodePackageManagerLanguageAdapter {

    public NodeNpmLanguageAdapter() {
        super(
                "npm",
                List.of(),
                List.of("npm", "test"),
                "Detected npm project with test script"
        );
    }
}
