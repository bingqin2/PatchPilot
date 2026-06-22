package io.patchpilot.backend.language.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(290)
public class NodeBunLanguageAdapter extends NodePackageManagerLanguageAdapter {

    public NodeBunLanguageAdapter() {
        super(
                "bun",
                List.of("bun.lockb", "bun.lock"),
                List.of("bun", "test"),
                "Detected Bun project with test script"
        );
    }
}
