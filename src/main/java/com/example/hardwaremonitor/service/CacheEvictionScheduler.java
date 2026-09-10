package com.example.hardwaremonitor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CacheEvictionScheduler {
    private final static Logger log = LoggerFactory.getLogger(CacheEvictionScheduler.class);

    @CacheEvict(value = "fastfetch", allEntries = true)
    @Scheduled(fixedRate = 500000)
    public void emptyFastfetchCache() {
        log.info("Flushing cache");
    }
}
