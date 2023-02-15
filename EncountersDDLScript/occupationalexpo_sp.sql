USE openmrs;
-- Populate etl gbv_occupationalexposure--
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_occupationalexposure 
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_occupationalexposure()
BEGIN
SELECT "Processing gbv_occupationalexposure data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.gbv_occupationalexposure(
                     uuid,
                     encounter_id,
                     visit_id,
                     patient_id,
                     location_id,
                     visit_date,
                     encounter_provider,
                     date_created,
                     location_during_exposure,
                     date_time_exposure,
                     cadre,
                     type_of_exposure,
                     specify_other_exposure,
                     severity_exposure,
                     device_causing_exposure,
                     specify_other_device,
                     device_safety,
                     procedure_device_used,
                     description_how_injury,
                     hiv_test_result,
                     HBsAG_result,
                     HCV_result,
                     accident_exposure,
                     pep_last_exposure,
                     how_many_days,
                     reason_incomplete_dose,
                     HB_immunization,
                     reason_no_partial_vaccine,
                     regimen_ARVs_datetime,
                     wish_to_take_PEP,
                     pregnant,
                     breastfeeding,
                     underlying_health_problem,
                     specify_health_problem,
                     hcw_on_medication,
                     preliminary_classification_risk,
                     arv_regimen_post_assessment,
                     date_dispense,
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
    max(if(o.concept_id=1673,o.value_text,null)) as location_during_exposure,
    max(if(o.concept_id=162601,o.value_datetime,null)) as date_time_exposure,
    max(if(o.concept_id=162169,o.value_text,null)) as cadre,
    max(if(o.concept_id=1572, case o.value_coded when 1536 then "Needle prick"
    	when 1588 then "Cut" when 1589 then "Splash on mucosa" when 1601 then "Splash on non intact skin"
    	when 5622 then "other(specify)" else "" end,null)) as type_of_exposure,
    max(if(o.concept_id=160716,o.value_text,null)) as specify_other_exposure,
    max(if(o.concept_id=159559, case o.value_coded when 1499 then "Superficial"
        	when 1500 then "Deep"  else "" end,null)) as severity_exposure,
    max(if(o.concept_id=164204, case o.value_coded when 460 then "Hollow bore needle"
            when 5201 then "Solid needle" when 5243 then "Scapel"
            when 136443 then "Glass" when 5266 then "Other(specify)" else "" end,null)) as device_causing_exposure,
    max(if(o.concept_id=160632,o.value_text,null)) as specify_other_device,
    max(if(o.concept_id=164875, case o.value_coded when 1065 then "Yes"
            	when 1066 then "No"  else "" end,null)) as device_safety,
    max(if(o.concept_id=166635,o.value_text,null)) as procedure_device_used,
    max(if(o.concept_id=1552,o.value_text,null)) as description_how_injury,
    max(if(o.concept_id=159427, case o.value_coded when 664 then "Negative"
         when 703 then "Positive" when 1067 then "Unknown" else "" end,null)) as  hiv_test_result,
    max(if(o.concept_id=161472, case o.value_coded when 664 then "Negative"
             when 703 then "Positive" when 1067 then "Unknown" else "" end,null)) as HBsAG_result,
    max(if(o.concept_id=161471, case o.value_coded when 664 then "Negative"
                 when 703 then "Positive" when 1067 then "Unknown" else "" end,null)) as HCV_result,
    max(if(o.concept_id=1716, case o.value_coded when 1065 then "Yes"
                	when 1066 then "No"  else "" end,null)) as accident_exposure,
    max(if(o.concept_id=165171, case o.value_coded when 1065 then "Yes"
                   	when 1066 then "No"  else "" end,null)) as pep_last_exposure,
    max(if(o.concept_id=166086,o.value_text,null)) as pep_regimen_taken,
    max(if(o.concept_id=165170,o.value_numeric,null)) as how_many_days,
    max(if(o.concept_id=160632,o.value_text,null)) as reason_incomplete_dose,
    max(if(o.concept_id=159430, case o.value_coded when 1065 then "Yes fully (3 doses)"
     when 1067 then "Yes partially (1-2 doses)"	when 1066 then "No"  else "" end,null)) as HB_immunization,
    max(if(o.concept_id=162704,o.value_text,null)) as reason_no_partial_vaccine,
    -- max(if(o.concept_id=160632,o.value_text,null)) as regimen_ARVs_datetime,
    max(if(o.concept_id=164845, case o.value_coded when 1065 then "Yes"
       when 1066 then "No"  else "" end,null)) as wish_to_take_PEP,
    max(if(o.concept_id=1523, case o.value_coded when 1065 then "Yes"
           when 1066 then "No"  else "" end,null)) as pregnant,
    max(if(o.concept_id=5632, case o.value_coded when 1065 then "Yes"
           when 1066 then "No"  else "" end,null)) as breastfeeding,
    max(if(o.concept_id=164878, case o.value_coded when 1065 then "Yes"
               when 1066 then "No"  else "" end,null)) as underlying_health_problem,
    max(if(o.concept_id=163042,o.value_text,null)) as specify_health_problem,
    max(if(o.concept_id=1417, case o.value_coded when 1065 then "Yes"
                   when 1066 then "No"  else "" end,null)) as hcw_on_medication,
    max(if(o.concept_id=165053, case o.value_coded when 1065 then "Low risk"
                       when 1066 then "High risk"  else "" end,null)) as  preliminary_classification_risk,
    max(if(o.concept_id=163104,o.value_text,null)) as arv_regimen_post_assessment,
    max(if(o.concept_id=166865,date(o.value_datetime),null)) as date_dispense,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as tca_date,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid = '11a880ec-cbb6-40c8-811d-2c9e057c534a'
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in
	(162169,1673,162601,1572,160716,159559,164204,160632,164875,166635,1552,159427,161472,161471,1716,165171,166086,165170,
	160632,159430,162704,164845,1523,5632,164878,163042,1417,165053,163104,166865)
where e.voided=0
group by e.patient_id, e.encounter_id;


SELECT "Completed processing gbv_occupationalexposure data ", CONCAT("Time: ", NOW());
END $$
DELIMITER ;