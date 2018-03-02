package com.oracle.eloqua.appframework.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.oracle.eloqua.appframework.entity.InstanceConfiguration;
import com.oracle.eloqua.appframework.enums.ServiceType;

public interface InstanceRepository extends CrudRepository<InstanceConfiguration, String> {

	List<InstanceConfiguration> findByServiceType(ServiceType serviceType);

}