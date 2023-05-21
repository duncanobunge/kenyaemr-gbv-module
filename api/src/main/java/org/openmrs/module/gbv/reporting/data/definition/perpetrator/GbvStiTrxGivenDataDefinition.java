package org.openmrs.module.gbv.reporting.data.definition.perpetrator;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;

public class GbvStiTrxGivenDataDefinition extends BaseDataDefinition implements EncounterDataDefinition{
    
    public static final long serialVersionUID = 1L;

    public GbvStiTrxGivenDataDefinition() {
        super();
    }

    public GbvStiTrxGivenDataDefinition(String name) {
        super(name);
    }

       /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
    
}
