package org.openmrs.module.gbv.reporting.data.definition.pepmanagement;

import java.util.Date;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/*type of assault col in pep managerment encounter */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class GbvDateTimeIncidenceDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    public static long serialVersionUID = 1L;

    public GbvDateTimeIncidenceDataDefinition() {
        super();
    }
    public GbvDateTimeIncidenceDataDefinition(String name) {
        super(name);
    }
    
               /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Date.class;
    }
}
