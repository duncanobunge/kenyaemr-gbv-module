package org.openmrs.module.gbv.reporting.cohort.definition.pepmanagement;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;


@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.GbvPepManagementEncCohortDefinition")
public class GbvPepManagementEncCohortDefinition extends BaseQuery<Encounter> implements EncounterQuery  {
    
}
