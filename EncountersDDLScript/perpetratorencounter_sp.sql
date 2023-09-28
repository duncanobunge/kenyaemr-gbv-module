USE openmrs;
-- populate etl perpetratorencounter consent:
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_perpetratorencounter;
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_perpetratorencounter()
BEGIN
SELECT "Processing GBV perpetrator encounter data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_perpetratorencounter(
                              uuid,
                              encounter_id ,
                              visit_id,
                              patient_id,
                              location_id,
                              visit_date,
                              encounter_provider,
                              date_created,
                              perpetrator_number,
                              phonenumber,
                              residence,
                              occupation,
                              other_occupation_specify,
                              presenting_issue,
                              action_plan_presenting_issue,
                              pep_given,
                              pep_given_no,
                              ecp_given,
                              ecp_given_no,
                              sti_treatment_given,
                              voided
)
select
	e.uuid,
	e.encounter_id as encounter_id,
	e.visit_id as visit_id,
	e.patient_id,
	e.location_id,
	date(e.encounter_datetime) as visit_date,
	e.creator as encounter_provider,
	e.date_created as date_created,
	max(if(o.concept_id=163151,o.value_text,null)) as perpetrator_number,
	max(if(o.concept_id=163152,o.value_text,null)) as phonenumber,
	max(if(o.concept_id=167131,o.value_text,null)) as residence,
    max(if(o.concept_id=1542,(case o.value_coded when 123801 then "unemployed" when 1538 then "farmer"
     when 1539 then "trader" when 1540 then "employee" when 159464 then "Housework"
     when 159465 then "Student" when 159466 then "Driver" when 162946 then "Teacher"
     when 1067 then "Other" else "" end),null)) as occupation,
    max(if(o.concept_id=160632,o.value_text,null)) as other_occupation_specify,
    max(if(o.concept_id=165138,o.value_text,null)) as presenting_issue,
    max(if(o.concept_id=163104,o.value_text,null)) as action_plan_presenting_issue,
    max(if(o.concept_id=165171,(case o.value_coded when 1065 then "Yes" when 1066 then "No"  else "" end),null)) as pep_given,
    max(if(o.concept_id=162169,o.value_text,null)) as pep_given_no,
    max(if(o.concept_id=160570,(case o.value_coded when 1065 then "Yes" when 1066 then "No"  else "" end),null)) as ecp_given,
    max(if(o.concept_id=160845,o.value_text,null)) as ecp_given_no,
    max(if(o.concept_id=165200,(case o.value_coded when 1065 then "Yes" when 1066 then "No"  else "" end),null)) as sti_treatment_given,
    e.voided as voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join
	(
		select form_id from form where
			uuid in('f37d7e0e-95e8-430d-96a3-8e22664f74d6')
	) f on f.form_id=e.form_id
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0
	and o.concept_id in (163151,163152,167131,1542,160632,165138,163104,165171,162169,160570,160845,165200)
where e.voided=0
group by e.patient_id, e.encounter_id;
SELECT "Completed processing GBV perpetrator encounter data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;