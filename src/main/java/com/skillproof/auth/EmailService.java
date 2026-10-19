package com.skillproof.auth;

import com.skillproof.common.BadRequestException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.host:}")
    private String smtpHost;

    @Value("${app.mail.from:noreply@skillproof.io}")
    private String mailFrom;

    @Value("${app.mail.dev-mode:true}")
    private boolean devMode;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String recipientEmail, String recipientName, String otpCode, OtpPurpose purpose) {
        String subject;
        String actionTitle;

        switch (purpose) {
            case EMAIL_VERIFICATION -> {
                subject = "Verify your email address — SkillProof";
                actionTitle = "Email Verification";
            }
            case LOGIN -> {
                subject = "Your one-time login code — SkillProof";
                actionTitle = "One-Time Login";
            }
            case PASSWORD_RESET -> {
                subject = "Reset your password — SkillProof";
                actionTitle = "Password Reset";
            }
            default -> {
                subject = "Your SkillProof verification code";
                actionTitle = "Verification";
            }
        }

        // Check if SMTP is configured
        boolean isConfigured = smtpHost != null && !smtpHost.trim().isEmpty() && !smtpHost.equalsIgnoreCase("localhost");

        if (!isConfigured) {
            if (devMode) {
                logger.warn("==================================================================");
                logger.warn(" [DEV EMAIL INBOX] To: {} ({})", recipientEmail, recipientName);
                logger.warn(" [DEV EMAIL INBOX] Subject: {}", subject);
                logger.warn(" [DEV EMAIL INBOX] Action: {}", actionTitle);
                logger.warn(" [DEV EMAIL INBOX] 6-DIGIT OTP CODE: >>> {} <<<", otpCode);
                logger.warn(" [DEV EMAIL INBOX] Valid for 5 minutes. (Configure SMTP for production delivery)");
                logger.warn("==================================================================");
                return;
            } else {
                logger.error("SMTP service is not configured and dev-mode is disabled.");
                throw new BadRequestException("Email delivery is not configured. Please specify SMTP settings in your environment.");
            }
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom, "SkillProof");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);

            String htmlContent = buildHtmlEmailTemplate(recipientName, actionTitle, otpCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Successfully dispatched {} OTP email to {}", purpose, recipientEmail);
        } catch (Exception ex) {
            logger.error("Failed to send {} email to {}: {}", purpose, recipientEmail, ex.getMessage());
            throw new BadRequestException("Unable to deliver verification email at this moment. Please try again later.");
        }
    }

    private String buildHtmlEmailTemplate(String name, String actionTitle, String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #0f172a; margin: 0; padding: 30px; color: #f8fafc; }
                .container { max-width: 520px; margin: 0 auto; background-color: #1e293b; border-radius: 12px; border: 1px solid #334155; padding: 36px; }
                .logo { font-size: 20px; font-weight: 700; color: #818cf8; letter-spacing: -0.5px; margin-bottom: 24px; }
                .title { font-size: 18px; font-weight: 600; color: #f1f5f9; margin-bottom: 12px; }
                .text { font-size: 14px; line-height: 1.6; color: #94a3b8; margin-bottom: 24px; }
                .code-box { background-color: #0f172a; border: 1px solid #4338ca; border-radius: 8px; padding: 20px; text-align: center; margin-bottom: 24px; }
                .otp-code { font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; font-size: 32px; font-weight: 700; letter-spacing: 8px; color: #a5b4fc; }
                .footer { font-size: 12px; color: #64748b; line-height: 1.5; border-top: 1px solid #334155; padding-top: 18px; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="logo">SkillProof</div>
                <div class="title">%s</div>
                <div class="text">Hello %s,<br>Use the 6-digit one-time code below to complete your %s request. This code will expire in 5 minutes.</div>
                <div class="code-box">
                  <div class="otp-code">%s</div>
                </div>
                <div class="text" style="font-size: 13px;">If you did not request this verification code, please ignore this email or contact security if you suspect unauthorized activity.</div>
                <div class="footer">SkillProof &bull; Prove what you can build.</div>
              </div>
            </body>
            </html>
            """.formatted(actionTitle, name != null ? name : "Developer", actionTitle.toLowerCase(), otpCode);
    }
}
