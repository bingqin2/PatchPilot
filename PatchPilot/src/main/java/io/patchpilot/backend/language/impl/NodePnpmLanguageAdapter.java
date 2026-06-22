package io.patchpilot.backend.language.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(300)
public class NodePnpmLanguageAdapter extends NodePackageManagerLanguageAdapter {

    public NodePnpmLanguageAdapter() {
        super(
                "pnpm",
                "pnpm-lock.yaml",
                List.of("pnpm", "test"),
                "Detected pnpm project with test script"
        );
    }
}
