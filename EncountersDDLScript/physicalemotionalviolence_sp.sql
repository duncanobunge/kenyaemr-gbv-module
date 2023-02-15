USE openmrs;
-- Populate etl gbv_physicalemotional_violence:GBVRC
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_physicalemotional_violence;
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_physicalemotional_violence()
BEGIN
SELECT "Processing etl_gbv_physicalemotional_violence data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_physicalemotional_violence(
            uuid,
            encounter_id,
            visit_id,
            patient_id ,
            location_id ,
            visit_date ,
            encounter_provider,
            date_created ,
            gbv_no,
            referred_from,
            referred_from_specify,
            type_of_violence,
            trauma_counselling,
            trauma_counselling_comment,
            sti_screening_tx,
            sti_screening_tx_comment,
            lab_test,
            lab_test_comment,
            rapid_hiv_test,
            rapid_hiv_test_comment,
            legal_counsel,
            legal_counsel_comment,
            child_protection,
            child_protection_comment,
            referred_to,
            scheduled_appointment_date,
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
	max(if(o.concept_id=165416,o.value_text,null)) as gbv_no,
	max(if(o.concept_id=1272,case o.value_coded when 5486 then "police" when 1275 then "Health facility"
	when 978 then "Self" when 160543 then "Community outreach" when 5622 then "other"
	else "" end,null)) as referred_from,
    max(if(o.concept_id=165092,o.value_text,null)) as referred_from_specify,
    max(if(o.concept_id=165205,case o.value_coded when 158358 then "physical violence" when 117510 then "Emotional"
	 else "" end,null)) as type_of_violence,
    max(if(o.concept_id=165184,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as trauma_counselling,
	max(if(o.concept_id=161011,o.value_text,null)) as trauma_counselling_comment,
	max(if(o.concept_id=165172,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as sti_screening_tx,
	max(if(o.concept_id=160632,o.value_text,null)) as sti_screening_tx_comment,
	max(if(o.concept_id=167229,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as lab_test,
    max(if(o.concept_id=165435,o.value_text,null)) as lab_test_comment,
    max(if(o.concept_id=164848,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as rapid_hiv_test,
    max(if(o.concept_id=164963,o.value_text,null)) as rapid_hiv_test_comment,
    max(if(o.concept_id=1379,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as legal_counsel,
    max(if(o.concept_id=165426,o.value_text,null)) as legal_counsel_comment,
    max(if(o.concept_id=161601,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as child_protection,
    max(if(o.concept_id=167214,o.value_text,null)) as child_protection_comment,
    max(if(o.concept_id=1885,case o.value_coded when 165192 then "police" when 165227 then "safe space" when 165227 then "ADR" else "" end,null)) as referred_to,
	e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid ="9d21275a-7657-433a-b305-a736423cc496"
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (165416,1272,165092,165205,160632,165184,161011,165172,160632,167229,165435
	,164848,164963,1379,165426,161601,167214,1885)
where e.voided=0
group by e.patient_id, e.encounter_id;


SELECT "Completed processing physical_emotional_violence data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;