package edu.utsa.cs3443.boxinggymapp.configuration;

import edu.utsa.cs3443.boxinggymapp.service.GymMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final GymMemberService gymMemberService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledPaymentCheck() {
        gymMemberService.checkAndUpdatePayments();
    }
}

