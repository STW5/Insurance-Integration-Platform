package com.stw.insuranceintegrationplatform.interfaceconfig.presentation;

import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping
    public ResponseEntity<List<InterfaceSummaryResponse>> list() {
        return ResponseEntity.ok(interfaceService.list());
    }
}
