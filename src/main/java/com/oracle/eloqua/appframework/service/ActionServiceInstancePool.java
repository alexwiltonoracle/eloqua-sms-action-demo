package com.oracle.eloqua.appframework.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oracle.eloqua.appframework.bulkapi.EloquaBulkApi;
import com.oracle.eloqua.appframework.entity.InstanceConfiguration;
import com.oracle.eloqua.appframework.enums.InstanceStatus;
import com.oracle.eloqua.appframework.enums.ServiceType;
import com.oracle.eloqua.appframework.repository.SMSActivityRepository;
import com.oracle.eloqua.appframework.repository.InstanceRepository;

@Component
public class ActionServiceInstancePool {

	private static final Logger log = LoggerFactory.getLogger(ActionServiceInstancePool.class);

	@Autowired
	private SMSActivityRepository campaignEntryRepository;

	@Autowired
	InstanceRepository instanceConfigurationRepository;

	@Autowired
	protected EloquaBulkApi eloquaBulkApi;

	protected List<ActionServiceInstance> serviceInstances = new ArrayList<ActionServiceInstance>();

	private ActionServiceInstance checkAndBuildActionServiceInstance(InstanceConfiguration instanceConfiguration) {

		ActionServiceInstance serviceInstance = findServiceInstance(instanceConfiguration.getInstanceId());

		if (serviceInstance == null) {

			serviceInstance = buildServiceInstance();
			serviceInstance.setup(eloquaBulkApi, instanceConfiguration);

			serviceInstances.add(serviceInstance);

		} else {
			log.info("...live instance exists, do not create");

		}

		return serviceInstance;

	}

	public ActionServiceInstance findServiceInstance(String instanceId) {

		for (ActionServiceInstance serviceInstance : serviceInstances) {
			if (serviceInstance.isInstance(instanceId)) {
				return serviceInstance;
			}
		}

		return null;
	}

	public ActionServiceInstance create(String installId, String instanceId, int siteId, String userName) {
		InstanceConfiguration instanceConfiguration = new InstanceConfiguration(ServiceType.ACTION, InstanceStatus.LIVE, installId, instanceId, siteId,
				userName);
		instanceConfigurationRepository.save(instanceConfiguration);

		ActionServiceInstance result = checkAndBuildActionServiceInstance(instanceConfiguration);

		return result;
	}

	public void notification(String instanceId, String notifyBody) {
		ActionServiceInstance serviceInstance = findServiceInstance(instanceId);

		if (serviceInstance != null) {
			serviceInstance.notify(notifyBody);
		}

	}

	public void delete(String instanceId) {
		log.info("Delete Action Service");

		ActionServiceInstance serviceInstance = findServiceInstance(instanceId);
		if (serviceInstance != null) {
			log.info("Stopping service instance");
			serviceInstance.stop();
			serviceInstances.remove(instanceId);
		}

		InstanceConfiguration instance = instanceConfigurationRepository.findOne(instanceId);

		if (instance != null) {
			instance.setInstanceStatus(InstanceStatus.DELETED);
			log.info("Marking as deleted in DB: " + instance.toString());
			instanceConfigurationRepository.save(instance);
		} else {
			log.info("Cannot find instance to delete. instanceId=" + instanceId);
		}

	}

	public void startExisting() {
		log.info("Starting existing Action Service");
		List<InstanceConfiguration> instanceConfigurations = instanceConfigurationRepository.findByServiceType(ServiceType.ACTION);
		log.info("Total found: " + instanceConfigurations.size());
		for (InstanceConfiguration instanceConfiguration : instanceConfigurations) {
			log.info("Checking whether to start instance: " + instanceConfiguration.getInstanceId());
			if (instanceConfiguration.getInstanceStatus() != InstanceStatus.DELETED) {
				log.info("...creating instance from DB record: " + instanceConfiguration.toString());
				checkAndBuildActionServiceInstance(instanceConfiguration);
			} else {
				log.info("...running instance deleted, do not create");

			}

		}

	}

	protected ActionServiceInstance buildServiceInstance() {
		ActionServiceInstance f = new ActionServiceInstance();
		f.setCampaignEntryRepository(campaignEntryRepository);
		return f;
	}

	@Scheduled(fixedDelay = 5000)
	private void fireTimer() {

		for (ActionServiceInstance actionService : serviceInstances) {
			actionService.checkForNewEntrants();
		}

	}

	public String configure(Map<String, String[]> allRequestParams) {
		log.info("Configure Feeder Service");
		return "configForm";
	}

	public ActionServiceInstance saveConfiguration(String instanceId, String smsBody) {

		InstanceConfiguration instanceConfiguration = instanceConfigurationRepository.findOne(instanceId);
		instanceConfiguration.setSmsBody(smsBody);
		instanceConfiguration = instanceConfigurationRepository.save(instanceConfiguration);
				
		ActionServiceInstance actionService = findServiceInstance(instanceId);
		actionService.setInstanceConfiguration(instanceConfiguration);

		return actionService;
	}

}
