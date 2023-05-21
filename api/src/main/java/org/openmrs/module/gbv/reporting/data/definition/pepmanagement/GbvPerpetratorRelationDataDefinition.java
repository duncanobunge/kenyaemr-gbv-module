package org.openmrs.module.gbv.reporting.data.definition.pepmanagement;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/* perpetrator relation col in pep managerment encounter */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class GbvPerpetratorRelationDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    public static long serialVersionUID = 1L;

    public GbvPerpetratorRelationDataDefinition() {
        super();
    }
    public GbvPerpetratorRelationDataDefinition(String name) {
        super(name);
    }
                 /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
    
}
