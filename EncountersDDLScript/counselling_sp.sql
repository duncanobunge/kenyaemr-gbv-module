USE openmrs;
-- Populate etl counsellingencounter :
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_counsellingencounter;
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_counsellingencounter()
BEGIN
SELECT "Processing GBV counsellingencounter data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_counsellingencounter(
                                        uuid,
                                        encounter_id,
                                        visit_id,
                                        patient_id,
                                        location_id,
                                        visit_date,
                                        encounter_provider,
                                        date_created,
                                        prc_number,
                                        type_of_exposure,
                                        visit_no,
                                        presenting_issue,
                                        emerging_issue,
                                        hiv_test_result,
                                        plan_of_action,
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
	max(if(o.concept_id=1646,o.value_text,null)) as prc_number,
    max(if(o.concept_id=167165,(case o.value_coded when 158358 then "physical violence" when 118688 then "emotional violence"
     when 167085 then "sgbv" when 164843 then "occupational exposure" when 164837 then "non-occupational exposure"
     else "" end),null)) as type_of_exposure,

   max(if(o.concept_id=1724,(case o.value_coded when 162080 then "Initial visit" when 1722 then "2nd visit"
        when 165307 then "3rd visit" when 1721 then "4th visit" when 1723 then "5th visit"
        else "" end),null)) as visit_no,
    max(if(o.concept_id=165138,o.value_text,null)) as presenting_issue,
    max(if(o.concept_id=160629,o.value_text,null)) as emerging_issue,
    max(if(o.concept_id=165171,(case o.value_coded when 703 then "Reactive" when 664 then "Non reactive"  else "" end),null)) as hiv_test_result,
    max(if(o.concept_id=163104,o.value_text,null)) as plan_of_action,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as tca_date,
    e.voided as voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join
	(
		select form_id from form where
			uuid in('"e983d758-5adf-4917-8172-0f4be4d8116a')
	) f on f.form_id=e.form_id
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0
	and o.concept_id in (1646,167165,1724,163104,165138,160629,160753)
where e.voided=0
group by e.patient_id, e.encounter_id;
SELECT "Completed processing GBV counsellingencounter  data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;