package com.example.hardwaremonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@Configuration
public class HardwareConfig {

    @Bean
    public OperatingSystemMXBean operatingSystemMXBean() {
        return ManagementFactory.getOperatingSystemMXBean();
    }

}
