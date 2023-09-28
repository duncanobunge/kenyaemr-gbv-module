USE openmrs;
-- populate etl gbv pepmanagementforsurvivor:
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_pepmanagementforsurvivor; 
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_pepmanagementforsurvivor()
BEGIN
SELECT "Processing gbv_pepmanagementforsurvivor test data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_pepmanagementforsurvivor(
      uuid,
      encounter_id,
      visit_id ,
      patient_id ,
      location_id ,
      visit_date,
      encounter_provider,
      date_created,
      date_reported,
      prc_number,
      weight,
      height,
      lmp,
      type_of_violence,
      specify_type_of_violence,
      type_of_assault,
      specify_type_of_assault,
      date_time_incidence,
      perpetrator_name,
      relation_to_perpetrator,
      compulsory_hiv_test,
      compulsory_hiv_test_result,
      perpetrator_file_number,
      state_of_patient ,
      state_of_patient_clothing ,
      examination_genitalia ,
      other_injuries ,
      high_vagina_anal_swab_result,
      RPR_VDRL_result,
      hiv_pre_test_counselling_result,
      given_pep,
      referred_to_psc,
      PDT_result,
      emergency_pills,
      emergency_pills_specify,
      sti_prophylaxis_trx,
      sti_prophylaxis_trx_specify,
      pep_regimen,
      pep_regimen_specify,
      starter_pack_given,
      date_given,
      HBsAG_result,
      LFTs_ALT_result,
      cretinine_result,
      other_test_specify_result,
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
	max(if(o.concept_id=166848,date(o.value_datetime),null)) as date_reported,
	max(if(o.concept_id=165416,o.value_text,null)) as prc_number,
	max(if(o.concept_id=5089,o.value_numeric,null)) as weight,
	max(if(o.concept_id=5090,o.value_numeric,null)) as height,
	max(if(o.concept_id=1427,date(o.value_datetime),null)) as lmp,
	max(if(o.concept_id=165205,(case o.value_coded when 127910 then "Rape" when 145691 then "Defilement"
	when 5622 then "Other" else "" end),null)) as type_of_violence,
	max(if(o.concept_id=165138,o.value_text,null)) as specify_type_of_violence,
	max(if(o.concept_id=123160,(case o.value_coded when 123385 then "Vaginal" when 148895 then "Anal"
	when 5622 then "Other"  else "" end),null)) as type_of_assault,
	max(if(o.concept_id=165145,o.value_text,null)) as specify_type_of_assault,
	max(if(o.concept_id=165349,o.value_datetime,null)) as date_time_incidence,
    max(if(o.concept_id=165230,o.value_text,null)) as perpetrator_name,
    max(if(o.concept_id=167214,o.value_text,null)) as relation_to_perpetrator,
    max(if(o.concept_id=164848,(case o.value_coded when 1065 then "Done" when 1066 then "Not Done" else "" end),null)) as compulsory_hiv_test,
	max(if(o.concept_id=159427,(case o.value_coded when 703 then "Not Reactive" when 664 then "Reactive" else "" end),null)) as compulsory_hiv_test_result,
    max(if(o.concept_id=1639,o.value_text,null)) as perpetrator_file_number,
    max(if(o.concept_id=163042,o.value_text,null)) as state_of_patient,
    max(if(o.concept_id=163045,o.value_text,null)) as state_of_patient_clothing,
    max(if(o.concept_id=160971,o.value_text,null)) as examination_genitalia,
    max(if(o.concept_id=165092,o.value_text,null)) as other_injuries,
    max(if(o.concept_id=163045,o.value_text,null)) as state_of_patient_clothing,
    max(if(o.concept_id=166141,(case o.value_coded when 703 then "Reactive" when 664 then "Non Reactive"
    when 1138 then "Indeterminate" else "" end),null)) as high_vagina_anal_swab_result,
    max(if(o.concept_id=299,(case o.value_coded when 1229 then "Non Reactive" when 1228 then "Reactive"
    else "" end),null)) as RPR_VDRL_result,
    max(if(o.concept_id=163760,(case o.value_coded when 664 then "Non Reactive" when 703 then "Reactive"
    else "" end),null)) as hiv_pre_test_counselling_result,
    max(if(o.concept_id=165171,(case o.value_coded when 1065 then "Yes" else "" end),null)) as given_pep,
    max(if(o.concept_id=165270,(case o.value_coded when 1065 then "Yes" else "" end),null)) as referred_to_psc,
    max(if(o.concept_id=160888,(case o.value_coded when 664 then "Non Reactive" when 703 then "Reactive"
            else "" end),null)) as PDT_result,
    max(if(o.concept_id=165167,(case o.value_coded when 1065 then "Issued" when 1066 then "Not Issued"
        else "" end),null)) as emergency_pills,
    max(if(o.concept_id=160138,o.value_text,null)) as emergency_pills_specify,
    max(if(o.concept_id=165200,(case o.value_coded when 166536 then "STI prophylaxis"
    when 1065 then "STI Treatment" when 1066 then "Not Issued" else "" end),null)) as sti_prophylaxis_trx,
    max(if(o.concept_id=160953,o.value_text,null)) as sti_prophylaxis_trx_specify,
    max(if(o.concept_id=164845,(case o.value_coded when 1065 then "Issued" when 1066 then "Not Issued"
    else "" end),null)) as pep_regimen,
    max(if(o.concept_id=160954,o.value_text,null)) as pep_regimen_specify,
    max(if(o.concept_id=1263,(case o.value_coded when 1065 then "Issued" when 1066 then "Not Issued"
    else "" end),null)) as starter_pack_given,
    max(if(o.concept_id=166865,date(o.value_datetime),null)) as date_given,
    max(if(o.concept_id=161472,(case o.value_coded when 664 then "Non Reactive" when 1066 then "Reactive"
        else "" end),null)) as HBsAG_result,
    max(if(o.concept_id=654,o.value_numeric,null)) as LFTs_ALT_result,
    max(if(o.concept_id=790,o.value_numeric,null)) as cretinine_result,
    max(if(o.concept_id=160987,o.value_text,null)) as  other_test_specify_result,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as tca_date,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid in ('f44b2405-226b-47c4-b98f-b826ea4725ae')
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in 
    (166848,165416,5090,5089,1427,165205,165138,123160,165349,165230,167214,164848,159427,
	1639,163042,163045,160971,165092,166141,299,163760,165171,165270,160888,165167,160138,160968,165200,
	160953,164845,160954,1263,166865,161472,654,790,160987,160753)
where e.voided=0
group by e.patient_id, e.encounter_id;

SELECT "Completed processing gbv_pepmanagementforsurvivor data ", CONCAT("Time: ", NOW());
END $$

DELIMITER ;