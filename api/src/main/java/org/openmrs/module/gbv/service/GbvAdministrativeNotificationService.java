package org.openmrs.module.gbv.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.gbv.domain.GbvAdministrativeNotification;

import java.util.List;

/**
 * Used to get {@link GbvAdministrativeNotification}s
 */
public interface GbvAdministrativeNotificationService extends OpenmrsService {

    List<GbvAdministrativeNotification> getAdministrativeNotifications();

}
