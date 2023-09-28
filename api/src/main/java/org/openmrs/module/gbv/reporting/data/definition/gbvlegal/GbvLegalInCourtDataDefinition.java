package org.openmrs.module.gbv.reporting.data.definition.gbvlegal;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class GbvLegalInCourtDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    
    public static long serialVersionUID = 1L;

    public GbvLegalInCourtDataDefinition() {
        super();
    }

    public GbvLegalInCourtDataDefinition(String name) {
        super(name);
    }
    
    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
    
}
