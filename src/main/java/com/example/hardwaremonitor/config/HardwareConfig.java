package com.example.hardwaremonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.nativefree.SystemInfo;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Configuration
public class HardwareConfig {

    @Bean
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }

    @Bean
    public FileSystem fileSystem() {
        return FileSystems.getDefault();
    }

    @Bean(name = "hardwareTaskExecutor")
    public Executor hardwareTaskExecutor() {
        return Executors.newFixedThreadPool(4, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("systemspec-fetch-worker-");
            thread.setDaemon(true);
            return thread;
        });
    }
}
