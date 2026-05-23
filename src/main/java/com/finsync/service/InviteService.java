package com.finsync.service;

import com.finsync.model.Invite;
import com.finsync.model.User;
import com.finsync.repository.InviteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InviteService {

    private final InviteRepository inviteRepository;
    private final UserService userService;

    public InviteService(InviteRepository inviteRepository, UserService userService) {
        this.inviteRepository = inviteRepository;
        this.userService = userService;
    }

    public void send(int senderId, String receiverEmail) {
        User receiver = userService.findByEmail(receiverEmail);
        if (receiver.getId().equals(senderId)) {
            throw new IllegalArgumentException("Нельзя пригласить самого себя");
        }
        Invite invite = new Invite();
        invite.setSenderId(senderId);
        invite.setReceiverId(receiver.getId());
        invite.setStatus("PENDING");
        inviteRepository.save(invite);
    }

    public List<Invite> getSent(int userId) {
        return inviteRepository.findBySenderId(userId);
    }

    public List<Invite> getReceived(int userId) {
        return inviteRepository.findPendingByReceiverId(userId);
    }

    public void accept(int inviteId) {
        updateStatus(inviteId, "ACCEPTED");
    }

    public void decline(int inviteId) {
        updateStatus(inviteId, "DECLINED");
    }

    // возвращает id-шники всех с кем есть принятый инвайт
    public List<Integer> getAcceptedPartnerIds(int userId) {
        List<Invite> accepted = inviteRepository.findAcceptedByUserId(userId);
        List<Integer> result = new ArrayList<>();
        for (Invite inv : accepted) {
            if (inv.getSenderId() == userId) {
                result.add(inv.getReceiverId());
            } else {
                result.add(inv.getSenderId());
            }
        }
        return result;
    }

    private void updateStatus(int inviteId, String status) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("Приглашение не найдено"));
        invite.setStatus(status);
        inviteRepository.save(invite);
    }
}
