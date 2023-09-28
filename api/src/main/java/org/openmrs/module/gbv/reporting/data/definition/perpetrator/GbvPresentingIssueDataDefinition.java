package org.openmrs.module.gbv.reporting.data.definition.perpetrator;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/* presenting_issue col */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class GbvPresentingIssueDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    public static final long serialVersionUID = 1L;

    public GbvPresentingIssueDataDefinition() {
        super();
    }

    public GbvPresentingIssueDataDefinition(String name) {
        super(name);
    }
    
      /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
    
}
