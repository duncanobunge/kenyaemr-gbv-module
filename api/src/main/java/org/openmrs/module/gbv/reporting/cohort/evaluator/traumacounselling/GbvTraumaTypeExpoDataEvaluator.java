package org.openmrs.module.gbv.reporting.cohort.evaluator.traumacounselling;

import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.GbvTraumaTypeExpoDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.openmrs.annotation.Handler;

import java.util.Map;

@Handler(supports = { GbvTraumaTypeExpoDataDefinition.class })
public class GbvTraumaTypeExpoDataEvaluator implements EncounterDataEvaluator {
    
    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);
		String qry = "select encounter_id,type_of_exposure from kenyaemr_etl.etl_gbv_counsellingencounter;";


        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
    
}
