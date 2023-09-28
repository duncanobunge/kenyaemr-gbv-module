package org.openmrs.module.gbv.service;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.gbv.domain.GbvAdministrativeNotification;
import org.openmrs.module.gbv.factory.GbvAdministrativeNotificationProducer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class GbvAdministrativeNotificationServiceImpl extends BaseOpenmrsService implements GbvAdministrativeNotificationService {

    @Autowired(required = false)
    private List<GbvAdministrativeNotificationProducer> administrativeNotificationProducers;
    
    @Override
    public List<GbvAdministrativeNotification> getAdministrativeNotifications() {
        List<GbvAdministrativeNotification> ret = new ArrayList<GbvAdministrativeNotification>();
        if (administrativeNotificationProducers != null) {
            for (GbvAdministrativeNotificationProducer producer : administrativeNotificationProducers) {
                List<GbvAdministrativeNotification> notifications = producer.generateNotifications();
                if (notifications != null) {
                    ret.addAll(notifications);
                }
            }
        }
        return ret;
    }

    public void setAdministrativeNotificationProducers(List<GbvAdministrativeNotificationProducer> administrativeNotificationProducers) {
        this.administrativeNotificationProducers = administrativeNotificationProducers;
    }
}
