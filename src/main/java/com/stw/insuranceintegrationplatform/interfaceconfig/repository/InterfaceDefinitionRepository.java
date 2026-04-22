package com.stw.insuranceintegrationplatform.interfaceconfig.repository;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InterfaceDefinitionRepository extends JpaRepository<InterfaceDefinitionEntity, String>, JpaSpecificationExecutor<InterfaceDefinitionEntity> {
    List<InterfaceDefinitionEntity> findByActiveTrue();
}
