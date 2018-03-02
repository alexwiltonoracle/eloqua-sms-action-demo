package com.oracle.eloqua.appframework.repository;

import org.springframework.data.repository.CrudRepository;

import com.oracle.eloqua.appframework.entity.AppInstallation;

public interface AppInstallationRepository extends CrudRepository<AppInstallation, String> {

}