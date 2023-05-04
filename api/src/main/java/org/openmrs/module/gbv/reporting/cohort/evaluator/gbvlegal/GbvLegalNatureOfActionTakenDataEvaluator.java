package org.openmrs.module.gbv.reporting.cohort.evaluator.gbvlegal;

import org.openmrs.annotation.Handler;
import org.openmrs.module.gbv.reporting.data.definition.gbvlegal.GbvLegalNatureOfActionDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates Legal Nature of Action Taken Col
 */

@Handler(supports = {GbvLegalNatureOfActionDataDefinition.class})
public class GbvLegalNatureOfActionTakenDataEvaluator implements EncounterDataEvaluator {

    
    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);
		String qry = "select encounter_id,nature_of_action_taken from kenyaemr_etl.etl_gbv_legal;";


        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }

}
