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

package org.openmrs.module.gbv.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.gbv.ContentManager;
import org.openmrs.module.gbv.program.GbvProgramDescriptor;
import org.openmrs.module.gbv.program.GbvProgramManager;
import org.openmrs.module.gbv.report.builder.Builds;
import org.openmrs.module.gbv.report.builder.ReportBuilder;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Report manager
 */
@Component
public class ReportingManager implements ContentManager {

	protected static final Log log = LogFactory.getLog(ReportingManager.class);

	private Map<String, ReportDescriptor> reports = new LinkedHashMap<String, ReportDescriptor>();

	private List<ReportDescriptor> commonReports = new ArrayList<ReportDescriptor>();

	private Map<String, ReportBuilder> builders = new HashMap<String, ReportBuilder>();

	private List<ReportDescriptor> cohortAnalysisReports = new ArrayList<ReportDescriptor>();

	@Autowired
	private GbvProgramManager programManager;

	/**
	 * @see org.openmrs.module.gbv.ContentManager#getPriority()
	 */
	@Override
	public int getPriority() {
		return 80;
	}

	/**
	 * @see org.openmrs.module.gbv.ContentManager#refresh()
	 */
	@Override
	public synchronized void refresh() {
		reports.clear();

		// Process report descriptor components
		for (ReportDescriptor descriptor : Context.getRegisteredComponents(ReportDescriptor.class)) {
			if (reports.containsKey(descriptor.getTargetUuid())) {
				throw new RuntimeException("Report " + descriptor.getTargetUuid() + " already registered");
			}

			reports.put(descriptor.getTargetUuid(), descriptor);

			log.debug("Registered report descriptor: " + descriptor.getId());
		}

		// Process form configuration beans
		for (ReportConfiguration configuration : Context.getRegisteredComponents(ReportConfiguration.class)) {
			// Register common reports
			if (configuration.getCommonReports() != null) {
				commonReports.addAll(configuration.getCommonReports());
			}

			// Register cohort analysis reports here
			if (configuration.getCohortAnalysis() != null) {
				cohortAnalysisReports.addAll(configuration.getCohortAnalysis());
			}

			// Register additional program specific reports
			if (configuration.getProgramReports() != null) {
				Map<GbvProgramDescriptor, Set<ReportDescriptor>> programReports = configuration.getProgramReports();

				for (GbvProgramDescriptor programDescriptor : programReports.keySet()) {
					for (ReportDescriptor report : programReports.get(programDescriptor)) {
						programDescriptor.addReport(report);
					}
				}
			}
		}

		// Process report builder components
		for (ReportBuilder builder : Context.getRegisteredComponents(ReportBuilder.class)) {
			Builds builds = builder.getClass().getAnnotation(Builds.class);
			if (builds != null) {
				for (String reportId : builds.value()) {
					builders.put(reportId, builder);
				}
			}
		}
	}

	/**
	 * Gets a report descriptor for the given report definition
	 * 
	 * @param definition the report definition
	 * @return the report descriptor
	 */
	public ReportDescriptor getReportDescriptor(ReportDefinition definition) {
		return reports.get(definition.getUuid());
	}

	/**
	 * Gets all report descriptors
	 * 
	 * @@return the list of report descriptors
	 */
	public List<ReportDescriptor> getAllReportDescriptors() {
		return new ArrayList<ReportDescriptor>(reports.values());
	}

	/**
	 * Gets all common (non program specific) reports
	 * 
	 * @@return the list of reports
	 */
	public List<ReportDescriptor> getCommonReports(AppDescriptor app) {
		return filterReports(commonReports, app);
	}

	/**
	 * Gets all common (non program specific) reports
	 * 
	 * @@return the list of reports
	 */
	public List<ReportDescriptor> getCohortAnalysisReports(AppDescriptor app) {
		return filterReports(cohortAnalysisReports, app);
	}

	/**
	 * Gets program specific reports
	 * 
	 * @@return the list of reports
	 */
	public List<ReportDescriptor> getProgramReports(AppDescriptor app, Program program) {
		GbvProgramDescriptor programDescriptor = programManager.getProgramDescriptor(program);
		if (programDescriptor.getReports() != null) {
			return filterReports(programDescriptor.getReports(), app);
		}
		return Collections.emptyList();
	}

	/**
	 * Gets a report builder for the given report
	 * 
	 * @param report the report
	 * @return the report builder
	 */

	/**
	 * Filters the given collection of reports to those applicable for the given
	 * application
	 * 
	 * @param app the application
	 * @return the filtered reports
	 */
	protected List<ReportDescriptor> filterReports(Collection<ReportDescriptor> descriptors, AppDescriptor app) {
		List<ReportDescriptor> filtered = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor descriptor : descriptors) {

			// Filter by app id
			if (app != null && descriptor.getApps() != null && !descriptor.getApps().contains(app)) {
				continue;
			}

			filtered.add(descriptor);
		}

		return filtered;
	}

	/**
	 * Gets a "stub" of a report definition from its UUID. We use this to avoid
	 * unnecessarily de-serializing definitions
	 * 
	 * @param uuid the UUID
	 * @return the stub
	 */
	protected ReportDefinition getReportDefinitionStub(String uuid) {
		String query = "SELECT serialized_object_id, uuid FROM serialized_object WHERE uuid = '" + uuid + "'";
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		if (result.size() > 0) {
			ReportDefinition rd = new ReportDefinition();
			rd.setId((Integer) result.get(0).get(0));
			rd.setUuid((String) result.get(0).get(1));
			return rd;
		}
		return null;
	}
}