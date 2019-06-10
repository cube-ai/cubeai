package com.wyy.repository;

import com.wyy.domain.Message;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor {

    long countIdByReceiverAndDeletedAndViewed(@Param("receiver") String receiver,
                                                @Param("deleted") Boolean deleted,
                                                @Param("viewed") Boolean viewed);
}
