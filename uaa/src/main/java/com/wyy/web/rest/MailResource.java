package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.dto.Mail;
import com.wyy.security.AuthoritiesConstants;
import com.wyy.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * REST controller for managing Message.
 */
@RestController
@RequestMapping("/api")
@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
public class MailResource {

    private final Logger log = LoggerFactory.getLogger(MailResource.class);

    private final MailService mailService;

    public MailResource(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * POST  /mail : send mail.
     *
     * @param mail the mail to send
     * @return the ResponseEntity with status 200 OK
     */
    @PostMapping("/mail")
    @Timed
    public ResponseEntity<Void> sendMail(@Valid @RequestBody Mail mail) {
        log.debug("REST request to send mail : {}", mail);

        this.mailService.sendEmail(mail.getTo(), mail.getSubject(), mail.getContent(), mail.getIsMultipart(), mail.getIsHtml());
        return ResponseEntity.ok().build();
    }

}
