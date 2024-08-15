package com.elice.spatz.domain.server.controller;

import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.server.dto.ServerGetDto;
import com.elice.spatz.domain.server.dto.ServerDto;
import com.elice.spatz.domain.server.service.ServerService;
import java.util.UUID;

import com.elice.spatz.domain.serverUser.dto.ServerUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @GetMapping("/server")
    public ResponseEntity<ServerDto> getServer(@RequestParam("id") Long id)
    {
        return ResponseEntity.ok(serverService.getServer(id));
    }

    @GetMapping("/servers")
    public ResponseEntity<List<ServerDto>> getServers(@AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        return ResponseEntity.ok(serverService.getServers(customUserDetails));
    }

    @PostMapping("/server")
    public ResponseEntity<ServerDto> createServer(@RequestBody ServerDto serverDto, @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        Long UserId = customUserDetails.getId();
        serverService.createServer(serverDto, UserId);
        return ResponseEntity.ok(serverDto);
    }

    @PatchMapping("/server")
    public ResponseEntity<ServerDto> patchServer(@RequestParam("id") Long id, @RequestBody ServerDto serverDto)
    {

        serverService.patchServer(id,serverDto);
        return ResponseEntity.status(HttpStatus.OK).body(serverDto);
    }

    @DeleteMapping("/server")
    public ResponseEntity<Void> deleteServer(@RequestParam("id") Long id)
    {

        serverService.deleteServer(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/server/invite")
    public ResponseEntity<ServerUserDto> inviteUser(@RequestParam("code") String code,@AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        ServerUserDto serverUserDto = serverService.inviteUser(code, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(serverUserDto);
    }


}
