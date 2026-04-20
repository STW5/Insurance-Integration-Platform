package com.stw.insuranceintegrationplatform.interfaceconfig.repository;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterfaceDefinitionRepository extends JpaRepository<InterfaceDefinitionEntity, String> {
    List<InterfaceDefinitionEntity> findByActiveTrue();
}
