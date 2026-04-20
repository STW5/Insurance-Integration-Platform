package com.stw.insuranceintegrationplatform.interfaceconfig.repository;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterfaceDefinitionRepository extends JpaRepository<InterfaceDefinitionEntity, String> {
}
