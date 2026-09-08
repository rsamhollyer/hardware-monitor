package com.example.hardwaremonitor.service;

import com.sun.management.OperatingSystemMXBean;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class NativeHardwareService {

    private final OperatingSystemMXBean osBean;

    public NativeHardwareService(OperatingSystemMXBean osBean) {
        this.osBean = osBean;
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("cpu", getCpuUsage());
        metrics.put("memory", getMemoryUsage());
        metrics.put("disk", getDiskUsage());

        return metrics;
    }

    private @NonNull Map<String, Object> getCpuUsage() {
        Map<String, Object> cpu = new HashMap<>();

        cpu.put("systemCpuLoad", osBean.getCpuLoad() * 100);
        cpu.put("processCpuLoad", osBean.getProcessCpuLoad() * 100);
        cpu.put("availableProcessors", osBean.getAvailableProcessors());

        return cpu;
    }

    private @NonNull Map<String, Object> getMemoryUsage() {
        Map<String, Object> mem = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"))) {
            String line;
            long memTotal = 0;
            long memAvailable = 0;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    memTotal = lineReplace(line);
                } else if (line.startsWith("MemAvailable:")) {
                    memAvailable = lineReplace(line);
                    break;
                }
            }
            mem.put("totalKb", memTotal);
            mem.put("availableKb", memAvailable);
            mem.put("usedKb", memTotal - memAvailable);
        } catch (Exception e) {
            mem.put("error", "Failed to read:" + e.getMessage());
        }

        return mem;
    }

    private @NonNull Long lineReplace(@NonNull String line) {
        return Long.parseLong(line.replaceAll("[^0-9]", ""));
    }

    private @NonNull Map<String, Object> getDiskUsage() {
        Map<String, Object> disk = new HashMap<>();
        File root = new File("/");

        disk.put("totalBytes", root.getTotalSpace());
        disk.put("freeBytes", root.getFreeSpace());
        disk.put("usableBytes", root.getUsableSpace());

        return disk;
    }
}
