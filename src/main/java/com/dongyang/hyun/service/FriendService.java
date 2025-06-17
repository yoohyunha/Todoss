package com.dongyang.hyun.service;

import com.dongyang.hyun.entity.FriendRequest;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.repository.FriendRequestRepository;
import com.dongyang.hyun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private UserRepository userRepository;

    public boolean sendRequest(Long fromId, Long toId) {
        if (fromId.equals(toId)) return false;
        User from = userRepository.findById(fromId).orElse(null);
        User to = userRepository.findById(toId).orElse(null);
        if (from == null || to == null) return false;
        // 이미 요청이 있거나 친구라면 중복 방지
        if (friendRequestRepository.findByFromUserOrToUserAndStatus(from, to, FriendRequest.Status.ACCEPTED).size() > 0)
            return false;
        FriendRequest req = new FriendRequest(null, from, to, FriendRequest.Status.PENDING);
        friendRequestRepository.save(req);
        return true;
    }

    public boolean acceptRequest(Long requestId, Long userId) {
        FriendRequest req = friendRequestRepository.findById(requestId).orElse(null);
        if (req == null || !req.getToUser().getId().equals(userId)) return false;
        req.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(req);
        return true;
    }

    public boolean rejectRequest(Long requestId, Long userId) {
        FriendRequest req = friendRequestRepository.findById(requestId).orElse(null);
        if (req == null || !req.getToUser().getId().equals(userId)) return false;
        req.setStatus(FriendRequest.Status.REJECTED);
        friendRequestRepository.save(req);
        return true;
    }

    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        List<FriendRequest> friends = friendRequestRepository
                .findByFromUserOrToUserAndStatus(user, user, FriendRequest.Status.ACCEPTED);
        return friends.stream().map(fr ->
                fr.getFromUser().getId().equals(userId) ? fr.getToUser() : fr.getFromUser()
        ).toList();
    }

    public List<FriendRequest> getPendingRequests(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        return friendRequestRepository.findByToUserAndStatus(user, FriendRequest.Status.PENDING);
    }

//    public boolean isFriend(Long userId, Long otherId) {
//        List<FriendRequest> accepted = friendRequestRepository
//                .findByFromUserOrToUserAndStatus(
//                        userRepository.findById(userId).orElse(null),
//                        userRepository.findById(otherId).orElse(null),
//                        FriendRequest.Status.ACCEPTED
//                );
//        return accepted.stream().anyMatch(fr ->
//                (fr.getFromUser().getId().equals(userId) && fr.getToUser().getId().equals(otherId)) ||
//                        (fr.getFromUser().getId().equals(otherId) && fr.getToUser().getId().equals(userId))
//        );
//    }

    public boolean isFriend(Long userId, Long otherId) {
        return friendRequestRepository.existsByFromUserIdAndToUserIdAndStatus(
                userId, otherId, FriendRequest.Status.ACCEPTED)
                || friendRequestRepository.existsByFromUserIdAndToUserIdAndStatus(
                otherId, userId, FriendRequest.Status.ACCEPTED);
    }


}
