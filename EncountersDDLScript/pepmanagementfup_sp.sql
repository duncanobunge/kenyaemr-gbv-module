USE openmrs;
-- Populate etl gbv_pepmanagement_followup:GBVRC
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_pepmanagement_followup; 
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_pepmanagement_followup()
BEGIN
SELECT "Processing etl_gbv_pepmanagement_followup data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_pepmanagement_followup(
                          uuid,
                          encounter_id,
                          visit_id,
                          patient_id,
                          location_id,
                          visit_date,
                          encounter_provider,
                          date_created,
                          visit_no,
                          HBsAG_test,
                          HBsAG_result,
                          hiv_test,
                          hiv_test_result,
                          lfts_test_result,
                          cretinine_test_result,
                          specify_other_test,
                          pep_completed,
                          reason_for_incomplete_pep,
                          patient_assessment,
                          hiv_serology ,
                          tca_date,
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
	max(if(o.concept_id=1724,case o.value_coded when 1722 then "2nd visit" when 165307 then "3rd visit"
	when 1721 then "4th visit" when 1723 then "5th visit" else "" end,null)) as visit_no,
    max(if(o.concept_id=161472,case o.value_coded when 158358 then "physical violence" when 117510 then "Emotional"
	 else "" end,null)) as type_of_violence,
	max(if(o.concept_id=161472,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as HBsAG_test,
    max(if(o.concept_id=165384,case o.value_coded when 664 then "Non reactive" when 703 then "Positive" else "" end,null)) as HBsAG_result,
    max(if(o.concept_id=164848,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as hiv_test,
    max(if(o.concept_id=159427,case o.value_coded when 664 then "Non reactive" when 703 then "Positive" else "" end,null)) as hiv_test_result,
    max(if(o.concept_id=654,o.value_text,null)) as lfts_test_result,
    max(if(o.concept_id=790,o.value_text,null)) as cretinine_test_result,
    max(if(o.concept_id=160689,o.value_text,null)) as specify_other_test,
    max(if(o.concept_id=165171,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as pep_completed,
    max(if(o.concept_id=161011,o.value_text,null)) as reason_for_incomplete_pep,
    max(if(o.concept_id=166664,case o.value_coded when 664 then "Non Reactive" when 703 then "Reactive" else "" end,null)) as hiv_serology,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as tca_date,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid ="839f3bb3-0b93-4afa-a2fc-739fd7012d18"
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (1724,161472,165384,164848,159427,790,654,
	160689,165171,166664,160753)
where e.voided=0
group by e.patient_id, e.encounter_id;

SELECT "Completed processing pepmanagement_followup data ", CONCAT("Time: ", NOW());
END $$

DELIMITER ;