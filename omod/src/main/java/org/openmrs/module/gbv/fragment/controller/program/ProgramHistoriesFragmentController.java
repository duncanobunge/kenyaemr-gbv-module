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


import org.openmrs.Patient;
import org.openmrs.module.gbv.program.GbvProgramDescriptor;
import org.openmrs.module.gbv.program.GbvProgramManager;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Program history fragment
 */
public class ProgramHistoriesFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam ("patient") Patient patient,
	        @FragmentParam("showClinicalData") boolean showClinicalData, @SpringBean GbvProgramManager programManager) {
		
		List<GbvProgramDescriptor> programs = new ArrayList<GbvProgramDescriptor>();
		
		if (!patient.isVoided()) {
			Collection<GbvProgramDescriptor> activePrograms = programManager.getPatientActivePrograms(patient);
			Collection<GbvProgramDescriptor> eligiblePrograms = programManager.getPatientEligiblePrograms(patient);
			// Display active programs on top
			programs.addAll(activePrograms);
			
			// Don't add duplicates for programs for which patient is both active and eligible
			for (GbvProgramDescriptor descriptor : eligiblePrograms) {
				if (!programs.contains(descriptor)) {
					if (descriptor.getTargetUuid().equalsIgnoreCase("e41c3d74-37c7-4001-9f19-ef9e35224b70")) {
					    programs.add(descriptor);
				}
			}
		}
		
		model.addAttribute("patient", patient);
		model.addAttribute("programs", programs);
		model.addAttribute("showClinicalData", showClinicalData);
	}
}
}
