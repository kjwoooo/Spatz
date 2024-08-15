package com.elice.spatz.domain.server.dto;

import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.serverUser.entity.ServerUser;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ServerDto {
    private Long id;
    private String name;
    private String inviteCode;
    private List<ServerUser> serverUsers;

    public Servers toEntity(){
            return Servers.builder()
                    .id(this.id)
                    .name(this.name)
                    .inviteCode(this.inviteCode)
                    .serverUsers(this.serverUsers)
                    .build();
    }
}
