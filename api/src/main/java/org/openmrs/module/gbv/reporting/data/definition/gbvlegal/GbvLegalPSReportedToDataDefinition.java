package org.openmrs.module.gbv.reporting.data.definition.gbvlegal;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/*Police station reported to */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class GbvLegalPSReportedToDataDefinition extends BaseDataDefinition implements EncounterDataDefinition{

    public static final long serialVersionUID = 1L;

    public GbvLegalPSReportedToDataDefinition() {
        super();
    }

    public GbvLegalPSReportedToDataDefinition(String name) {
        super(name);
    }

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
    
}
