package com.finsync.repository;

import com.finsync.model.Invite;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InviteRepository extends CrudRepository<Invite, Integer> {

    @Query("SELECT * FROM invites WHERE receiver_id = :userId AND status = 'PENDING'")
    List<Invite> findPendingByReceiverId(int userId);

    @Query("SELECT * FROM invites WHERE sender_id = :userId")
    List<Invite> findBySenderId(int userId);
}
