package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Message;

import com.wyy.dto.MessageDraft;
import com.wyy.repository.MessageRepository;
import com.wyy.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * REST controller for managing Message.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

    private final Logger log = LoggerFactory.getLogger(MessageResource.class);

    private static final String ENTITY_NAME = "message";

    private final MessageRepository messageRepository;

    public MessageResource(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * POST  /messages : Send a new message.
     *
     * @param message the message to create
     * @return the ResponseEntity with status 200 OK
     */
    @PostMapping("/messages/send")
    @Timed
    public ResponseEntity<Void> sendMessage(HttpServletRequest request, @Valid @RequestBody Message message) {
        log.debug("REST request to send Message : {}", message);

        String userLogin = request.getRemoteUser();
        if (null != userLogin) {
            message.setSender(userLogin);
            message.setId(null);
            message.setViewed(false);
            message.setDeleted(false);
            message.setCreatedDate(Instant.now());
            message.setModifiedDate(Instant.now());
            messageRepository.save(message);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * POST  /messages/multicast : Send a new multicast message.
     *
     * @param messageDraft the message (with receiver list) to create
     * @return the ResponseEntity with status 200 OK
     */
    @PostMapping("/messages/multicast")
    @Timed
    public ResponseEntity<Void> sendMulticastMessage(HttpServletRequest request, @Valid @RequestBody MessageDraft messageDraft) {
        log.debug("REST request to send a new multicast Message : {}", messageDraft);

        String userLogin = request.getRemoteUser();
        if (null != userLogin) {
            Message message = messageDraft.getMessage();
            List<String> receivers = messageDraft.getReceivers();
            message.setSender(userLogin);
            message.setId(null);
            message.setViewed(false);
            message.setDeleted(false);
            message.setCreatedDate(Instant.now());
            message.setModifiedDate(Instant.now());
            receivers.forEach((receiver) -> {
                message.setId(null);  // messageRepository执行save之后把返回值自动赋值给了message，所以这里需要重新为id赋空值
                message.setReceiver(receiver);
                messageRepository.save(message);
            });

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT  /messages/viewed : Updates an existing message' viewed.
     *
     * @param id the message id to update
     * @param viewed the message viewed to update
     * @return the ResponseEntity with status 200 (OK) or 400 (badRequest)
     */
    @PutMapping("/messages/viewed")
    @Timed
    public ResponseEntity<Message> updateMessageViewed(HttpServletRequest request,
                                                       @RequestParam(value = "id") Long id,
                                                       @RequestParam(value = "viewed") Boolean viewed) throws URISyntaxException {
        log.debug("REST request to update Message viewed: {}", id);

        String userLogin = request.getRemoteUser();
        Message message = messageRepository.findOne(id);
        if (null != userLogin && message.getReceiver().equals(userLogin)) {
            message.setViewed(viewed);
            messageRepository.save(message);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT  /messages/deleted : Updates an existing message' deleted.
     *
     * @param id the message id to update
     * @param deleted the message deleted to update
     * @return the ResponseEntity with status 200 (OK) or 400 (badRequest)
     */
    @PutMapping("/messages/deleted")
    @Timed
    public ResponseEntity<Message> updateMessageDeleted(HttpServletRequest request,
                                                       @RequestParam(value = "id") Long id,
                                                       @RequestParam(value = "deleted") Boolean deleted) throws URISyntaxException {
        log.debug("REST request to update Message deleted: {}", id);

        Message message = messageRepository.findOne(id);
        String userLogin = request.getRemoteUser();
        if (null != userLogin && message.getReceiver().equals(userLogin)) {
            message.setDeleted(deleted);
            messageRepository.save(message);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET  /messages : get all the messages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of messages in body,  or 400 (badRequest)
     */
    @GetMapping("/messages")
    @Timed
    public ResponseEntity<List<Message>> getAllmessages(
        HttpServletRequest request,
        @RequestParam(value = "receiver", required = false) String receiver,
        @RequestParam(value = "sender", required = false) String sender,
        @RequestParam(value = "deleted", required = false) Boolean deleted,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {
        log.debug("REST request to get all messages");

        Page<Message> page;
        Specification specification = new Specification<Message>() {
            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();

                if(null != receiver){
                    predicates1.add(criteriaBuilder.equal(root.get("receiver"), receiver));
                }
                if (null != deleted) {
                    predicates1.add(criteriaBuilder.equal(root.get("deleted"), deleted));
                }
                if (null != sender) {
                    predicates1.add(criteriaBuilder.equal(root.get("sender"), sender));
                }

                if (null != filter) {
                    predicates2.add(criteriaBuilder.like(root.get("receiver"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("sender"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("content"), "%"+filter+"%"));
                }

                Predicate predicate1 = criteriaBuilder.and(predicates1.toArray(new Predicate[predicates1.size()]));
                Predicate predicate2 = criteriaBuilder.or(predicates2.toArray(new Predicate[predicates2.size()]));

                if (predicates2.size() > 0) {
                    return criteriaBuilder.and(predicate1, predicate2);
                } else {
                    return predicate1;
                }
            }
        };

        String userLogin = request.getRemoteUser();
        if ((null != userLogin)
            && ((null != receiver && receiver.equals(userLogin)) || (null != sender && sender.equals(userLogin)))) {
            page =  this.messageRepository.findAll(specification, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/messages/paging");

            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET  /messages/:id : get the "id" message.
     *
     * @param id the id of the message to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the message, or with status 404 (Not Found)
     */
    @GetMapping("/messages/{id}")
    @Timed
    public ResponseEntity<Message> getMessage(HttpServletRequest request,
                                              @PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);

        String userLogin = request.getRemoteUser();
        Message message = messageRepository.findOne(id);

        if ((null != message) && (null != userLogin) && (message.getReceiver().equals(userLogin) || message.getSender().equals(userLogin))) {
            return ResponseEntity.ok().body(message);
        } else {
            return  ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE  /messages/:id : delete the "id" message.
     *
     * @param id the id of the message to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/messages/{id}")
    @Timed
    public ResponseEntity<Void> deleteMessage(HttpServletRequest request,
                                              @PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);

        String userLogin = request.getRemoteUser();
        Message message = messageRepository.findOne(id);

        if ((null != message) && (null != userLogin) && message.getReceiver().equals(userLogin)) {
            messageRepository.delete(id);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /messages/unread-count : get the number of unreaded messages.
     *
     * @return the ResponseEntity with status 200 (OK) and the number of unreaded messages in body
     */
    @GetMapping("/messages/unread-count")
    @Timed
    public long getUnreadCount(@RequestParam(value = "receiver") String receiver,
                               @RequestParam(value = "deleted") Boolean deleted) {
        log.debug("REST request to get the number of unreaded messages");

        return messageRepository.countIdByReceiverAndDeletedAndViewed(receiver, deleted, false);
    }

}
