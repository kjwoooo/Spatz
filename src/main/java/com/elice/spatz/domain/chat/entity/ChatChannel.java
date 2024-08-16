package com.elice.spatz.domain.chat.entity;

import com.elice.spatz.domain.file.entity.File;
import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatChannel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Servers server;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private List<File> files;
}
