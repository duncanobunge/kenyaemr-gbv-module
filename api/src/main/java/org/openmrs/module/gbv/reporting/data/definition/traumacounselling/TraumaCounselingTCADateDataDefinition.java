package org.openmrs.module.gbv.reporting.data.definition.traumacounselling;

import java.util.Date;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class TraumaCounselingTCADateDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    
    public static final long serialVersionUID = 1L;

    public TraumaCounselingTCADateDataDefinition() {
        super();
    }

    public TraumaCounselingTCADateDataDefinition(String name) {
        super(name);
    }
    
        /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
        public Class<?> getDataType() {
            return Date.class;
        }
}
