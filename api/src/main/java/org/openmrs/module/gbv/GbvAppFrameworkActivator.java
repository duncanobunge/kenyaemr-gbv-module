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
package org.openmrs.module.gbv;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.gbv.config.GbvAppFrameworkConfig;
import org.openmrs.module.gbv.domain.AppDescriptor;
import org.openmrs.module.gbv.domain.GbvAppTemplate;
import org.openmrs.module.gbv.domain.GbvExtension;
import org.openmrs.module.gbv.factory.GbvAppConfigurationLoaderFactory;
import org.openmrs.module.gbv.factory.GbvAppFrameworkFactory;
import org.openmrs.module.gbv.repository.GbvAllAppDescriptors;
import org.openmrs.module.gbv.repository.GbvAllAppTemplates;
import org.openmrs.module.gbv.repository.GbvAllFreeStandingExtensions;
import org.openmrs.module.gbv.service.GbvAppFrameworkService;

import java.util.Iterator;
import java.util.List;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class GbvAppFrameworkActivator extends BaseModuleActivator implements ModuleActivator {

    private Log log = LogFactory.getLog(getClass());

	/**
	 * @see ModuleActivator#contextRefreshed()
	 * @should set all available apps on {@link GbvAppFrameworkService}
	 * @should create privileges for all available apps
	 */
	public void contextRefreshed() {
        // I dislike this way of pulling in services and components. Ideally they should be pulled in
        // using proper IOC. It is not possible for this class since the activator of each module is created
        // in openmrs-core via newInstance() which doesn't instantiate it in a spring way. This should
        // later be changed.

        // configuration file used to store various configuration properties
        GbvAppFrameworkConfig config = Context.getRegisteredComponent("appFrameworkConfig", GbvAppFrameworkConfig.class);
        config.refreshContext();

        List<GbvAppFrameworkFactory> appFrameworkFactories = Context.getRegisteredComponents(GbvAppFrameworkFactory.class);
        GbvAllAppTemplates allAppTemplates = Context.getRegisteredComponents(GbvAllAppTemplates.class).get(0);
        GbvAllAppDescriptors allAppDescriptors = Context.getRegisteredComponents(GbvAllAppDescriptors.class).get(0);
        GbvAllFreeStandingExtensions allFreeStandingExtensions = Context.getRegisteredComponents(GbvAllFreeStandingExtensions.class).get(0);

        if (!config.getLoadAppsFromClasspath()) {
            disableDefaultConfigurationFactory(appFrameworkFactories);
        }

        registerAppsAndExtensions(appFrameworkFactories, allAppTemplates, allAppDescriptors, allFreeStandingExtensions, config);

    }

    public void registerAppsAndExtensions(List<GbvAppFrameworkFactory> appFrameworkFactories, GbvAllAppTemplates allAppTemplates,
                                          GbvAllAppDescriptors allAppDescriptors, GbvAllFreeStandingExtensions allFreeStandingExtensions,
                                          GbvAppFrameworkConfig config) {
        allAppTemplates.clear();
        allAppDescriptors.clear();
        allFreeStandingExtensions.clear();
        for (GbvAppFrameworkFactory appFrameworkFactory : appFrameworkFactories) {

            try {
                List<GbvAppTemplate> appTemplates = appFrameworkFactory.getAppTemplates();
                if (appTemplates != null) {
                    allAppTemplates.add(appTemplates);
                }

                List<AppDescriptor> appDescriptors = appFrameworkFactory.getAppDescriptors();
                if (appDescriptors != null) {
                    allAppDescriptors.add(appDescriptors);
                }

                List<GbvExtension> extensions = appFrameworkFactory.getExtensions();
                if (extensions != null) {
                    allFreeStandingExtensions.add(extensions);
                }

                allAppDescriptors.setAppTemplatesOnInstances(allAppTemplates);
                allAppDescriptors.setExtensionApps();
            }
            catch (Exception e) {
                log.error("Error loading app framework. Some apps might not work." + appFrameworkFactory, e);
            }
        }
    }

    @Override
    public void started() {
        setupLoginLocationTag(Context.getLocationService());
        super.started();
    }

    public void setupLoginLocationTag(LocationService locationService) {
        LocationTag supportsLogin = locationService.getLocationTagByName(GbvAppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN);
        if (supportsLogin == null) {
            supportsLogin = new LocationTag();
            supportsLogin.setName(GbvAppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN);
            supportsLogin.setDescription(GbvAppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_DESCRIPTION);
            supportsLogin.setUuid(GbvAppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID);
            locationService.saveLocationTag(supportsLogin);
        }
    }

    private void disableDefaultConfigurationFactory(List<GbvAppFrameworkFactory> appFrameworkFactories) {
        Iterator i = appFrameworkFactories.iterator();
        while (i.hasNext()) {
            if (i.next().getClass().getCanonicalName().equals(GbvAppConfigurationLoaderFactory.class.getCanonicalName())) {
                i.remove();
            }
        }

    }


}
