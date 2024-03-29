/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.gbv.fragment.controller.program;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.PatientProgram;
import org.openmrs.module.gbv.program.GbvProgramDescriptor;
import org.openmrs.module.gbv.program.GbvProgramManager;
import org.openmrs.module.gbv.wrapper.Enrollment;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.module.gbv.EmrWebConstants;

/**
 * Program completion fragment
 */
public class ProgramCompletionFragmentController {
	
	public void controller(@FragmentParam("patientProgram") PatientProgram patientProgram,
	        @FragmentParam("showClinicalData") boolean showClinicalData, @SpringBean GbvProgramManager programManager,
	        FragmentModel model) {
		
		GbvProgramDescriptor programDescriptor = programManager.getProgramDescriptor(patientProgram.getProgram());
		Form defaultCompletionForm = programDescriptor.getDefaultCompletionForm().getTarget();
		
		Enrollment enrollment = new Enrollment(patientProgram);
		
		// Might not be the default completion form, but should have the same encounter type
		Encounter encounter = enrollment.lastEncounter(defaultCompletionForm.getEncounterType());
		
		model.put("summaryFragment",
		    programDescriptor.getFragments().get(EmrWebConstants.PROGRAM_COMPLETION_SUMMARY_FRAGMENT));
		model.put("enrollment", patientProgram);
		model.put("encounter", encounter);
		model.put("showClinicalData", showClinicalData);
	}
}
