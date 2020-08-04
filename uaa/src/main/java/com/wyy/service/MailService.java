package com.wyy.service;

import com.wyy.domain.User;

import io.github.jhipster.config.JHipsterProperties;

import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
            MessageSource messageSource, SpringTemplateEngine templateEngine) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);

    }

    @Async
    public void sendActivationEmail(User user, String activateUrlPrefix) {
        // sendEmailFromTemplate(user, "activationEmail", "email.activation.title");

        String subject = "CubeAI注册帐号激活";
        String content = "" +
            "<html>" +
            "<p>你好！</p>" +
            "<p>你正在进行注册帐号激活，请点击以下链接或者将其复制到浏览器地址栏打开网页来进行激活：</p>" +
            "<a href=\"" + activateUrlPrefix + user.getActivationKey() + "\"" + ">" + activateUrlPrefix + user.getActivationKey() + "</a>" +
            "<p>请妥善保管，切勿向他人泄露！</p>" +
            "<p>谢谢！</p>" +
            "<p>CubeAI ★ 智立方</p>" +
            "<p>(本邮件为系统后台发送，请勿回复！)</p>" +
            "</html>";

        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user, String resetUrlPrefix) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        // sendEmailFromTemplate(user, "passwordResetEmail", "email.reset.title");

        String subject = "CubeAI密码重置";
        String content = "" +
            "<html>" +
            "<p>你好！</p>" +
            "<p>你正在进行密码重置，请点击以下链接或者将其复制到浏览器地址栏打开网页来重置密码：</p>" +
            "<a href=\"" + resetUrlPrefix + user.getResetKey() + "\"" + ">" + resetUrlPrefix + user.getResetKey() + "</a>" +
            "<p>请妥善保管，切勿向他人泄露！</p>" +
            "<p>谢谢！</p>" +
            "<p>CubeAI ★ 智立方</p>" +
            "<p>(本邮件为系统后台发送，请勿回复！)</p>" +
            "</html>";

        sendEmail(user.getEmail(), subject, content, false, true);
    }
}
