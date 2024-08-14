package com.elice.spatz.domain.server.service;

import com.elice.spatz.domain.server.dto.ServerDto;
import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.server.repository.ServerRepository;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.exception.errorCode.ServerErrorCode;
import com.elice.spatz.exception.exception.ServerException;
import java.util.UUID;
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

    @Transactional(readOnly = true)
    public ServerDto getServer(Long id)
    {
        Servers server = serverRepository.findById(id).orElseThrow(()->
                new ServerException(ServerErrorCode.SERVER_NOT_FOUND));

        return new ServerDto(server.getId(),server.getName());
    }

    @Transactional(readOnly = true)
    public List<ServerDto> getServers() {
        List<Servers> servers = serverRepository.findAll();
        return servers.stream()
                .map(server -> new ServerDto(server.getId(),server.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ServerDto createServer(ServerDto serverDto, Long UserId)
    {
        Servers newServer = serverDto.toEntity();
        newServer.setInviteCode(generateInviteCode());
        serverRepository.save(newServer);
        serverDto.setId(newServer.getId());
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


}
