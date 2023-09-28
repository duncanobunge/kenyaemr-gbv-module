use openmrs;
-- Populate etl gbv consenting:
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_consenting;
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_consenting()
BEGIN
SELECT "Processing GBV PRC Consenting data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_consenting(
                  uuid,
                  encounter_id,
                  visit_id,
                  patient_id,
                  location_id,
                  visit_date,
                  encounter_provider,
                  date_created,
                  medical_examination,
                  collect_sample,
                  provide_evidence,
                  client_signature,
                  witness_name,
                  witness_signature,
                  provider_name,
                  provider_signature,
                  date_consented,
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
    max(if(o.concept_id=165176,(case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end),null)) as medical_examination,
    max(if(o.concept_id=161934,(case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end),null)) as collect_sample,
    max(if(o.concept_id=165180,(case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end),null)) as provide_evidence,
     max(if(o.concept_id=167018,o.value_text,null)) as client_signature,
    max(if(o.concept_id=165143,o.value_text,null)) as witness_name,
    max(if(o.concept_id=166847,o.value_text,null)) as witness_signature,
    max(if(o.concept_id=1473,o.value_text,null)) as provider_name,
    max(if(o.concept_id=163258,o.value_text,null)) as provider_signature,
    max(if(o.concept_id=1711,date(o.value_datetime),null)) as date_consented,
    e.voided as voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join
	(
		select form_id from form where
			uuid in('d720a8b3-52cc-41e2-9a75-3fd0d67744e5')
	) f on f.form_id=e.form_id
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0
	and o.concept_id in (165176,161934,165180,167018,165143,166847,1473,163258,1711)
where e.voided=0
group by e.patient_id, e.encounter_id;
SELECT "Completed processing GBV PRC consent data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;