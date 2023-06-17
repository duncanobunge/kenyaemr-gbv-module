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

package org.openmrs.module.gbv.program;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.gbv.UiResource;
import org.openmrs.module.gbv.form.GbvFormDescriptor;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.gbv.AbstractEntityDescriptor;
import org.openmrs.module.gbv.report.ReportDescriptor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Describes how a program can be used in the EMR. Each program should have a
 * component of this type in the application
 * context.
 */
public class GbvProgramDescriptor extends AbstractEntityDescriptor<Program> {

	private Class<? extends PatientCalculation> eligibilityCalculation;

	private GbvFormDescriptor defaultEnrollmentForm;

	private GbvFormDescriptor defaultCompletionForm;

	private Set<GbvFormDescriptor> patientForms;

	private Set<GbvFormDescriptor> visitForms;

	private Set<ReportDescriptor> reports;

	private Map<String, UiResource> fragments;

	/**
	 * @see org.openmrs.module.gbv.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public Program getTarget() {
		return MetadataUtils.existing(Program.class, targetUuid);
	}

	/**
	 * Gets the eligibility calculation class
	 * 
	 * @return the eligibility calculation class
	 */
	public Class<? extends PatientCalculation> getEligibilityCalculation() {
		return eligibilityCalculation;
	}

	/**
	 * Sets the eligibility calculation class
	 * 
	 * @param eligibilityCalculation the eligibility calculation class
	 */
	public void setEligibilityCalculation(Class<? extends PatientCalculation> eligibilityCalculation) {
		this.eligibilityCalculation = eligibilityCalculation;
	}

	/**
	 * Gets the default enrollment form
	 * 
	 * @return the form
	 */
	public GbvFormDescriptor getDefaultEnrollmentForm() {
		return defaultEnrollmentForm;
	}

	/**
	 * Sets the default enrollment form
	 * 
	 * @param defaultEnrollmentForm the form
	 */
	public void setDefaultEnrollmentForm(GbvFormDescriptor defaultEnrollmentForm) {
		this.defaultEnrollmentForm = defaultEnrollmentForm;
	}

	/**
	 * Gets the default completion form
	 * 
	 * @return the form
	 */
	public GbvFormDescriptor getDefaultCompletionForm() {
		return defaultCompletionForm;
	}

	/**
	 * Sets the default completion form
	 * 
	 * @param defaultCompletionForm the form
	 */
	public void setDefaultCompletionForm(GbvFormDescriptor defaultCompletionForm) {
		this.defaultCompletionForm = defaultCompletionForm;
	}

	/**
	 * Gets the per-patient forms
	 * 
	 * @return the patient forms
	 */
	public Set<GbvFormDescriptor> getPatientForms() {
		return patientForms;
	}

	/**
	 * Sets the per-patient forms
	 * 
	 * @param patientForms the patient forms
	 */
	public void setPatientForms(Set<GbvFormDescriptor> patientForms) {
		this.patientForms = patientForms;
	}

	/**
	 * Adds an additional per-patient form
	 * 
	 * @param patientForm the patient form
	 */
	public void addPatientForm(GbvFormDescriptor patientForm) {
		if (patientForms == null) {
			patientForms = new HashSet<GbvFormDescriptor>();
		}

		patientForms.add(patientForm);
	}

	/**
	 * Gets the per-visit forms
	 * 
	 * @return the visit forms
	 */
	public Set<GbvFormDescriptor> getVisitForms() {
		return visitForms;
	}

	/**
	 * Sets the per-visit forms
	 * 
	 * @param visitForms the visit forms
	 */
	public void setVisitForms(Set<GbvFormDescriptor> visitForms) {
		this.visitForms = visitForms;
	}

	/**
	 * Adds an additional per-visit form
	 * 
	 * @param visitForm the visit form
	 */
	public void addVisitForm(GbvFormDescriptor visitForm) {
		if (visitForms == null) {
			visitForms = new HashSet<GbvFormDescriptor>();
		}

		visitForms.add(visitForm);
	}

	/**
	 * Gets the reports
	 * 
	 * @return the reports
	 */
	public Set<ReportDescriptor> getReports() {
		return reports;
	}

	/**
	 * Sets the reports
	 * 
	 * @param reports the reports
	 */
	public void setReports(Set<ReportDescriptor> reports) {
		this.reports = reports;
	}

	/**
	 * Adds an additional report
	 * 
	 * @param report the report
	 */
	public void addReport(ReportDescriptor report) {
		if (reports == null) {
			reports = new HashSet<ReportDescriptor>();
		}

		reports.add(report);
	}

	/**
	 * Gets the fragments
	 * 
	 * @return the fragment resources
	 */
	public Map<String, UiResource> getFragments() {
		return fragments;
	}

	/**
	 * Sets the fragments
	 * 
	 * @param fragments the fragment resources
	 */
	public void setFragments(Map<String, UiResource> fragments) {
		this.fragments = fragments;
	}
}