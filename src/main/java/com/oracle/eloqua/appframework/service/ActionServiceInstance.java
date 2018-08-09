package com.oracle.eloqua.appframework.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.oracle.eloqua.appframework.bulkapi.EloquaApiConnector;
import com.oracle.eloqua.appframework.bulkapi.EloquaBulkApi;
import com.oracle.eloqua.appframework.entity.InstanceConfiguration;
import com.oracle.eloqua.appframework.entity.SMSActivity;
import com.oracle.eloqua.appframework.enums.RecordStatus;
import com.oracle.eloqua.appframework.repository.SMSActivityRepository;
import com.oracle.eloqua.appframework.util.Util;

public class ActionServiceInstance {

	private SMSActivityRepository smsActivityRepository;

	private static final Logger log = LoggerFactory.getLogger(ActionServiceInstance.class);

	private EloquaBulkApi eloquaBulkApi;

	private EloquaApiConnector eloquaApiConnector;

	private InstanceConfiguration instanceConfiguration;

	public void setup(EloquaBulkApi eloquaBulkApi, EloquaApiConnector eloquaApiConnector,
			InstanceConfiguration instanceConfiguration) {
		this.eloquaBulkApi = eloquaBulkApi;

		this.instanceConfiguration = instanceConfiguration;

		this.eloquaApiConnector = eloquaApiConnector;
	}

	private JSONObject bulkApiDefinition(String status, int executionId) {
		String instanceIdNoDash = instanceConfiguration.getInstanceId().replace("-", "");

		String definitionName = String.format("SMS Action Bulk Import [Instance=%s, result=%s]",
				instanceConfiguration.getInstanceId(), status);
		try {
			JSONObject body = new JSONObject();
			JSONObject fields = new JSONObject();
			JSONObject syncActions = new JSONObject();

			fields.put("contactId", "{{Contact.Id}}");

			syncActions.put("destination",
					"{{ActionInstance(" + instanceIdNoDash + ").Execution[" + executionId + "]}}");
			syncActions.put("action", "setStatus");
			syncActions.put("status", status);

			JSONArray syncActionsArray = new JSONArray();
			syncActionsArray.put(syncActions);

			body.put("name", definitionName);
			body.put("fields", fields);
			body.put("syncActions", syncActionsArray);
			body.put("identifierFieldName", "contactId");

			return body;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void configure(String smsBody) {
		log.info("Configure Action Service");
		log.info("SMS Body: " + smsBody);
	}

	public void notify(int campaignId, Integer executionId, String notifyBody) {

		try {
			String campaignDetailsString = eloquaApiConnector.doRest("2.0", "assets/campaign/" + campaignId,
					instanceConfiguration.getInstallId(), HttpMethod.GET, null);

			JSONObject campaignDetails = new JSONObject(campaignDetailsString);

			String campaignRegion = campaignDetails.getString("region");
			String campaignName = campaignDetails.getString("name");

			JSONObject json = new JSONObject(notifyBody);

			JSONArray items = json.getJSONArray("items");

			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);

				SMSActivity smsActivity = new SMSActivity();
				smsActivity.setInstanceId(instanceConfiguration.getInstanceId());
				smsActivity.setContactId(item.getString("ContactID"));
				smsActivity.setEmailAddress(item.getString("EmailAddress"));
				smsActivity.setMobileNumber(item.getString("Mobile"));
				smsActivity.setMessage(instanceConfiguration.getSmsBody());
				smsActivity.setStatus(RecordStatus.NEW);
				smsActivity.setCampaignName(campaignName);
				smsActivity.setCampaignRegion(campaignRegion);
				smsActivity.setExecutionId(executionId);

				smsActivityRepository.save(smsActivity);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void checkForNewEntrants() {

		try {
			log.debug("Checking entries for instance: " + instanceConfiguration.getInstanceId());

			List<SMSActivity> smsActivities = smsActivityRepository
					.findByInstanceIdAndStatus(instanceConfiguration.getInstanceId(), RecordStatus.SMS_SENT);
			if (smsActivities.size() > 0) {
				doBulkUpload(smsActivities);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void doBulkUpload(List<SMSActivity> smsActivities) throws JSONException {

		if (smsActivities.size() > 0) {
			JSONObject bulkApiDefinition = bulkApiDefinition("complete", smsActivities.get(0).getExecutionId());

			JSONArray bulkApiPayload = new JSONArray();

			for (SMSActivity smsActivity : smsActivities) {
				smsActivity.setStatus(RecordStatus.UPLOAD_IN_PROGRESS);
				smsActivityRepository.save(smsActivity);
				log.info("Processing SMS Activity: " + smsActivity.toString());

				JSONObject jsonEmail = new JSONObject();
				jsonEmail.put("contactId", smsActivity.getContactId());
				bulkApiPayload.put(jsonEmail);

			}

			eloquaBulkApi.doUpload(bulkApiDefinition.toString(), bulkApiPayload.toString(), "contacts/imports",
					instanceConfiguration.getInstallId());

			for (SMSActivity smsActivity : smsActivities) {
				smsActivity.setStatus(RecordStatus.UPLOAD_COMPLETE);
				smsActivityRepository.save(smsActivity);

			}

		}

	}

	public void stop() {
		log.info("Service stop. InstanceId=" + instanceConfiguration.getInstanceId());
		// do anything here to prepare the service for shutdown
	}

	public JSONObject buildCreateResponse(boolean requiresConfiguration) {

		try {

			JSONObject recordDefinition = new JSONObject();
			recordDefinition.put("ContactID", "{{Contact.Id}}");
			recordDefinition.put("EmailAddress", "{{Contact.Field(C_EmailAddress)}}");
			recordDefinition.put("Mobile", "{{Contact.Field(C_MobilePhone)}}");

			JSONObject createResponse = new JSONObject();
			createResponse.put("recordDefinition", recordDefinition);
			createResponse.put("requiresConfiguration", requiresConfiguration);

			Util.logPrettyJson(log, "Create Response:", createResponse);

			return createResponse;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void activated() {
		// TODO Any processes when the service is activated
		log.info("Instance activated. InstanceId=" + instanceConfiguration.getInstanceId());

	}

	public void draft() {
		// TODO Any processes when the service is changed to draft status
		log.info("Instance draft. InstanceId=" + instanceConfiguration.getInstanceId());

	}

	public SMSActivityRepository getCampaignEntryRepository() {
		return smsActivityRepository;
	}

	public void setCampaignEntryRepository(SMSActivityRepository campaignEntryRepository) {
		this.smsActivityRepository = campaignEntryRepository;
	}

	public EloquaBulkApi getEloquaBulkApi() {
		return eloquaBulkApi;
	}

	public void setEloquaBulkApi(EloquaBulkApi eloquaBulkApi) {
		this.eloquaBulkApi = eloquaBulkApi;
	}

	public static Logger getLog() {
		return log;
	}

	public InstanceConfiguration getInstanceConfiguration() {
		return instanceConfiguration;
	}

	public void setInstanceConfiguration(InstanceConfiguration instanceConfiguration) {
		this.instanceConfiguration = instanceConfiguration;

		// set if the service requires configuration

		boolean requiresConfiguration = !configured();

		JSONObject putBody = buildCreateResponse(requiresConfiguration);

		eloquaApiConnector.doCloud("1.0", "actions/instances/" + instanceConfiguration.getInstanceId(),
				instanceConfiguration.getInstallId(), HttpMethod.PUT, putBody.toString());

	}

	public boolean isInstance(String instanceId) {
		return instanceId.equals(instanceConfiguration.getInstanceId());
	}

	public boolean configured() {
		if (Util.nullOrEmpty(instanceConfiguration.getSmsBody())) {
			log.info("Service not fully configured");
			return false;
		} else {
			log.info("Service fully configured");
			return true;
		}

	}

}
