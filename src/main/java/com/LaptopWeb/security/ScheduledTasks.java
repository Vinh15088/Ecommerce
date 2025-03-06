package com.LaptopWeb.security;

import com.LaptopWeb.repository.InvalidatedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@EnableScheduling
public class ScheduledTasks {
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 3600000)
    public void removeExpiredTokens() {
        log.info("The time is now {}", dateFormat.format(new Date()) + " - Remove expired Token");
        var listInvalidatedToken = invalidatedTokenRepository.findAll();

        for(var token:listInvalidatedToken) {
            if(token.getExpiryTime().before(new Date())) {
                invalidatedTokenRepository.delete(token);
            }
        }
    }
}
