package com.example.hardwaremonitor.controller;

import com.example.hardwaremonitor.service.SystemSpecsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hardware")
public class NativeHardwareController {

    private final SystemSpecsService systemSpecsService;

    public NativeHardwareController(SystemSpecsService systemSpecsService) {
        this.systemSpecsService = systemSpecsService;
    }

    @GetMapping("/metrics")
    public Map<String, Object> getSystemFetch() {
        return systemSpecsService.getSystemFetch();
    }
}
