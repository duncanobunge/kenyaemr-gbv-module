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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.handler.TagHandler;
import org.openmrs.module.gbv.ContentManager;
import org.openmrs.module.gbv.CoreUtils;
import org.openmrs.module.gbv.form.GbvFormDescriptor.Gender;
import org.openmrs.module.gbv.program.GbvProgramDescriptor;
import org.openmrs.module.gbv.program.GbvProgramManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Forms manager
 */
@Component
public class GbvFormManager implements ContentManager {

	protected static final Log log = LogFactory.getLog(GbvFormManager.class);

	protected static final String tagHandlerClassSuffix = "TagHandler";

	private Map<String, GbvFormDescriptor> forms = new LinkedHashMap<String, GbvFormDescriptor>();

	private List<GbvFormDescriptor> commonPatientForms = new ArrayList<GbvFormDescriptor>();
	private List<GbvFormDescriptor> commonVisitForms = new ArrayList<GbvFormDescriptor>();

	@Autowired
	private GbvProgramManager programManager;

	/**
	 * @see org.openmrs.module.gbv.ContentManager#getPriority()
	 */
	@Override
	public int getPriority() {
		return 70;
	}

	/**
	 * @see org.openmrs.module.gbv.ContentManager#refresh()
	 */
	@Override
	public synchronized void refresh() {
		forms.clear();
		commonPatientForms.clear();
		commonVisitForms.clear();

		List<GbvFormDescriptor> descriptors = Context.getRegisteredComponents(GbvFormDescriptor.class);

		// Sort by form descriptor order
		Collections.sort(descriptors);

		// Process form descriptor beans
		for (GbvFormDescriptor formDescriptor : descriptors) {
			Form form = Context.getFormService().getFormByUuid(formDescriptor.getTargetUuid());

			if (form == null) {
				throw new RuntimeException("No such form with UUID: " + formDescriptor.getTargetUuid());
			}

			if (forms.containsKey(formDescriptor.getTargetUuid())) {
				throw new RuntimeException("Form " + formDescriptor.getTargetUuid() + " already registered");
			}

			forms.put(form.getUuid(), formDescriptor);

			// Attach form resource if descriptor specifies one
			if (formDescriptor.getHtmlform() != null) {
				FormUtils.setFormXmlResource(form, formDescriptor.getHtmlform());
			}

			log.debug("Registered form '" + form.getName() + "' (" + form.getUuid() + ")");
		}

		// Process form configuration beans
		for (FormConfiguration configuration : Context.getRegisteredComponents(FormConfiguration.class)) {
			// Register general per-patient forms
			commonPatientForms.addAll(configuration.getCommonPatientForms());

			// Register general per-visit forms
			commonVisitForms.addAll(configuration.getCommonVisitForms());

			// Register additional program specific per-patient forms
			for (GbvProgramDescriptor programDescriptor : configuration.getProgramPatientForms().keySet()) {
				for (GbvFormDescriptor form : configuration.getProgramPatientForms().get(programDescriptor)) {
					programDescriptor.addPatientForm(form);
				}
			}

			// Register additional program specific per-visit forms
			for (GbvProgramDescriptor programDescriptor : configuration.getProgramVisitForms().keySet()) {
				for (GbvFormDescriptor form : configuration.getProgramVisitForms().get(programDescriptor)) {
					programDescriptor.addVisitForm(form);
				}
			}

			// Disable forms which should be disabled
			for (GbvFormDescriptor descriptor : configuration.getDisabledForms()) {
				descriptor.setEnabled(false);
			}
		}

		commonPatientForms = CoreUtils.merge(commonPatientForms); // Sorts and removes duplicates
		commonVisitForms = CoreUtils.merge(commonVisitForms);

		refreshTagHandlers();
	}

	/**
	 * Refreshes tag handler components
	 */
	private void refreshTagHandlers() {
		for (TagHandler tagHandler : Context.getRegisteredComponents(TagHandler.class)) {
			String className = tagHandler.getClass().getSimpleName();

			if (className.endsWith(tagHandlerClassSuffix)) {
				String tagName = StringUtils
						.uncapitalize(className.substring(0, className.length() - tagHandlerClassSuffix.length()));
				HtmlFormEntryUtil.getService().addHandler(tagName, tagHandler);
				log.info("Registered tag handler class " + className + " for tag <" + tagName + ">");
			} else {
				log.warn("Not registering tag handler class " + className + ". Name does not end with "
						+ tagHandlerClassSuffix);
			}
		}
	}

	/**
	 * Gets the form descriptor for the given form
	 * 
	 * @param form the form
	 * @return the form descriptor
	 */
	public GbvFormDescriptor getFormDescriptor(Form form) {
		return forms.get(form.getUuid());
	}

	/**
	 * Gets all registered form descriptors
	 * 
	 * @return the form descriptors
	 */
	public Collection<GbvFormDescriptor> getAllFormDescriptors() {
		return forms.values();
	}

	/**
	 * Gets all per-patient forms
	 * 
	 * @param app     the current application
	 * @param patient the patient
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getCommonFormsForPatient(AppDescriptor app, Patient patient) {
		return filterForms(commonPatientForms, app, patient);
	}

	/**
	 * Gets the program specific per-patient forms
	 * 
	 * @param app     the current application
	 * @param program the program
	 * @param patient the patient
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getProgramFormsForPatient(AppDescriptor app, Program program, Patient patient) {
		GbvProgramDescriptor programDescriptor = programManager.getProgramDescriptor(program);
		if (programDescriptor.getPatientForms() != null) {
			return filterForms(programDescriptor.getPatientForms(), app, patient);
		}
		return Collections.emptyList();
	}

	/**
	 * Gets all uncompleted per-visit forms appropriate for the given visit
	 * 
	 * @param app   the current application
	 * @param visit the visit
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getAllUncompletedFormsForVisit(AppDescriptor app, Visit visit) {
		List<GbvFormDescriptor> uncompletedForms = new ArrayList<GbvFormDescriptor>();
		Set<Form> completedForms = new HashSet<Form>();

		// Gather up all completed forms
		if (visit.getEncounters() != null) {
			for (Encounter encounter : visit.getEncounters()) {
				if (encounter.getForm() != null && !encounter.isVoided()) {
					completedForms.add(encounter.getForm());
				}
			}
		}

		// Include only forms that haven't been completed for this visit
		for (GbvFormDescriptor suitableForms : getAllFormsForVisit(app, visit)) {
			if (!completedForms.contains(suitableForms.getTarget())) {
				uncompletedForms.add(suitableForms);
			}
		}

		return uncompletedForms;
	}

	/**
	 * Gets all per-visit forms appropriate for the given visit
	 * 
	 * @param app   the current application
	 * @param visit the visit
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getAllFormsForVisit(AppDescriptor app, Visit visit) {
		Set<GbvFormDescriptor> forms = new TreeSet<GbvFormDescriptor>();

		forms.addAll(commonVisitForms);

		// Consider all programs active on the visit stop date, or if visit is still
		// open, active now
		Date activeOnDate = (visit.getStopDatetime() != null) ? visit.getStopDatetime() : new Date();

		for (GbvProgramDescriptor activeProgram : programManager.getPatientActivePrograms(visit.getPatient(),
				activeOnDate)) {
			if (activeProgram.getVisitForms() != null) {
				forms.addAll(activeProgram.getVisitForms());
			}
		}

		return filterForms(forms, app, visit.getPatient());
	}

	/**
	 * Gets all completed per-visit forms appropriate for the given visit
	 * 
	 * @param app   the current application
	 * @param visit the visit
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getCompletedFormsForVisit(AppDescriptor app, Visit visit) {
		List<GbvFormDescriptor> completedForms = new ArrayList<GbvFormDescriptor>();

		for (Encounter encounter : visit.getEncounters()) {
			if (encounter.getForm() != null) {
				GbvFormDescriptor descriptor = getFormDescriptor(encounter.getForm());

				// Filter by app and ignore forms with no descriptor
				if (descriptor != null && descriptor.getApps().contains(app)) {
					completedForms.add(descriptor);
				}
			}
		}

		return completedForms;
	}

	/**
	 * Filters the given collection of enabled forms to those applicable for the
	 * given application and patient
	 * 
	 * @param app     the application
	 * @param patient the patient
	 * @return the filtered forms
	 */
	protected List<GbvFormDescriptor> filterForms(Collection<GbvFormDescriptor> descriptors, AppDescriptor app,
			Patient patient) {
		List<GbvFormDescriptor> filtered = new ArrayList<GbvFormDescriptor>();
		for (GbvFormDescriptor descriptor : descriptors) {
			if (!descriptor.isEnabled()) {
				continue;
			}

			// Filter by app id
			if (app != null && descriptor.getApps() != null && !descriptor.getApps().contains(app)) {
				continue;
			}

			// Filter by patient gender
			if (patient.getGender() != null) {
				if (patient.getGender().equals("F") && descriptor.getGender() == Gender.MALE)
					continue;
				else if (patient.getGender().equals("M") && descriptor.getGender() == Gender.FEMALE)
					continue;
			}

			/*
			 * if (descriptor.getShowIfCalculation() != null &&
			 * !isPatientEligibleFor(patient, descriptor)) {
			 * continue;
			 * }
			 */

			filtered.add(descriptor);
		}

		return filtered;
	}

	/**
	 *
	 * @param patient
	 * @param descriptor
	 * @return
	 */
	/*
	 * public boolean isPatientEligibleFor(Patient patient, FormDescriptor
	 * descriptor) {
	 * 
	 * Class<? extends PatientCalculation> clazz =
	 * descriptor.getShowIfCalculation();
	 * 
	 * PatientCalculation calculation =
	 * CalculationUtils.instantiateCalculation(clazz, null);
	 * 
	 * CalculationResult result =
	 * Context.getService(PatientCalculationService.class).evaluate(patient.getId(),
	 * calculation);
	 * return ResultUtil.isTrue(result);
	 * }
	 */

	/**
	 * function for interacting with o3
	 */

	/**
	 * Filters forms for a patient
	 * 
	 * @param descriptors
	 * @param patient
	 * @return
	 */
	protected List<GbvFormDescriptor> filterForms(Collection<GbvFormDescriptor> descriptors, Patient patient) {
		List<GbvFormDescriptor> filtered = new ArrayList<GbvFormDescriptor>();
		for (GbvFormDescriptor descriptor : descriptors) {
			if (!descriptor.isEnabled()) {
				continue;
			}

			// Filter by patient gender
			if (patient.getGender() != null) {
				if (patient.getGender().equals("F") && descriptor.getGender() == Gender.MALE)
					continue;
				else if (patient.getGender().equals("M") && descriptor.getGender() == Gender.FEMALE)
					continue;
			}

			/*
			 * if (descriptor.getShowIfCalculation() != null &&
			 * !isPatientEligibleFor(patient, descriptor)) {
			 * continue;
			 * }
			 */

			filtered.add(descriptor);
		}

		return filtered;
	}

	/**
	 * Gets all completed per-visit forms appropriate for the given visit
	 * 
	 * @param visit the visit
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getCompletedFormsForVisit(Visit visit) {
		List<GbvFormDescriptor> completedForms = new ArrayList<GbvFormDescriptor>();

		for (Encounter encounter : visit.getEncounters()) {
			if (encounter.getForm() != null) {
				GbvFormDescriptor descriptor = getFormDescriptor(encounter.getForm());

				// Filter by app and ignore forms with no descriptor
				if (descriptor != null) {
					completedForms.add(descriptor);
				}
			}
		}

		return completedForms;
	}

	/**
	 * Get all uncompleted forms for visit
	 * 
	 * @param visit
	 * @return
	 */
	public List<GbvFormDescriptor> getAllUncompletedFormsForVisit(Visit visit) {
		List<GbvFormDescriptor> uncompletedForms = new ArrayList<GbvFormDescriptor>();
		Set<Form> completedForms = new HashSet<Form>();

		// Gather up all completed forms
		if (visit.getEncounters() != null) {
			for (Encounter encounter : visit.getEncounters()) {
				if (encounter.getForm() != null && !encounter.isVoided()) {
					completedForms.add(encounter.getForm());
				}
			}
		}

		// Include only forms that haven't been completed for this visit
		for (GbvFormDescriptor suitableForms : getAllFormsForVisit(visit)) {
			if (!completedForms.contains(suitableForms.getTarget())) {
				uncompletedForms.add(suitableForms);
			}
		}

		return uncompletedForms;
	}

	/**
	 * Gets all per-visit forms appropriate for the given visit
	 * 
	 * @param visit the visit
	 * @return the form descriptors
	 */
	public List<GbvFormDescriptor> getAllFormsForVisit(Visit visit) {
		Set<GbvFormDescriptor> forms = new TreeSet<GbvFormDescriptor>();

		forms.addAll(commonVisitForms);

		// Consider all programs active on the visit stop date, or if visit is still
		// open, active now
		Date activeOnDate = (visit.getStopDatetime() != null) ? visit.getStopDatetime() : new Date();

		for (GbvProgramDescriptor activeProgram : programManager.getPatientActivePrograms(visit.getPatient(),
				activeOnDate)) {
			if (activeProgram.getVisitForms() != null) {
				forms.addAll(activeProgram.getVisitForms());
			}
		}

		return filterForms(forms, visit.getPatient());
	}

}