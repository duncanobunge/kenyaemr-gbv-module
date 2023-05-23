package org.openmrs.module.gbv.reporting.data.definition.pepfollowup;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/* compulsory Hiv Test Result col in pep managerment encounter */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class PatientAssessmentDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    
    public static long serialVersionUID = 1L;

    public PatientAssessmentDataDefinition() {
        super();
    }
    public PatientAssessmentDataDefinition(String name) {
        super(name);
    }
                 /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
    
}
