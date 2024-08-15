package com.elice.spatz.domain.server.service;

import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.server.dto.ServerDto;
import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.server.repository.ServerRepository;
import com.elice.spatz.domain.serverUser.dto.ServerUserDto;
import com.elice.spatz.domain.serverUser.entity.ServerUser;
import com.elice.spatz.domain.serverUser.repository.ServerUserRepository;
import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.exception.errorCode.ServerErrorCode;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.exception.ServerException;

import java.util.ArrayList;
import java.util.UUID;

import com.elice.spatz.exception.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ServerUserRepository serverUserRepository;

    @Transactional(readOnly = true)
    public ServerDto getServer(Long id)
    {
        Servers server = serverRepository.findById(id).orElseThrow(()->
                new ServerException(ServerErrorCode.SERVER_NOT_FOUND));


        return new ServerDto(server.getId(),server.getName(),server.getInviteCode(),server.getServerUsers());
    }

    @Transactional(readOnly = true)
    public List<ServerDto> getServers(CustomUserDetails customUserDetails) {
        List<Servers> servers = serverRepository.findAll();

        Users user = userRepository.findById(customUserDetails.getId()).orElseThrow(()->
                new UserException(UserErrorCode.USER_NOT_FOUND));

        List<Servers> userServerList = serverUserRepository.findServersByUser(user);

        return userServerList.stream()
                .map(server -> new ServerDto(server.getId(),server.getName(),server.getInviteCode(),server.getServerUsers()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ServerDto createServer(ServerDto serverDto, Long id)
    {
        Servers newServer = serverDto.toEntity();
        newServer.setServerUsers(new ArrayList<>());
        newServer.setInviteCode(generateInviteCode());
        Users user = userRepository.findById(id).orElseThrow(()->
                new UserException(UserErrorCode.USER_NOT_FOUND));
        ServerUserDto serverUserDto = new ServerUserDto(newServer, user);
        newServer.getServerUsers().add(serverUserDto.toEntity());
        serverRepository.save(newServer);
        serverDto.setId(newServer.getId());
        serverDto.setServerUsers(newServer.getServerUsers());
        System.out.println();
        return serverDto;
    }

    @Transactional
    public void patchServer(Long id, ServerDto serverDto)
    {
        Servers server = serverRepository.findById(id).orElseThrow(()->
                new ServerException(ServerErrorCode.SERVER_NOT_FOUND));
        server.setName(serverDto.getName());

        serverRepository.save(server);


    }

    @Transactional
    public void deleteServer(Long id)
    {
        serverRepository.findById(id).orElseThrow(()->
                new ServerException(ServerErrorCode.SERVER_NOT_FOUND));
        serverRepository.deleteById(id);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public ServerUserDto inviteUser(String code, CustomUserDetails customUserDetails)
    {
        Users user = userRepository.findById(customUserDetails.getId()).orElseThrow(()->
                new UserException(UserErrorCode.USER_NOT_FOUND));
        Servers server = serverRepository.findByInviteCode(code).orElseThrow(()->
                new ServerException(ServerErrorCode.SERVER_NOT_FOUND));
        ServerUserDto serverUserDto = new ServerUserDto(server,user);
        server.getServerUsers().add(serverUserDto.toEntity());
        serverRepository.save(server);
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println(server.getServerUsers());
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println("=======================");

        return serverUserDto;
    }


}
