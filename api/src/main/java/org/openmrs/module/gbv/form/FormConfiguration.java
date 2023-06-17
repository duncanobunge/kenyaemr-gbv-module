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

package org.openmrs.module.gbv.form;

import org.openmrs.module.gbv.AbstractContentConfiguration;
import org.openmrs.module.gbv.program.GbvProgramDescriptor;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Configuration for forms
 */
public class FormConfiguration extends AbstractContentConfiguration {

	private Set<GbvFormDescriptor> commonPatientForms;

	private Set<GbvFormDescriptor> commonVisitForms;

	private Map<GbvProgramDescriptor, Set<GbvFormDescriptor>> programPatientForms;

	private Map<GbvProgramDescriptor, Set<GbvFormDescriptor>> programVisitForms;

	private Set<GbvFormDescriptor> disabledForms;

	/**
	 * Gets the common per-patient forms
	 * 
	 * @return the form descriptors
	 */
	public Set<GbvFormDescriptor> getCommonPatientForms() {
		if (commonPatientForms == null) {
			commonPatientForms = new LinkedHashSet<GbvFormDescriptor>();
		}

		return commonPatientForms;
	}

	/**
	 * Sets the common per-patient forms
	 * 
	 * @param commonPatientForms the form descriptors
	 */
	public void setCommonPatientForms(Set<GbvFormDescriptor> commonPatientForms) {
		this.commonPatientForms = commonPatientForms;
	}

	/**
	 * Gets the general pre-visit forms
	 * 
	 * @return the form descriptors
	 */
	public Set<GbvFormDescriptor> getCommonVisitForms() {
		if (commonVisitForms == null) {
			commonVisitForms = new LinkedHashSet<GbvFormDescriptor>();
		}

		return commonVisitForms;
	}

	/**
	 * Sets the general per-visit forms
	 * 
	 * @param commonVisitForms the form descriptors
	 */
	public void setCommonVisitForms(Set<GbvFormDescriptor> commonVisitForms) {
		this.commonVisitForms = commonVisitForms;
	}

	/**
	 * Gets the program specific per-patient forms
	 * 
	 * @return the map of program and form descriptors
	 */
	public Map<GbvProgramDescriptor, Set<GbvFormDescriptor>> getProgramPatientForms() {
		if (programPatientForms == null) {
			programPatientForms = new LinkedHashMap<GbvProgramDescriptor, Set<GbvFormDescriptor>>();
		}

		return programPatientForms;
	}

	/**
	 * Sets the program specific per-patient forms
	 * 
	 * @param programPatientForms the map of program and form descriptors
	 */
	public void setProgramPatientForms(Map<GbvProgramDescriptor, Set<GbvFormDescriptor>> programPatientForms) {
		this.programPatientForms = programPatientForms;
	}

	/**
	 * Gets the program specific per-visit forms
	 * 
	 * @return the map of program and form descriptors
	 */
	public Map<GbvProgramDescriptor, Set<GbvFormDescriptor>> getProgramVisitForms() {
		if (programVisitForms == null) {
			programVisitForms = new LinkedHashMap<GbvProgramDescriptor, Set<GbvFormDescriptor>>();
		}

		return programVisitForms;
	}

	/**
	 * Sets the program specific per-visit forms
	 * 
	 * @param programVisitForms the map of program and form descriptors
	 */
	public void setProgramVisitForms(Map<GbvProgramDescriptor, Set<GbvFormDescriptor>> programVisitForms) {
		this.programVisitForms = programVisitForms;
	}

	/**
	 * Gets the forms to be disabled
	 * 
	 * @return the form descriptors
	 */
	public Set<GbvFormDescriptor> getDisabledForms() {
		if (disabledForms == null) {
			disabledForms = new LinkedHashSet<GbvFormDescriptor>();
		}

		return disabledForms;
	}

	/**
	 * Sets the forms to be disabled
	 * 
	 * @param disabledForms the form descriptors
	 */
	public void setDisabledForms(Set<GbvFormDescriptor> disabledForms) {
		this.disabledForms = disabledForms;
	}
}