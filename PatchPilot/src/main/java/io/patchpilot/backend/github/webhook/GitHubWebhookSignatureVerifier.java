package io.patchpilot.backend.github.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class GitHubWebhookSignatureVerifier {

    private final String webhookSecret;

    public GitHubWebhookSignatureVerifier(@Value("${patchpilot.github.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public boolean isValid(String payload, String signatureHeader) {
        if (webhookSecret.isBlank() || signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }
        String expectedSignature = "sha256=" + hmacSha256Hex(payload);
        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signatureHeader.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hmacSha256Hex(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("HmacSHA256 is not available", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to verify GitHub webhook signature", exception);
        }
    }
}
