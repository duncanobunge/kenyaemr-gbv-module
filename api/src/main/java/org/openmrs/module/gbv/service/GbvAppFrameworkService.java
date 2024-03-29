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
package org.openmrs.module.gbv.service;

import org.openmrs.Location;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.gbv.context.GbvAppContextModel;
import org.openmrs.module.gbv.domain.AppDescriptor;
import org.openmrs.module.gbv.domain.GbvAppTemplate;
import org.openmrs.module.gbv.domain.GbvExtension;
import org.openmrs.module.gbv.domain.GbvRequireable;
import org.openmrs.module.gbv.domain.GbvUserApp;

import java.util.List;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(AppFrameworkService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
public interface GbvAppFrameworkService extends OpenmrsService {
	
	List<AppDescriptor> getAllApps();

	List<GbvExtension> getAllExtensions(String extensionPointId);

    List<GbvExtension> getExtensionsById(String extensionPointId, String id);
	
	List<AppDescriptor> getAllEnabledApps();
	
	/**
	 * Gets all enabled extensions for the specified extensionPointId
	 * 
	 * @param extensionPointId the extensionPointId to match against
	 * @return a list of Extensions
	 * @should get all extensions for the specified extensionPointId
	 */
	List<GbvExtension> getAllEnabledExtensions(String extensionPointId);

	List<GbvExtension> getAllEnabledExtensions();

	void enableApp(String appId);
	
	void disableApp(String appId);
	
	void enableExtension(String extensionId);
	
	void disableExtension(String extensionId);
	
	/**
	 * Gets all enabled extensions for the currently logged in user
	 * 
	 * @return a list of Extensions
	 * @should get all enabled extensions for the currently logged in user
     * @should return extensions with no required privilege if there is no authenticated user
	 */
	List<GbvExtension> getExtensionsForCurrentUser();
	
	/**
	 * Gets all enabled extensions for the currently logged in user for the specified
	 * extensionPointId
	 * 
	 * @param extensionPointId the extension point id to match against
	 * @return a list of Extensions
	 * @should get all enabled extensions for the logged in user and extensionPointId
	 */
	List<GbvExtension> getExtensionsForCurrentUser(String extensionPointId);

    /**
     * Gets enabled extensions points for the currently logged in user, for the specified extensionPointId. If any
     * extension has a "require" attribute, and contextModel is not null, we evaluate the extension's require string
     * against contextModel.
     * @param extensionPointId
     * @param contextModel
     * @return
     * @should get enabled extensions for the current user whose require property matches the contextModel
     */
    List<GbvExtension> getExtensionsForCurrentUser(String extensionPointId, GbvAppContextModel appContextModel);

    /**
	 * Gets all enabled apps for the currently logged in user
	 * 
	 * @return a list of Extensions
	 * @should get all enabled apps for the currently logged in user
     * @should return apps with no required privilege if there is no authenticated user
	 */
	List<AppDescriptor> getAppsForCurrentUser();

    /**
     * Gets all non-retired locations that are tagged as supporting logins.
     * @see org.openmrs.module.gbv.GbvAppFrameworkConstants#LOCATION_TAG_SUPPORTS_LOGIN
     * @return all locations that you can choose as a sessionLocation when logging in
     */
    List<Location> getLoginLocations();

    /**
     * @return all app templates
     */
    List<GbvAppTemplate> getAllAppTemplates();

    /**
     * Gets an app template by its id
     * @param id
     * @return
     */
    GbvAppTemplate getAppTemplate(String id);

    /**
     * Gets an app by its id
     * @param id
     * @return
     * @should return a user app that matches the specified id
     */
    AppDescriptor getApp(String id);

    /**
     * Gets a UserApp that matches the specified appId
     *
     * @since 2.3
     * @param appId
     * @return UserApp
     * @should return a user app that matches the specified appId
     */
    GbvUserApp getUserApp(String appId);

    /**
     * Gets all UserApps
     *
     * @since 2.3
     * @return List<UserApp>
     * @should return a list of UserApps
     */
    List<GbvUserApp> getUserApps();

    /**
     * Saves a new UserApp to the database or updates it in the DB in case it is an existing one
     *
     * @since 2.3
     * @param userApp the UserApp to save
     * @return UserApp
     * @should save the user app to the database and update the list of loaded apps
     */
    GbvUserApp saveUserApp(GbvUserApp userApp);

    /**
     * Deletes the UserApp from the database
     *
     * @since 2.3
     * @param userApp the UserApp to purge
     * @should remove the user app from the database and update the list of loaded apps
     */
    void purgeUserApp(GbvUserApp userApp);

	/**
	 * Checks whether a Requireable object should be included in the current context
	 *
	 * @since 2.15.0
	 * @param candidate the requireable object to check
	 * @param contextModel the current context to check against
	 * @return true if the requireable should be included in this context; false otherwise
	 */
	boolean checkRequireExpression(GbvRequireable candidate, GbvAppContextModel contextModel);
}
