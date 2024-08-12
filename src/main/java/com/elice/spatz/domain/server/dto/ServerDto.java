package com.elice.spatz.domain.server.dto;

import com.elice.spatz.domain.server.entity.Servers;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ServerDto {
    private Long id;
    private String name;

    public Servers toEntity(){
            return Servers.builder()
                    .id(this.id)
                    .name(this.name)
                    .build();
    }
}
