package com.finsync.service;

import com.finsync.model.Invite;
import com.finsync.model.User;
import com.finsync.repository.InviteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!inviteRepository.findAcceptedBetweenUsers(senderId, receiver.getId()).isEmpty()) {
            throw new IllegalArgumentException("Этот пользователь уже есть в вашем списке друзей");
        }
        if (!inviteRepository.findPendingBySenderAndReceiver(senderId, receiver.getId()).isEmpty()) {
            throw new IllegalArgumentException("Приглашение этому пользователю уже отправлено");
        }
        Invite invite = new Invite();
        invite.setSenderId(senderId);
        invite.setReceiverId(receiver.getId());
        invite.setStatus("PENDING");
        inviteRepository.save(invite);
    }

    public List<User> getPartners(int userId) {
        return getAcceptedPartnerIds(userId).stream()
                .map(userService::findById)
                .collect(Collectors.toList());
    }

    public List<Invite> getSent(int userId) {
        return inviteRepository.findBySenderId(userId);
    }

    public List<Invite> getReceived(int userId) {
        return inviteRepository.findPendingByReceiverId(userId);
    }

    public List<Invite> getAcceptedInvites(int userId) {
        return inviteRepository.findAcceptedByUserId(userId);
    }

    public Invite getAcceptedInviteBetween(int userId, int friendId) {
        List<Invite> list = inviteRepository.findAcceptedBetweenUsers(userId, friendId);
        return list.isEmpty() ? null : list.get(0);
    }

    public void accept(int inviteId) {
        updateStatus(inviteId, "ACCEPTED");
    }

    public void decline(int inviteId) {
        updateStatus(inviteId, "DECLINED");
    }

    public void removeFriend(int inviteId, int userId) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("Приглашение не найдено"));
        if (invite.getSenderId() != userId && invite.getReceiverId() != userId) {
            throw new IllegalArgumentException("Нет доступа");
        }
        invite.setStatus("DECLINED");
        inviteRepository.save(invite);
    }

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
