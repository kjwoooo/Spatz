package com.elice.spatz.domain.userfeature.service;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.domain.userfeature.entity.BannedUser;
import com.elice.spatz.domain.userfeature.repository.BannedUserRepository;
import com.elice.spatz.domain.userfeature.repository.ReportCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class ReactiveBannedUserScheduler {
    private final BannedUserRepository bannedUserRepository;
    private final ReportCountRepository reportCountRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void reactiveBannedUsers() {
        LocalDateTime now = LocalDateTime.now().minusSeconds(1);

        List<BannedUser> bannedUserList = bannedUserRepository.findAllByBannedEnd(now);

        bannedUserList.stream()
                .forEach(bannedUser -> {
                    Users user = bannedUser.getUser();

                    bannedUserRepository.delete(bannedUser);
                    if(user.getReportCount() != null){
                        reportCountRepository.delete(user.getReportCount());
                    }
                    user.setBannedUser(null);
                    user.setReportCount(null);
                    userRepository.save(user);
        });
    }
}

