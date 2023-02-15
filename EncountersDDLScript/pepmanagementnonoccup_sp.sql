USE openmrs;
-- Populate etl_gbv_pepmanagement_nonoccupationalexposure--
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_pepmanagement_nonoccupationalexposure;
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_pepmanagement_nonoccupationalexposure()
BEGIN
SELECT "Processing gbv_pepmanagement_nonoccupationalexposure data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_pepmanagement_nonoccupationalexposure(
                  uuid,
                  encounter_id,
                  visit_id,
                  patient_id,
                  location_id,
                  visit_date,
                  encounter_provider,
                  date_created,
                  ocn_number,
                  weight,
                  height,
                  type_of_exposure,
                  specify_other_exposure,
                  hiv_test_result,
                  starter_pack_given,
                  pep_regimen,
                  pep_regimen_specify,
                  HBsAG_result,
                  LFTs_ALT_result,
                  creatinine_result,
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
	max(if(o.concept_id=162086,o.value_text,null)) as ocn_number,
    max(if(o.concept_id=5089,o.value_numeric,null)) as weight,
    max(if(o.concept_id=5090,o.value_numeric,null)) as height,
    max(if(o.concept_id=165060, case o.value_coded when 165059 then 'Condom burst' when 127910 then 'Rape'
    when 159218 then 'unprotected sex' when 147273 then 'Human bite' when 137655 then 'Cut wound' when 1536 then 'Needle prick'
    when 145691 then Defilement when 5622 then other else '' end,null)) as type_of_exposure,
    max(if(o.concept_id=165138,o.value_text,null)) as specify_other_exposure,
    max(if(o.concept_id=159427,case o.value_coded when 703 then "Reactive" when 664 then "Non Reactive" else "" end,null)) as hiv_test_result,
    max(if(o.concept_id=1263,case o.value_coded when 1065 then "Yes" when 1066 then "No" else "" end,null)) as starter_pack_given,
    max(if(o.concept_id=166655,case o.value_coded when 162326 then "RegCat:Children 25-34.9kg"
    when 162323 then "RegCat:Adults and Children > 34.9Kg"
    when 162321 then "RegCat:Children 20-24.9kg"  when 162322 then "RegCat:Children < 20kg"
     else "" end,null)) as pep_regimen,
    max(if(o.concept_id=163104,o.value_text,null)) as pep_regimen_specify,
    max(if(o.concept_id=161472,(case o.value_coded when 664 then "Non Reactive" when 1066 then "Reactive"
            else "" end),null)) as HBsAG_result,
    max(if(o.concept_id=654,o.value_numeric,null)) as LFTs_ALT_result,
    max(if(o.concept_id=790,o.value_numeric,null)) as creatinine_result,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as tca_date,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid = '92de9269-6bb4-4c24-8ec9-870aa2c64b5a'
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (162086,165416,5089,
	5090,165060,165138,159427,1263,166655,163104,161472,654,790,160753)
where e.voided=0
group by e.patient_id, e.encounter_id;


SELECT "Completed processing gbv_pepmanagement_nonoccupationalexposure data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;