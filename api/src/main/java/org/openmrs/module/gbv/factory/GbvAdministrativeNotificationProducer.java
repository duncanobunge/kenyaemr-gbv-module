package org.openmrs.module.gbv.factory;

import org.openmrs.module.gbv.domain.GbvAdministrativeNotification;

import java.util.List;

/**
 * Modules that want to produce {@link GbvAdministrativeNotification}s should register a bean implementing this interface
 */
public interface GbvAdministrativeNotificationProducer {

    List<GbvAdministrativeNotification> generateNotifications();

}
