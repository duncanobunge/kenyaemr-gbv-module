package org.openmrs.module.gbv.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.openmrs.api.db.hibernate.DbSessionFactory;  
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.gbv.domain.GbvComponentState;
import org.openmrs.module.gbv.domain.GbvComponentType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GbvAllComponentsState {

    private static Object lockObject = new Object();

    protected final Log log = LogFactory.getLog(getClass());

	private DbSessionFactory sessionFactory;

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void setComponentState(String componentId, GbvComponentType type, boolean enabled) {
        log.debug("Setting Component State : componentId = " + componentId + " type = " + type + " enabled " + enabled);

        GbvComponentState componentState;
        synchronized (lockObject) {
            componentState = getComponentState(componentId, type);

            if (componentState == null) {
                componentState = new GbvComponentState(componentId, type, enabled);
            } else {
                componentState.setEnabled(enabled);
            }

            sessionFactory.getCurrentSession().saveOrUpdate(componentState);
        }
    }

    public GbvComponentState getComponentState(String componentId, GbvComponentType type) {
        log.debug("Fetching Component State : componentId = " + componentId + " type = " + type);

        return getComponentStateFromDB(componentId, type);
    }

    private GbvComponentState getComponentStateFromDB(String componentId, GbvComponentType type) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GbvComponentState.class);
        criteria.add(Restrictions.eq("componentId", componentId));
        criteria.add(Restrictions.eq("componentType", type));
        return (GbvComponentState) criteria.uniqueResult();
    }
}
