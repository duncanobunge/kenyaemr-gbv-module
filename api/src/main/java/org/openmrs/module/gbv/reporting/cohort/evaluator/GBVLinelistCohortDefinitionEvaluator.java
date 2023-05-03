/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.gbv.reporting.cohort.evaluator;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.gbv.reporting.cohort.definition.GBVLinelistCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluator for patients enrolled in covid treatment program: CCA
 */
@Handler(supports = { GBVLinelistCohortDefinition.class })
public class GBVLinelistCohortDefinitionEvaluator implements EncounterQueryEvaluator {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	EvaluationService evaluationService;
	
	@Override
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);
		
		String qry ="select encounter_id from kenyaemr_etl.etl_gbv_consenting where date(visit_date) between date(:startDate) and date(:endDate);";


		/*String qry = "select t.patient_id from (select\n" +
				"\te.uuid,\n" +
				"\te.encounter_id as encounter_id,\n" +
				"\te.visit_id as visit_id,\n" +
				"\te.patient_id,\n" +
				"\te.location_id,\n" +
				"\tdate(e.encounter_datetime) as visit_date,\n" +
				"\te.creator as encounter_provider,\n" +
				"\te.date_created as date_created,\n" +
				"    max(if(o.concept_id=165176,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as medical_examination,\n" +
				"    max(if(o.concept_id=161934,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as collect_sample,\n" +
				"    max(if(o.concept_id=165180,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as provide_evidence,\n" +
				"     max(if(o.concept_id=167018,o.value_text,null)) as client_signature,\n" +
				"    max(if(o.concept_id=165143,o.value_text,null)) as witness_name,\n" +
				"    max(if(o.concept_id=166847,o.value_text,null)) as witness_signature,\n" +
				"    max(if(o.concept_id=1473,o.value_text,null)) as provider_name,\n" +
				"    max(if(o.concept_id=163258,o.value_text,null)) as provider_signature,\n" +
				"    max(if(o.concept_id=1711,date(o.value_datetime),null)) as date_consented,\n" +
				"    e.voided as voided\n" +
				"from encounter e\n" +
				"\tinner join person p on p.person_id=e.patient_id and p.voided=0\n" +
				"\tinner join\n" +
				"\t(\n" +
				"\t\tselect form_id from form where\n" +
				"\t\t\tuuid in('d720a8b3-52cc-41e2-9a75-3fd0d67744e5')\n" +
				"\t) f on f.form_id=e.form_id\n" +
				"\tleft outer join obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
				"\tand o.concept_id in (165176,161934,165180,167018,165143,166847,1473,163258,1711)\n" +
				"where e.voided=0\n" +
				"group by e.patient_id, e.encounter_id)t;";  */

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		builder.addParameter("endDate", endDate);
		builder.addParameter("startDate", startDate);
		
		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
	}
	
}
