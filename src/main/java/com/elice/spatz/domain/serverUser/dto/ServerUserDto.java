package com.elice.spatz.domain.serverUser.dto;

import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.serverUser.entity.ServerUser;
import com.elice.spatz.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ServerUserDto {
    private Servers serverId;

    private Users user;


    public ServerUser toEntity(){
        return ServerUser.builder()
                .server(this.serverId)
                .user(this.user)
                .build();
    }
}
