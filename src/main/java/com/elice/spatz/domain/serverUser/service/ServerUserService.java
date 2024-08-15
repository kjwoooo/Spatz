package com.elice.spatz.domain.serverUser.service;

import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.serverUser.repository.ServerUserRepository;
import com.elice.spatz.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerUserService {

//    private final ServerUserRepository serverUserRepository;
//    @Transactional
//    public List<Servers> getServersByUserId(Users user)
//    {
//        return serverUserRepository.findServersByUser(user);
//    }
}
