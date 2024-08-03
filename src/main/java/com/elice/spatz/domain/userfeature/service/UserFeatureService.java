package com.elice.spatz.domain.userfeature.service;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.userfeature.model.dto.request.BlockCreateDto;
import com.elice.spatz.domain.userfeature.model.dto.request.FriendRequestCreateDto;
import com.elice.spatz.domain.userfeature.model.dto.request.RequestMapper;
import com.elice.spatz.domain.userfeature.model.dto.response.BlockDto;
import com.elice.spatz.domain.userfeature.model.dto.response.FriendDto;
import com.elice.spatz.domain.userfeature.model.dto.response.FriendRequestDto;
import com.elice.spatz.domain.userfeature.model.dto.response.ResponseMapper;
import com.elice.spatz.domain.userfeature.model.entity.Block;
import com.elice.spatz.domain.userfeature.model.entity.FriendRequest;
import com.elice.spatz.domain.userfeature.model.entity.Friendship;
import com.elice.spatz.domain.userfeature.model.entity.Status;
import com.elice.spatz.domain.userfeature.repository.BlockRepository;
import com.elice.spatz.domain.userfeature.repository.FriendRequestRepository;
import com.elice.spatz.domain.userfeature.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.elice.spatz.domain.userfeature.model.entity.Status.ACCEPTED;
import static com.elice.spatz.domain.userfeature.model.entity.Status.REJECTED;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class UserFeatureService {
    private final BlockRepository blockRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;

    // 1. 차단 요청
    @Transactional
    public void createBlock(BlockCreateDto blockCreateDto){
        Block newBlock = requestMapper.blockCreateDtoToBlock(blockCreateDto);
        blockRepository.save(newBlock);
    }
    // 2. 차단 조회
    @Transactional
    public Page<BlockDto> getBlocks(long blockerId, Pageable pageable){
        Page<Block> blocks = blockRepository.findAllByBlockerId(blockerId, pageable);
        List<BlockDto> blockDtoList = new ArrayList<>();
        blockDtoList = blocks.getContent().stream()
                .map(responseMapper::blockToBlockDto)
                .collect(Collectors.toList());

        return new PageImpl<>(blockDtoList, pageable, blocks.getTotalElements());
    }
    // 3. 차단 해제 (하드딜리트)
    @Transactional
    public void unBlock(long id) {
        blockRepository.deleteById(id);
    }

    // 1. 친구 요청
    @Transactional
    public void createFriendRequest(FriendRequestCreateDto friendRequestCreateDto){
        FriendRequest friendRequest = requestMapper.friendRequestCreateDtoToFriendRequest(friendRequestCreateDto);
        friendRequestRepository.save(friendRequest);
    }
    // 2. 보낸/받은 요청 조회
    @Transactional
    public Page<FriendRequestDto> getFriendRequests(String status, long userId, Pageable pageable){
        Page<FriendRequest> friendRequests;

        if (status.equals("sent")){
            friendRequests = friendRequestRepository.findAllByRequesterId(userId, pageable);
        } else if (status.equals("received")){
            friendRequests = friendRequestRepository.findAllByRecipientId(userId, pageable);
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        List<FriendRequestDto> friendRequestDtoList = friendRequests.getContent().stream()
                .map(responseMapper::friendRequestToFriendRequestDto)
                .collect(Collectors.toList());

        return new PageImpl<>(friendRequestDtoList, pageable, friendRequests.getTotalElements());
    }
    // 3. 받은 요청 응답
    @Transactional
    public void responseReceivedFriendRequest(long id, String status){
        FriendRequest receivedFriendRequest = friendRequestRepository.findById(id).orElseThrow();
        if(status.equals("ACCEPTED")){
            receivedFriendRequest.setRequestStatus(ACCEPTED);
            Users requester = receivedFriendRequest.getRequester();
            Users recipient = receivedFriendRequest.getRecipient();

            Friendship friendship = new Friendship();
            friendship.setUser(requester);
            friendship.setFriend((recipient));
            friendship.setFriendStatus(TRUE);

            friendshipRepository.save(friendship);
        } else if (status.equals("REJECTED")){
            receivedFriendRequest.setRequestStatus(REJECTED);
        }
        else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        friendRequestRepository.save(receivedFriendRequest);
    }
    // 4. 보낸/받은 요청 삭제 (하드딜리트)
    @Transactional
    public void deleteSentFriendRequest(long id){
        friendRequestRepository.deleteById(id);
    }

    // 1. 친구 조회
    @Transactional
    public Page<FriendDto> getFriendShips(long userId, Pageable pageable){
        // 내가 userId로 등록된 친구 목록 조회
        Page<Friendship> friendshipsAsUser = friendshipRepository.findAllByUserId(userId, pageable);
        List<FriendDto> userFriends = friendshipsAsUser.getContent().stream()
                .map(f -> new FriendDto(
                        f.getUser().getId(),
                        f.getFriend().getId(),
                        f.getUser().getNickname(),
                        f.getFriend().getNickname()))
                .collect(Collectors.toList());

        // 내가 friendId로 등록된 친구 조회
        Page<Friendship> friendshipsAsFriend = friendshipRepository.findAllByFriendId(userId, pageable);
        List<FriendDto> friendUsers = friendshipsAsFriend.getContent().stream()
                .map(f -> new FriendDto(
                        f.getFriend().getId(),
                        f.getUser().getId(),
                        f.getFriend().getNickname(),
                        f.getUser().getNickname()))
                .collect(Collectors.toList());

        // 최종 친구 목록
        List<FriendDto> allFriendDtos = new ArrayList<>();
        allFriendDtos.addAll(userFriends);
        allFriendDtos.addAll(friendUsers);

        // 페이지 처리
        long totalElements = allFriendDtos.size();
        return new PageImpl<>(allFriendDtos, pageable, totalElements);
    }
    // 2. 친구 검색 조회
    @Transactional
    public Page<FriendDto> getFriendshipsByKeyword(String keyword, long userId, Pageable pageable){
        // 내가 userId로 등록된 친구 목록 조회
        Page<Friendship> friendshipsAsUser = friendshipRepository.findAllByUserId(userId, pageable);
        List<FriendDto> userFriends = friendshipsAsUser.getContent().stream()
                .map(f -> new FriendDto(
                        f.getUser().getId(),
                        f.getFriend().getId(),
                        f.getUser().getNickname(),
                        f.getFriend().getNickname()))
                .filter(dto -> dto.getFriendNickname().contains(keyword))
                .collect(Collectors.toList());

        // 내가 friendId로 등록된 친구 조회
        Page<Friendship> friendshipsAsFriend = friendshipRepository.findAllByFriendId(userId, pageable);
        List<FriendDto> friendUsers = friendshipsAsFriend.getContent().stream()
                .map(f -> new FriendDto(
                        f.getFriend().getId(),
                        f.getUser().getId(),
                        f.getFriend().getNickname(),
                        f.getUser().getNickname()))
                .filter(dto -> dto.getUserNickname().contains(keyword))
                .collect(Collectors.toList());

        // 최종 친구 목록
        List<FriendDto> allFriendDtos = new ArrayList<>();
        allFriendDtos.addAll(userFriends);
        allFriendDtos.addAll(friendUsers);

        // 페이지 처리
        long totalElements = allFriendDtos.size();
        return new PageImpl<>(allFriendDtos, pageable, totalElements);
    }
    // 3. 친구 해제 (하드딜리트)
    @Transactional
    public void deleteFriendShip(long id){
        friendshipRepository.deleteById(id);
    }
}
