package com.stw.insuranceintegrationplatform.interfaceconfig.presentation;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interfaces")
public class InterfaceController {
    private final InterfaceService interfaceService;

    public InterfaceController(InterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @PostMapping
    public ResponseEntity<InterfaceSummaryResponse> register(@Valid @RequestBody RegisterInterfaceRequest request) {
        return ResponseEntity.ok(interfaceService.register(request));
    }

    @PutMapping("/{interfaceCode}")
    public ResponseEntity<InterfaceSummaryResponse> update(
            @PathVariable String interfaceCode,
            @Valid @RequestBody UpdateInterfaceRequest request
    ) {
        return ResponseEntity.ok(interfaceService.update(interfaceCode, request));
    }

    @GetMapping
    public ResponseEntity<List<InterfaceSummaryResponse>> list(
            @RequestParam(required = false) ProtocolType protocolType,
            @RequestParam(required = false) String targetInstitution,
            @RequestParam(required = false) InterfaceHealthStatus healthStatus,
            @RequestParam(required = false) Boolean active
    ) {
        return ResponseEntity.ok(interfaceService.list(protocolType, targetInstitution, healthStatus, active));
    }

    @GetMapping("/{interfaceCode}")
    public ResponseEntity<InterfaceSummaryResponse> get(@PathVariable String interfaceCode) {
        return ResponseEntity.ok(interfaceService.get(interfaceCode));
    }
}
