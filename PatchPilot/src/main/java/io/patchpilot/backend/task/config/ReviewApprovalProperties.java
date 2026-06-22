package io.patchpilot.backend.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "patchpilot.review-approval")
public class ReviewApprovalProperties {

    private List<String> allowedOperators = new ArrayList<>();

    public List<String> getAllowedOperators() {
        return allowedOperators;
    }

    public void setAllowedOperators(List<String> allowedOperators) {
        this.allowedOperators = allowedOperators == null ? new ArrayList<>() : allowedOperators;
    }

    public List<String> normalizedAllowedOperators() {
        Map<String, String> operators = new LinkedHashMap<>();
        for (String operator : allowedOperators) {
            if (!StringUtils.hasText(operator)) {
                continue;
            }
            String trimmedOperator = operator.trim();
            operators.putIfAbsent(trimmedOperator.toLowerCase(), trimmedOperator);
        }
        return List.copyOf(operators.values());
    }
}
