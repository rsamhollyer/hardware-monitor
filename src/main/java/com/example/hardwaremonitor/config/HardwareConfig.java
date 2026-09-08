package com.example.hardwaremonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@Configuration
public class HardwareConfig {

    @Bean
    public OperatingSystemMXBean operatingSystemMXBean() {
        return ManagementFactory.getOperatingSystemMXBean();
    }

    @Bean
    public FileSystem fileSystem() {
        return FileSystems.getDefault();
    }
}
