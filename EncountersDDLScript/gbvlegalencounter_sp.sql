USE openmrs;
-- Populate etl GBV legal forms:
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_legal_encounter; 
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_legal_encounter()
BEGIN
SELECT "Processing GBV legal data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_legal(
                uuid ,
                encounter_id ,
                visit_id,
                patient_id ,
                location_id ,
                visit_date ,
                encounter_provider,
                date_created ,
                ob_number,
                police_station_reported_to,
                perpetrator,
                investigating_officer,
                investigating_officer_phonenumber,
                nature_of_action_taken,
                action_taken_description,
                referral_from_gbvrc,
                in_court ,
                criminal_suit_no ,
                case_details,
                alternate_contact_name,
                alternate_phonenumber,
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
	max(if(o.concept_id=167133,o.value_text,null)) as ob_number,
	max(if(o.concept_id=165262,o.value_text,null)) as police_station_reported_to,
	max(if(o.concept_id=165230,o.value_text,null)) as perpetrator,
	max(if(o.concept_id=167018,o.value_text,null)) as investigating_officer,
	max(if(o.concept_id=163152,o.value_text,null)) as investigating_officer_phonenumber,
    max(if(o.concept_id=165205,(case o.value_coded when 118935 then "P3 Filled" when 118688 then "In court"
        when 141537 then "ADR" when 145691 then "Case dropped" when 127910 then "Settled out of court"
        when 126582 then "Alternative Justice system" when 147944 then "Conviction"
        when 161636 then "Unreported case" else "" end),null)) as nature_of_action_taken,
    max(if(o.concept_id=166511,o.value_text,null)) as action_taken_description,
    max(if(o.concept_id=1562,(case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end),null)) as in_court,
    max(if(o.concept_id=162086,o.value_text,null)) as criminal_suit_no,
    max(if(o.concept_id=161011,o.value_text,null)) as case_details,
    max(if(o.concept_id=163258,o.value_text,null)) as alternate_contact_name,
    max(if(o.concept_id=159635,o.value_text,null)) as alternate_phonenumber,
    e.voided as voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join
	(
		select form_id, uuid,name from form where
			uuid in('d0c36426-4503-4236-ab5d-39bff77f2b50')
	) f on f.form_id=e.form_id
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (166511,165205,167133,165262,165230,167018,163152,162086,161011,159635,163258,161103,1562)
where e.voided=0
group by e.patient_id, e.encounter_id;

SELECT "Completed processing GBV legal encounter data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;