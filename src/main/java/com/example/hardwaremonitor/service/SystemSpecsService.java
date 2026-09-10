package com.example.hardwaremonitor.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.nativefree.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class SystemSpecsService {

    private final SystemInfo systemInfo;
    private final FileSystem fileSystem;
    private final Executor executor;

    public SystemSpecsService(
            SystemInfo systemInfo,
            FileSystem fileSystem,
            @Qualifier("hardwareTaskExecutor") Executor executor) {

        this.systemInfo = systemInfo;
        this.fileSystem = fileSystem;
        this.executor = executor;
    }

    @Cacheable(value = "fastfetch")
    public Map<String, Object> getSystemFetch() {
        Map<String, Object> metrics = new HashMap<>();

        CompletableFuture<Object> osFuture = CompletableFuture.supplyAsync(this::getOSData, executor);
        CompletableFuture<Object> cpuFuture = CompletableFuture.supplyAsync(this::getCpuData, executor);
        CompletableFuture<Object> gpuFuture = CompletableFuture.supplyAsync(this::getGPUData, executor);
        CompletableFuture<Object> memFuture = CompletableFuture.supplyAsync(this::getMemoryData, executor);
        CompletableFuture<Object> diskFuture = CompletableFuture.supplyAsync(this::getDiskData, executor);


        CompletableFuture.allOf(osFuture, cpuFuture, gpuFuture, memFuture, diskFuture).join();

        try {
            metrics.put("os", osFuture.get());
            metrics.put("cpu", cpuFuture.get());
            metrics.put("gpu", gpuFuture.get());
            metrics.put("memory", memFuture.get());
            metrics.put("disk", diskFuture.get());
        } catch (Exception e) {
            metrics.put("error", "Parallel fetch execution interrupted: " + e.getMessage());
        }

        return metrics;
    }

    private @NonNull Map<String, Object> getOSData() {
        Map<String, Object> osData = new HashMap<>();
        OperatingSystem os = systemInfo.getOperatingSystem();

        osData.put("distro", os.getFamily());
        osData.put("kernel", os.getVersionInfo().getBuildNumber());

        return osData;
    }

    private @NonNull Map<String, Object> getCpuData() {
        Map<String, Object> cpuData = new HashMap<>();
        CentralProcessor cpu = systemInfo.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier cpuId = cpu.getProcessorIdentifier();

        cpuData.put("arch", cpuId.getMicroarchitecture());
        cpuData.put("name", cpuId.getName());
        cpuData.put("freq", cpu.getMaxFreq());

        return cpuData;
    }

    private @NonNull Map<String, Object> getGPUData() {
        Map<String, Object> gpuData = new HashMap<>();
        GraphicsCard gpuInfo = systemInfo.getHardware().getGraphicsCards().getFirst();

        gpuData.put("name", gpuInfo.getName());
        gpuData.put("vram", gpuInfo.getVRam());

        return gpuData;
    }

    private @NonNull Map<String, Object> getMemoryData() {
        Map<String, Object> memData = new HashMap<>();
        GlobalMemory mem = systemInfo.getHardware().getMemory();
        long total = mem.getTotal();
        long unUsed = mem.getAvailable();
        long used = total - unUsed;

        memData.put("total", total);
        memData.put("used", used);

        return memData;
    }

    private @NonNull Map<String, Object> getDiskData() {
        Map<String, Object> diskData = new HashMap<>();

        try {
            FileStore store = Files.getFileStore(fileSystem.getPath("/"));
            diskData.put("total", store.getTotalSpace());
            diskData.put("free", store.getUnallocatedSpace());

        } catch (Exception e) {
            diskData.put("diskError", e.getMessage());
        }

        return diskData;
    }
}
