package com.example.hardwaremonitor.controller;

import com.example.hardwaremonitor.service.NativeHardwareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hardware")
public class NativeHardwareController {

    private final NativeHardwareService hardwareService;

    public NativeHardwareController(NativeHardwareService hardwareService) {
        this.hardwareService = hardwareService;
    }

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        return hardwareService.getMetrics();
    }
}
