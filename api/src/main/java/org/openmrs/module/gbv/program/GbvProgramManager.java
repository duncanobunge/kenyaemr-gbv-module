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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.gbv.ContentManager;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Program manager
 */
@Component
public class GbvProgramManager implements ContentManager {

	protected static final Log log = LogFactory.getLog(GbvProgramManager.class);

	private Map<String, GbvProgramDescriptor> programs = new LinkedHashMap<String, GbvProgramDescriptor>();

	/**
	 * @see org.openmrs.module.gbv.ContentManager#getPriority()
	 */
	@Override
	public int getPriority() {
		return 60;
	}

	/**
	 * @see org.openmrs.module.gbv.ContentManager#refresh()
	 */
	@Override
	public synchronized void refresh() {
		programs.clear();

		List<GbvProgramDescriptor> descriptors = Context.getRegisteredComponents(GbvProgramDescriptor.class);

		// Sort by identifier descriptor order
		Collections.sort(descriptors);

		for (GbvProgramDescriptor descriptor : descriptors) {
			if (programs.containsKey(descriptor.getTargetUuid())) {
				throw new RuntimeException("Program " + descriptor.getTargetUuid() + " already registered");
			}

			programs.put(descriptor.getTargetUuid(), descriptor);

			log.debug("Registered program '" + descriptor.getTarget().getName() + "' (" + descriptor.getTargetUuid()
					+ ")");
		}
	}

	/**
	 * Gets all program descriptors
	 * 
	 * @return the program descriptors
	 */
	public Collection<GbvProgramDescriptor> getAllProgramDescriptors() {
		return programs.values();
	}

	/**
	 * Gets the program descriptor for the given program
	 * 
	 * @param program the program
	 * @return the program descriptor
	 */
	public GbvProgramDescriptor getProgramDescriptor(Program program) {
		return programs.get(program.getUuid());
	}

	/**
	 * Gets program descriptors for all programs which the given patient has ever
	 * been enrolled in
	 * 
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<GbvProgramDescriptor> getPatientPrograms(Patient patient) {
		Collection<GbvProgramDescriptor> everIn = new LinkedHashSet<GbvProgramDescriptor>();

		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
			GbvProgramDescriptor descriptor = getProgramDescriptor(pp.getProgram());
			if (descriptor != null && descriptor.isEnabled()) {
				everIn.add(descriptor);
			}
		}

		return everIn;
	}

	/**
	 * Gets program descriptors for all programs which the given patient is eligible
	 * for
	 * 
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<GbvProgramDescriptor> getPatientEligiblePrograms(Patient patient) {
		List<GbvProgramDescriptor> eligibleFor = new ArrayList<GbvProgramDescriptor>();

		for (GbvProgramDescriptor descriptor : programs.values()) {
			if (descriptor.isEnabled() && isPatientEligibleFor(patient, descriptor.getTarget())) {
				eligibleFor.add(descriptor);
			}
		}

		return eligibleFor;
	}

	/**
	 * Checks whether patient is eligible to enroll in the given program
	 * 
	 * @param patient the patient
	 * @param program the program
	 * @return true if patient can enroll
	 */
	public boolean isPatientEligibleFor(Patient patient, Program program) {
		GbvProgramDescriptor descriptor = getProgramDescriptor(program);

		Class<? extends PatientCalculation> clazz = descriptor.getEligibilityCalculation();

		PatientCalculation calculation = CalculationUtils.instantiateCalculation(clazz, null);

		CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(patient.getId(),
				calculation);
		return ResultUtil.isTrue(result);
	}

	/**
	 * Gets program descriptors for all programs which the given patient is
	 * currently enrolled in
	 * 
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<GbvProgramDescriptor> getPatientActivePrograms(Patient patient) {
		return getPatientActivePrograms(patient, new Date());
	}

	/**
	 * Gets program descriptors for all programs which the given patient was
	 * enrolled in on given date
	 * 
	 * @param patient the patient
	 * @param onDate  the date
	 * @return the program descriptors
	 */
	public Collection<GbvProgramDescriptor> getPatientActivePrograms(Patient patient, Date onDate) {
		List<GbvProgramDescriptor> activeIn = new ArrayList<GbvProgramDescriptor>();

		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
			if (pp.getActive(onDate)) {
				GbvProgramDescriptor descriptor = getProgramDescriptor(pp.getProgram());
				if (descriptor != null && descriptor.isEnabled()) {
					activeIn.add(descriptor);
				}
			}
		}

		return activeIn;
	}

	/**
	 * Gets all enrollments for the given patient in the given program, in
	 * chronological order. Regular service method
	 * doesn't guarantee order.
	 * 
	 * @param patient the patient
	 * @param program the program
	 * @return the enrollments
	 */
	public List<PatientProgram> getPatientEnrollments(Patient patient, Program program) {
		List<PatientProgram> enrollments = Context.getProgramWorkflowService().getPatientPrograms(patient, program,
				null, null, null, null, false);

		// Sort by enrollment date ascending
		Collections.sort(enrollments, new Comparator<PatientProgram>() {
			@Override
			public int compare(PatientProgram pp1, PatientProgram pp2) {
				return OpenmrsUtil.compare(pp1.getDateEnrolled(), pp2.getDateEnrolled());
			}
		});

		return enrollments;
	}
}