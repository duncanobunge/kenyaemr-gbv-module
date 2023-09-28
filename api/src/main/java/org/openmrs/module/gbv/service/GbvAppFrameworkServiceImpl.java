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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.gbv.GbvAppFrameworkActivator;
import org.openmrs.module.gbv.GbvAppFrameworkConstants;
import org.openmrs.module.gbv.GbvLoginLocationFilter;
import org.openmrs.module.gbv.config.GbvAppFrameworkConfig;
import org.openmrs.module.gbv.context.GbvAppContextModel;
import org.openmrs.module.gbv.domain.AppDescriptor;
import org.openmrs.module.gbv.domain.GbvAppTemplate;
import org.openmrs.module.gbv.domain.GbvComponentState;
import org.openmrs.module.gbv.domain.GbvComponentType;
import org.openmrs.module.gbv.domain.GbvExtension;
import org.openmrs.module.gbv.domain.GbvRequireable;
import org.openmrs.module.gbv.domain.GbvUserApp;
import org.openmrs.module.gbv.feature.GbvFeatureToggleProperties;
import org.openmrs.module.gbv.repository.GbvAllAppDescriptors;
import org.openmrs.module.gbv.repository.GbvAllAppTemplates;
import org.openmrs.module.gbv.repository.GbvAllComponentsState;
import org.openmrs.module.gbv.repository.GbvAllFreeStandingExtensions;
import org.openmrs.module.gbv.repository.GbvAllUserApps;
import org.springframework.transaction.annotation.Transactional;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It is a default implementation of {@link GbvAppFrameworkService}.
 */
public class GbvAppFrameworkServiceImpl extends BaseOpenmrsService implements GbvAppFrameworkService {

	private final Log log = LogFactory.getLog(getClass());

	private GbvAllAppTemplates allAppTemplates;

	private GbvAllAppDescriptors allAppDescriptors;

	private GbvAllFreeStandingExtensions allFreeStandingExtensions;

	private GbvAllComponentsState allComponentsState;

	private LocationService locationService;

	private GbvFeatureToggleProperties featureToggles;

	private GbvAppFrameworkConfig appFrameworkConfig;

	private ScriptEngine javascriptEngine;

	private GbvAllUserApps allUserApps;

	public GbvAppFrameworkServiceImpl(GbvAllAppTemplates allAppTemplates, GbvAllAppDescriptors allAppDescriptors,
	    GbvAllFreeStandingExtensions allFreeStandingExtensions, GbvAllComponentsState allComponentsState,
	    LocationService locationService, GbvFeatureToggleProperties featureToggles, GbvAppFrameworkConfig appFrameworkConfig,
	    GbvAllUserApps allUserApps) throws ScriptException {
		this.allAppTemplates = allAppTemplates;
		this.allAppDescriptors = allAppDescriptors;
		this.allFreeStandingExtensions = allFreeStandingExtensions;
		this.allComponentsState = allComponentsState;
		this.locationService = locationService;
		this.featureToggles = featureToggles;
		this.appFrameworkConfig = appFrameworkConfig;
		this.javascriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.allUserApps = allUserApps;

		if (javascriptEngine == null) {
			javascriptEngine = new jdk.nashorn.api.scripting.NashornScriptEngineFactory().getScriptEngine();
			javascriptEngine.setBindings(javascriptEngine.createBindings(), ScriptContext.GLOBAL_SCOPE);
		}

		// there is surely a cleaner way to define this utility function in the global scope
		this.javascriptEngine.eval("function hasMemberWithProperty(list, propName, val) { " + "if (!list) { return false; } "
		        + "var i, len=list.length; " + "for (i=0; i<len; ++i) { "
		        + "  if (list[i][propName] == val) { return true; } " + "} " + "return false; " + "}");
		Object hasMemberWithProperty = javascriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).get("hasMemberWithProperty");
		javascriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE).put("hasMemberWithProperty", hasMemberWithProperty);

		this.javascriptEngine.eval("function some(list, func) { " + "if (!list) { return false; } "
                + "var i, len=list.length; " + "for (i=0; i<len; ++i) { "
                + "  if (func(list[i]) === true) { return true; } " + "} " + "return false; " + "}");
        Object some = javascriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).get("some");
        javascriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE).put("some", some);

		this.javascriptEngine.eval("function fullMonthsBetweenDates(earlierDate, laterDate) { "
				+ "var d1 = new Date(earlierDate); "
				+ "var d2 = new Date(laterDate); "
				+ "var monthsBetween = ((d2.getFullYear() - d1.getFullYear()) * 12) + (d2.getMonth() - d1.getMonth()); "
				+ "if (d2.getDate() < d1.getDate()) { monthsBetween = monthsBetween - 1; } "
				+ "return monthsBetween; "
				+ "}");
		Object fullMonthsBetweenDates = javascriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).get("fullMonthsBetweenDates");
		javascriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE).put("fullMonthsBetweenDates", fullMonthsBetweenDates);
	}

	@Override
	public List<AppDescriptor> getAllApps() {
		return allAppDescriptors.getAppDescriptors();
	}

	@Override
	public List<GbvExtension> getExtensionsById(String extensionPointId, String id) {
		List<GbvExtension> matchingExtensions = new ArrayList<GbvExtension>();
		for (GbvExtension extension : getAllExtensions(extensionPointId)) {
			if (extension.getId().equalsIgnoreCase(id)) {
				matchingExtensions.add(extension);
			}
		}
		return matchingExtensions;
	}

	@Override
	// TODO shouldn't this be getAllFreeStandingExtensions?
	public List<GbvExtension> getAllExtensions(String extensionPointId) {
		List<GbvExtension> matchingExtensions = new ArrayList<GbvExtension>();
		for (GbvExtension extension : allFreeStandingExtensions.getExtensions()) {
			if (extensionPointId == null || extension.getExtensionPointId().equalsIgnoreCase(extensionPointId))
				matchingExtensions.add(extension);
		}
		return matchingExtensions;
	}

	@Override
	public List<AppDescriptor> getAllEnabledApps() {

		// first just get all apps
		List<AppDescriptor> appDescriptors = getAllApps();

		// find out which ones are disabled
		List<AppDescriptor> disabledAppDescriptors = new ArrayList<AppDescriptor>();
		for (AppDescriptor appDescriptor : appDescriptors) {
			if (disabledByComponentState(appDescriptor) || disabledByFeatureToggle(appDescriptor)
			        || disabledByAppFrameworkConfig(appDescriptor)) {
				disabledAppDescriptors.add(appDescriptor);
			}
		}

		// remove disabled apps
		appDescriptors.removeAll(disabledAppDescriptors);
		return appDescriptors;
	}

	private boolean disabledByComponentState(AppDescriptor appDescriptor) {
		GbvComponentState componentState = allComponentsState.getComponentState(appDescriptor.getId(), GbvComponentType.APP);
		return componentState != null && !componentState.getEnabled();
	}

	private boolean disabledByFeatureToggle(AppDescriptor appDescriptor) {
		return disabledByFeatureToggle(appDescriptor.getFeatureToggle());
	}

	private boolean disabledByFeatureToggle(String featureToggle) {

		if (StringUtils.isBlank(featureToggle)) {
			return false;
		}

		Boolean negated = false;

		if (featureToggle.startsWith("!")) {
			featureToggle = featureToggle.substring(1);
			negated = true;
		}

		Boolean featureEnabled = featureToggles.isFeatureEnabled(featureToggle);

		return (!negated && !featureEnabled) || (negated && featureEnabled);
	}

	private boolean disabledByAppFrameworkConfig(AppDescriptor appDescriptor) {
		return !appFrameworkConfig.isEnabled(appDescriptor);
	}

	@Override
	public List<GbvExtension> getAllEnabledExtensions() {
		List<GbvExtension> extensions = new ArrayList<GbvExtension>();

		// first get all extensions from enabled apps
		for (AppDescriptor app : getAllEnabledApps()) {
			if (app.getExtensions() != null) {
				for (GbvExtension extension : app.getExtensions()) {
					// extensions that belong to apps can't be disabled independently of their app, so we don't check AllComponentsState here
					if (!disabledByFeatureToggle(extension) && !disabledByAppFrameworkConfig(extension)) {
						extensions.add(extension);
					}
				}
			}
		}

		// now get "standalone extensions"
		for (GbvExtension extension : allFreeStandingExtensions.getExtensions()) {
			if (!disabledByFeatureToggle(extension) && !disabledByComponentState(extension)
			        && !disabledByAppFrameworkConfig(extension)) {
				extensions.add(extension);
			}
		}

		Collections.sort(extensions);
		return extensions;
	}

	/**
	 * @see org.openmrs.module.gbv.service.GbvAppFrameworkService#getAllEnabledExtensions(java.lang.String)
	 */
	@Override
	public List<GbvExtension> getAllEnabledExtensions(String extensionPointId) {
		List<GbvExtension> extensions = new ArrayList<GbvExtension>();

		// first get all extensions from enabled apps
		for (AppDescriptor app : getAllEnabledApps()) {
			if (app.getExtensions() != null) {
				for (GbvExtension extension : app.getExtensions()) {
					// extensions that belong to apps can't be disabled independently of their app, so we don't check AllComponentsState here
					if (matchesExtensionPoint(extension, extensionPointId) && !disabledByFeatureToggle(extension)
					        && !disabledByAppFrameworkConfig(extension)) {
						extensions.add(extension);
					}
				}
			}
		}

		// now get "standalone extensions"
		for (GbvExtension extension : allFreeStandingExtensions.getExtensions()) {
			if (matchesExtensionPoint(extension, extensionPointId) && !disabledByFeatureToggle(extension)
			        && !disabledByComponentState(extension) && !disabledByAppFrameworkConfig(extension)) {
				extensions.add(extension);
			}
		}

		Collections.sort(extensions);
		return extensions;
	}

	private boolean matchesExtensionPoint(GbvExtension extension, String extensionPointId) {
		return extensionPointId == null || extensionPointId.equals(extension.getExtensionPointId());
	}

	private boolean disabledByComponentState(GbvExtension extension) {
		GbvComponentState componentState = allComponentsState.getComponentState(extension.getId(), GbvComponentType.EXTENSION);
		return componentState != null && !componentState.getEnabled();
	}

	private boolean disabledByFeatureToggle(GbvExtension extension) {
		return disabledByFeatureToggle(extension.getFeatureToggle());
	}

	private boolean disabledByAppFrameworkConfig(GbvExtension extension) {
		return !appFrameworkConfig.isEnabled(extension);
	}

	@Override
	public void enableApp(String appId) {
		allComponentsState.setComponentState(appId, GbvComponentType.APP, true);
	}

	@Override
	public void disableApp(String appId) {
		allComponentsState.setComponentState(appId, GbvComponentType.APP, false);
	}

	@Override
	public void enableExtension(String extensionId) {
		allComponentsState.setComponentState(extensionId, GbvComponentType.EXTENSION, true);
	}

	@Override
	public void disableExtension(String extensionId) {
		allComponentsState.setComponentState(extensionId, GbvComponentType.EXTENSION, false);
	}

	@Override
	public List<GbvExtension> getExtensionsForCurrentUser() {
		return getExtensionsForCurrentUser(null);
	}

	@Override
	public List<GbvExtension> getExtensionsForCurrentUser(String extensionPointId) {
		return getExtensionsForCurrentUser(extensionPointId, null);
	}

	@Override
	public List<GbvExtension> getExtensionsForCurrentUser(String extensionPointId, GbvAppContextModel contextModel) {
		List<GbvExtension> extensions = new ArrayList<GbvExtension>();
		UserContext userContext = Context.getUserContext();

		for (GbvExtension candidate : getAllEnabledExtensions(extensionPointId)) {
			if ((candidate.getBelongsTo() == null
			        || hasPrivilege(userContext, candidate.getBelongsTo().getRequiredPrivilege()))
			        && hasPrivilege(userContext, candidate.getRequiredPrivilege())) {
				if (contextModel == null || checkRequireExpression(candidate, contextModel)) {
					extensions.add(candidate);
				}
			}
		}

		return extensions;
	}

	@Override
	public boolean checkRequireExpression(GbvRequireable candidate, GbvAppContextModel contextModel) {
		try {
			String requireExpression = candidate.getRequire();
			if (StringUtils.isBlank(requireExpression)) {
				return true;
			} else {
				// If any properties in contextModel are Maps, we want to allow scripts to access their subproperties
				// with dot notation, but ScriptEngine and Bindings don't naturally handle this. Instead we will convert
				// all Map-type properties to JSON and use this to define objects directly within the ScriptEngine.
				// (Properties that are not Maps don't need this special treatment.)
				Bindings bindings = new SimpleBindings();
				Map<String, Object> mapProperties = new HashMap<String, Object>();
				for (Map.Entry<String, Object> e : contextModel.entrySet()) {
					if (e.getValue() instanceof Map) {
						mapProperties.put(e.getKey(), e.getValue());
					} else {
						bindings.put(e.getKey(), e.getValue());
					}
				}

				javascriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
				ObjectMapper jackson = new ObjectMapper();
				for (Map.Entry<String, Object> e : mapProperties.entrySet()) {
					try {
						javascriptEngine.eval("var " + e.getKey() + " = " + jackson.writeValueAsString(e.getValue()) + ";");
					}
					catch (Exception ex) {
						StringBuilder extraInfo = new StringBuilder();
						extraInfo.append("type:").append(e.getValue().getClass().getName());
						if (e.getValue() instanceof Map) {
							Map<?, ?> map = (Map<?, ?>) e.getValue();
							extraInfo.append(" properties:");
							for (Map.Entry entry : map.entrySet()) {
								extraInfo.append(" ").append(entry.getKey().toString()).append(":")
										.append(entry.getValue() == null ? "null" : entry.getValue().getClass().toString());
							}
						}
						log.error("Failed to set '" + e.getKey() + "' scope variable (" + extraInfo
										+ ") while evaluating require check for " + candidate.getClass().getSimpleName()
										.toLowerCase() + " " + candidate.getId(),
								ex);
						return false;
					}
				}

				return javascriptEngine.eval("(" + requireExpression + ") == true").equals(Boolean.TRUE);
			}

		}
		catch (Exception e) {
			log.error("Failed to evaluate 'require' check for extension " + candidate.getId(), e);
			return false;
		}
	}

	@Override
	public List<AppDescriptor> getAppsForCurrentUser() {
		List<AppDescriptor> userApps = new ArrayList<AppDescriptor>();
		UserContext userContext = Context.getUserContext();

		List<AppDescriptor> enabledApps = getAllEnabledApps();

		for (AppDescriptor candidate : enabledApps) {
			if (hasPrivilege(userContext, candidate.getRequiredPrivilege())) {
				userApps.add(candidate);
			}
		}
		return userApps;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Location> getLoginLocations() {
		LocationTag supportsLogin = locationService.getLocationTagByName(GbvAppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN);
		List<Location> locations = locationService.getLocationsByTag(supportsLogin);
		List<GbvLoginLocationFilter> filters = Context.getRegisteredComponents(GbvLoginLocationFilter.class);
		if (filters.isEmpty()) {
			return locations;
		}

		List<Location> allowedLocations = new ArrayList();
		for (Location location : locations) {
			boolean exclude = false;
			for (GbvLoginLocationFilter filter : filters) {
				if (!filter.accept(location)) {
					exclude = true;
					break;
				}
			}

			if (!exclude) {
				allowedLocations.add(location);
			}
		}

		return allowedLocations;
	}

	private boolean hasPrivilege(UserContext userContext, String privilege) {
		return StringUtils.isBlank(privilege) || userContext.hasPrivilege(privilege);
	}

	@Override
	public List<GbvAppTemplate> getAllAppTemplates() {
		return allAppTemplates.getAppTemplates();
	}

	@Override
	public GbvAppTemplate getAppTemplate(String id) {
		return allAppTemplates.getAppTemplate(id);
	}

	@Override
	public AppDescriptor getApp(String id) {
		return allAppDescriptors.getAppDescriptor(id);
	}

	@Override
	public GbvUserApp getUserApp(String appId) {
		return allUserApps.getUserApp(appId);
	}

	@Override
	public List<GbvUserApp> getUserApps() {
		return allUserApps.getUserApps();
	}

	@Override
	public GbvUserApp saveUserApp(GbvUserApp userApp) {
		GbvUserApp toReturn = allUserApps.saveUserApp(userApp);
		//Refresh to pick up the newly added ones and any changes
		new GbvAppFrameworkActivator().contextRefreshed();
		return toReturn;
	}

	@Override
	public void purgeUserApp(GbvUserApp userApp) {
		allUserApps.deleteUserApp(userApp);
		new GbvAppFrameworkActivator().contextRefreshed();
	}

}
