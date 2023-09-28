/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.gbv.repository;

import java.util.List;

import org.openmrs.api.db.hibernate.DbSessionFactory;  
import org.openmrs.module.gbv.domain.GbvUserApp;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GbvAllUserApps {
	
	private DbSessionFactory sessionFactory;
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	public GbvUserApp getUserApp(String appId) {
		return (GbvUserApp) sessionFactory.getCurrentSession().get(GbvUserApp.class, appId);
	}
	
	@Transactional
	public GbvUserApp saveUserApp(GbvUserApp userApp) {
		sessionFactory.getCurrentSession().saveOrUpdate(userApp);
		return userApp;
	}
	
	@Transactional(readOnly = true)
	public List<GbvUserApp> getUserApps() {
		return sessionFactory.getCurrentSession().createCriteria(GbvUserApp.class).list();
	}
	
	@Transactional
	public void deleteUserApp(GbvUserApp userApp) {
		sessionFactory.getCurrentSession().delete(userApp);
	}
	
}
