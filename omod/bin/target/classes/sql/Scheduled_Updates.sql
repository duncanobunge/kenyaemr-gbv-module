SET @OLD_SQL_MODE=@@SQL_MODE $$
SET SQL_MODE='' $$

-- --------------------------------------------------- adding gbv form procedures ---------------------------------------------
-- gbvrc_consent_prc data
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_consenting $$
CREATE PROCEDURE sp_update_etl_gbv_consenting(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing gbv_consenting data ", CONCAT("Time: ", NOW());
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
where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
    group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE
                             encounter_provider=VALUES(encounter_provider),
                             visit_date=VALUES(visit_date),
                             medical_examination=VALUES(medical_examination),
                             collect_sample=VALUES(collect_sample),
                             provide_evidence=VALUES(provide_evidence),
                             client_signature=VALUES(client_signature),
                             witness_name=VALUES(witness_name),
                             witness_signature=VALUES(witness_signature),
                             provider_name=VALUES(provider_name),
                             provider_signature=VALUES(provider_signature),
                             date_consented=VALUES(date_consented),
                             voided=VALUES(voided);
    END $$

-- gbv counselling encounter --
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_counsellingencounter $$
CREATE PROCEDURE sp_update_etl_gbv_counsellingencounter(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing gbv_counsellingencounter data ", CONCAT("Time: ", NOW());
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
    where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
    group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE
                             encounter_provider=VALUES(encounter_provider),
                             visit_date=VALUES(visit_date),
                             prc_number=VALUES(prc_number),
                             type_of_exposure=VALUES(type_of_exposure),
                             visit_no=VALUES(visit_no),
                             presenting_issue=VALUES(presenting_issue),
                             emerging_issue=VALUES(emerging_issue),
                             hiv_test_result=VALUES(hiv_test_result),
                             plan_of_action=VALUES(plan_of_action),
                             tca_date=VALUES(tca_date),
                             voided=VALUES(voided);
    END $$
-- sp_update_etl_gbv_perpetratorencounter --
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_perpetratorencounter $$
CREATE PROCEDURE sp_update_etl_gbv_perpetratorencounter(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing sp_update_etl_gbv_perpetratorencounter data ", CONCAT("Time: ", NOW());
    insert into kenyaemr_etl.sp_update_etl_gbv_perpetratorencounter(
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
    where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
    group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE
                             encounter_provider=VALUES(encounter_provider),
                             visit_date=VALUES(visit_date),
                             perpetrator_number=VALUES(perpetrator_number),
                             phonenumber=VALUES(phonenumber),
                             residence=VALUES(residence),
                             occupation=VALUES(occupation),
                             other_occupation_specify=VALUES(other_occupation_specify),
                             presenting_issue=VALUES(presenting_issue),
                             action_plan_presenting_issue=VALUES(action_plan_presenting_issue),
                             pep_given=VALUES(pep_given),
                             pep_given_no=VALUES(pep_given_no),
                             ecp_given=VALUES(ecp_given),
                             ecp_given_no=VALUES(ecp_given_no),
                             sti_treatment_given=VALUES(sti_treatment_given),
                             voided=VALUES(voided);
    END $$

-- gbv_legal--
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_legal $$
CREATE PROCEDURE sp_update_etl_gbv_legal(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing legal data ", CONCAT("Time: ", NOW());
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
    where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
    group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE

                                                      encounter_provider= VALUES(encounter_provider),
                                                      visit_date = VALUES(visit_date),
                                                      ob_number=VALUES(ob_number),
                                                      police_station_reported_to=VALUES(police_station_reported_to),
                                                      perpetrator=VALUES(perpetrator),
                                                      investigating_officer=VALUES(investigating_officer),
                                                      investigating_officer_phonenumber=VALUES(investigating_officer_phonenumber),
                                                      nature_of_action_taken=VALUES(nature_of_action_taken),
                                                      action_taken_description=VALUES(action_taken_description),
                                                      referral_from_gbvrc=VALUES(referral_from_gbvrc),
                                                      in_court=VALUES(in_court),
                                                      criminal_suit_no=VALUES(criminal_suit_no) ,
                                                      case_details=VALUES(case_details),
                                                      alternate_contact_name=VALUES(alternate_contact_name),
                                                      alternate_phonenumber=VALUES(alternate_phonenumber),
                                                      voided=VALUES(voided);

    END $$

-- gbvrc_pepmanagementforsurvivors
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_pepmanagementforsurvivors $$
CREATE PROCEDURE sp_update_etl_gbv_pepmanagementforsurvivors(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing gbv_pepmanagementforsurvivors data ", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_pepmanagementforsurvivors(
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
    max(if(o.concept_id=299,(case o.value_coded when 1229 then "Non Reactive" when 1228 then "Reactive" else "" end),null)) as RPR_VDRL_result,
    max(if(o.concept_id=163760,(case o.value_coded when 664 then "Non Reactive" when 703 then "Reactive" else "" end),null)) as hiv_pre_test_counselling_result,
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
	1639,163042,163045,160971,165092,166141,299,163760,165171,165270,160888,165167,160138,160968,165200,160953,164845,160954,1263,166865,161472,654,790,160987,160753)
where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE

            encounter_provider=VALUES(encounter_provider),
            visit_date=VALUES(visit_date),
                  date_reported=VALUES(date_reported),
                  prc_number=VALUES(prc_number),
                  weight=VALUES(weight),
                  height=VALUES(height),
                  lmp=VALUES(lmp),
                  type_of_violence=VALUES(type_of_violence),
                  specify_type_of_violence=VALUES(specify_type_of_violence),
                  type_of_assault=VALUES(type_of_assault),
                  specify_type_of_assault=VALUES(specify_type_of_assault),
                  date_time_incidence=VALUES(date_time_incidence),
                  perpetrator_name=VALUES(perpetrator_name),
                  relation_to_perpetrator=VALUES(relation_to_perpetrator),
                  compulsory_hiv_test=VALUES(compulsory_hiv_test),
                  compulsory_hiv_test_result=VALUES(compulsory_hiv_test_result),
                  perpetrator_file_number=VALUES(perpetrator_file_number),
                  state_of_patient=VALUES(state_of_patient) ,
                  state_of_patient_clothing=VALUES(state_of_patient_clothing) ,
                  examination_genitalia=VALUES(examination_genitalia) ,
                  other_injuries=VALUES(other_injuries),
                  high_vagina_anal_swab_result=VALUES(high_vagina_anal_swab_result),
                  RPR_VDRL_result=VALUES(RPR_VDRL_result),
                  hiv_pre_test_counselling_result=VALUES(hiv_pre_test_counselling_result),
                  given_pep=VALUES(given_pep),
                  referred_to_psc=VALUES(referred_to_psc),
                  PDT_result=VALUES(PDT_result),
                  emergency_pills=VALUES(emergency_pills),
                  emergency_pills_specify=VALUES(emergency_pills_specify),
                  sti_prophylaxis_trx=VALUES(sti_prophylaxis_trx),
                  sti_prophylaxis_trx_specify=VALUES(sti_prophylaxis_trx_specify),
                  pep_regimen=VALUES(pep_regimen),
                  pep_regimen_specify=VALUES(pep_regimen_specify),
                  starter_pack_given=VALUES(starter_pack_given),
                  date_given=VALUES(date_given),
                  HBsAG_result=VALUES(HBsAG_result),
                  LFTs_ALT_result=VALUES(LFTs_ALT_result),
                  cretinine_result=VALUES(cretinine_result),
                  other_test_specify_result=VALUES(other_test_specify_result),
                  tca_date=VALUES(tca_date),
                  voided=VALUES(voided);
    END $$

-- gbv pep management for nonoccupational exposure
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_pepmanagement_nonoccupationalexposure $$
CREATE PROCEDURE sp_update_etl_gbv_pepmanagement_nonoccupationalexposure(IN last_update_time DATETIME)
BEGIN
    SELECT "Processing gbv_pepmanagement_nonoccupationalexposure data ", CONCAT("Time: ", NOW());
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
where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE

                        encounter_provider=VALUES(encounter_provider),
                        visit_date=VALUES(visit_date),
                        ocn_number=VALUES(ocn_number),
                                          weight=VALUES(weight),
                                          height=VALUES(height),
                                          type_of_exposure=VALUES(type_of_exposure),
                                          specify_other_exposure=VALUES(specify_other_exposure),
                                          hiv_test_result=VALUES(hiv_test_result),
                                          starter_pack_given=VALUES(starter_pack_given),
                                          pep_regimen=VALUES(pep_regimen),
                                          pep_regimen_specify=VALUES(pep_regimen_specify),
                                          HBsAG_result=VALUES(HBsAG_result),
                                          LFTs_ALT_result=VALUES(LFTs_ALT_result),
                                          creatinine_result=VALUES(creatinine_result),
                                          tca_date=VALUES(tca_date),
                                          voided=VALUES(voided);
END $$

-- gbv_linkage
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_linkage $$
CREATE PROCEDURE sp_update_etl_gbv_linkage(IN last_update_time DATETIME)
 BEGIN
    SELECT "Processing gbv_linkage data ", CONCAT("Time: ", NOW());
 insert into kenyaemr_etl.etl_gbv_linkage(
                 uuid,
                 encounter_id ,
                 patient_id  ,
                 location_id ,
                 visit_date,
                 encounter_provider ,
                 date_created ,
                 date_of_violence,
                 nature_of_violence,
                 action_taken ,
                 other_action_taken,
                 any_special_need ,
                 comment_special_need,
                 date_contacted_at_community,
                 number_of_interactive_session,
                 date_referred_GBVRC,
                 date_clinically_seen_at_GBVRC,
                 mobilizer_name,
                 mobilizer_phonenumber ,
                 received_by,
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
	max(if(o.concept_id=165349,date(o.value_datetime),null)) as date_of_violence,
	max(if(o.concept_id=165205,case o.value_coded when 158358 then "Physical"
	when 152370 then "Sexual" when 117510 then "Emotional" when 124824 then "Financial"
	when 141815 then "IPV" else "" end,null)) as nature_of_violence,
    max(if(o.concept_id=159639,case o.value_coded when 161189 then "Go to hospital"
    when 151316 then "Take over the counter medicine" when 167254 then "Reported to local Authority"
    when 165227 then "Moved to safe place" when 165183 then "Received counselling"
    when 148502 then "Perpetrator apprehended" when 5622 then "Other (specify)" else "" end,null)) as action_taken,
    max(if(o.concept_id=166511,o.value_text,null)) as other_action_taken,
    max(if(o.concept_id=161601,case o.value_coded when 118686 then "Elderly"
    when 161597 then "OVC" when 161599 then "Person living with disability"
    when 166043 then "Adolescent" when 5622 then "Other (specify)" else "" end,null)) as any_special_need,
    max(if(o.concept_id=161011,o.value_text,null)) as comment_special_need,
    max(if(o.concept_id=165144,date(o.value_datetime),null)) as date_contacted_at_community,
    max(if(o.concept_id=1639,o.value_numeric,null)) as number_of_interactive_session,
    max(if(o.concept_id=161560,date(o.value_datetime),null)) as date_referred_GBVRC,
    max(if(o.concept_id=163260,date(o.value_datetime),null)) as date_clinically_seen_at_GBVRC,
    max(if(o.concept_id=164141,o.value_text,null)) as mobilizer_name,
    max(if(o.concept_id=159635,o.value_text,null)) as mobilizer_phonenumber,
    max(if(o.concept_id=1473,o.value_text,null)) as received_by,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid = 'f760e38c-3d2f-4a5d-aa3d-e9682576efa8'
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in
	(166511,165349,165205,159639,161601,161011,165144,1639,161560,163260,164141,159635,1473)
where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
group by e.patient_id, e.encounter_id
ON DUPLICATE KEY UPDATE

                       encounter_provider=VALUES(encounter_provider),
                       visit_date=VALUES(visit_date),
                       date_of_violence=VALUES(date_of_violence),
                       nature_of_violence=VALUES(nature_of_violence),
                       action_taken=VALUES(action_taken) ,
                       other_action_taken=VALUES(other_action_taken),
                       any_special_need=VALUES(any_special_need),
                       comment_special_need=VALUES(comment_special_need),
                       date_contacted_at_community=VALUES(date_contacted_at_community),
                       number_of_interactive_session=VALUES(number_of_interractive_session),
                       date_referred_GBVRC=VALUES(date_referred_GBVRC),
                       date_clinically_seen_at_GBVRC=VALUES(date_clinically_seen_at_GBVRC),
                       mobilizer_name=VALUES(mobilizer_name),
                       mobilizer_phonenumber=VALUES(mobilizer_phonenumber),
                       received_by=VALUES(received_by),
                       voided=VALUES(voided);
END $$

-- etl_gbv_occupationalexposure
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_occupationalexposure $$
CREATE PROCEDURE sp_update_etl_gbv_occupationalexposure(IN last_update_time DATETIME)
BEGIN
SELECT "Processing etl_gbv_occupationalexposure data ", CONCAT("Time: ", NOW());
INSERT INTO kenyaemr_etl.etl_gbv_occupationalexposure(
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
SELECT
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
    where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
group by e.patient_id, e.encounter_id
ON DUPLICATE KEY UPDATE

                                            encounter_provider=VALUES(encounter_provider),
                                            visit_date=VALUES(visit_date),
                                            date_created=VALUES(date_created),
                                            location_during_exposure=VALUES(location_during_exposure),
                                            date_time_exposure=VALUES(date_time_exposure),
                                            cadre=VALUES(cadre),
                                            type_of_exposure=VALUES(type_of_exposure),
                                            specify_other_exposure=VALUES(specify_other_device),
                                            severity_exposure=VALUES(severity_exposure),
                                            device_causing_exposure=VALUES(device_causing_exposure),
                                            specify_other_device=VALUES(specify_other_device),
                                            device_safety=VALUES(device_safety),
                                            procedure_device_used=VALUES(procedure_device_used),
                                            description_how_injury=VALUES(description_how_injury),
                                            hiv_test_result=VALUES(hiv_test_result),
                                            HBsAG_result=VALUES(HBaAG_result),
                                            HCV_result=VALUES(HCV_result),
                                            accident_exposure=VALUES(accident_exposure),
                                            pep_last_exposure=VALUES(pep_last_exposure),
                                            how_many_days=VALUES(how_many_days),
                                            reason_incomplete_dose=VALUES(reason_incomplete_dose),
                                            HB_immunization=VALUES(HB_immunization),
                                            reason_no_partial_vaccine=VALUES(reason_no_partial_vaccine),
                                            regimen_ARVs_datetime=VALUES(regimen_ARVs_datetime),
                                            wish_to_take_PEP=VALUES(wish_to_take_PEP),
                                            pregnant=VALUES(pregnant),
                                            breastfeeding=VALUES(breastfeeding),
                                            underlying_health_problem=VALUES(underlying_health_problem),
                                            specify_health_problem=VALUES(specify_health_problem),
                                            hcw_on_medication=VALUES(hcw_on_medication),
                                            preliminary_classification_risk=VALUES(preliminary_classification_risk),
                                            arv_regimen_post_assessment=VALUES(arv_regimen_post_assessment),
                                            date_dispense=VALUES(date_dispense),
                                            tca_date=VALUES(tca_date),
                                            voided=VALUES(voided);
END $$


-- gbv_pepfollow up
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_pepmanagement_followup $$
CREATE PROCEDURE sp_update_etl_gbv_pepmanagement_followup(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing etl_gbv_pepmanagement_followup data ", CONCAT("Time: ", NOW());
INSERT INTO kenyaemr_etl.etl_gbv_pepmanagement_followup(
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
    SELECT
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
        max(if(o.concept_id=161103,o.value_text,null)) as rri_test,
    	max(if(o.concept_id=165092,o.value_text,null)) as pr_test,
        max(if(o.concept_id=165205,case o.value_coded when 158358 then "physical violence" when 117510 then "Emotional"
    	when 5622 then "other" else "" end,null)) as type_of_violence,
        max(if(o.concept_id = 160632,o.value_text,null)) as specify_type_of_violence,
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
    	inner join form f on f.form_id=e.form_id and f.uuid = 'a0943862-f0fe-483d-9f11-44f62abae063'
    	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (165416,1272,165092,165205,160632,165184,161011,165172,160632,167229,165435
    	,164848,164963,1379,165426,161601,167214,1885)
    where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
    group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE
                                              visit_date=VALUES(visit_date) ,
                                              encounter_provider=VALUES(encounter_provider),
                                              visit_no=VALUES(visit_no),
                                              HBsAG_test=VALUES(HBsAG_test),
                                              HBsAG_result=VALUES(HBsAG_result),
                                              hiv_test=VALUES(hiv_test),
                                              hiv_test_result=VALUES(hiv_test_result),
                                              lfts_test_result=VALUES(lfts_test_result),
                                              cretinine_test_result=VALUES(cretinine_test_result),
                                              specify_other_test=VALUES(specify_other_test),
                                              pep_completed=VALUES(pep_completed),
                                              reason_for_incomplete_pep=VALUES(reason_for_incomplete_pep),
                                              patient_assessment=VALUES(patient_assessment),
                                              hiv_serology=VALUES(hiv_serology),
                                              tca_date=VALUES(tca_date),
                                              voided=VALUES(voided);
    END $$

-- gbv_physicalemotional_violence
DROP PROCEDURE IF EXISTS sp_update_etl_gbv_physicalemotional_violence $$
CREATE PROCEDURE sp_update_etl_gbv_physicalemotional_violence(IN last_update_time DATETIME)
  BEGIN
    SELECT "Processing gbv_physicalemotional_violence data ", CONCAT("Time: ", NOW());
INSERT INTO kenyaemr_etl.etl_gbv_physicalemotional_violence(
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
   SELECT
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
        max(if(o.concept_id=161103,o.value_text,null)) as rri_test,
    	max(if(o.concept_id=165092,o.value_text,null)) as pr_test,
        max(if(o.concept_id=165205,case o.value_coded when 158358 then "physical violence" when 117510 then "Emotional"
    	when 5622 then "other" else "" end,null)) as type_of_violence,
        max(if(o.concept_id = 160632,o.value_text,null)) as specify_type_of_violence,
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
    	inner join form f on f.form_id=e.form_id and f.uuid = 'a0943862-f0fe-483d-9f11-44f62abae063'
    	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (165416,1272,165092,165205,160632,165184,161011,165172,160632,167229,165435
    	,164848,164963,1379,165426,161601,167214,1885)
    where e.voided=0 and (e.date_created >= last_update_time
            or e.date_changed >= last_update_time
            or e.date_voided >= last_update_time
            or o.date_created >= last_update_time
            or o.date_voided >= last_update_time)
    group by e.patient_id, e.encounter_id
    ON DUPLICATE KEY UPDATE
                    visit_date=VALUES(visit_date) ,
                    encounter_provider=VALUES(encounter_provider),
                    gbv_no=VALUES(gbv_no),
                    referred_from=VALUES(referred_from),
                    referred_from_specify=VALUES(referred_from_specify),
                    type_of_violence=VALUES(type_of_violence),
                    trauma_counselling=VALUES(trauma_counselling),
                    trauma_counselling_comment=VALUES(trauma_counselling_comment),
                    sti_screening_tx=VALUES(sti_screening_tx),
                    sti_screening_tx_comment=VALUES(sti_screening_tx_comment),
                    lab_test=VALUES(lab_test),
                    lab_test_comment=VALUES(lab_test_comment),
                    rapid_hiv_test=VALUES(rapid_hiv_test),
                    rapid_hiv_test_comment=VALUES(rapid_hiv_test_comment),
                    legal_counsel=VALUES(legal_counsel),
                    legal_counsel_comment=VALUES(legal_counsel_comment),
                    child_protection=VALUES(child_protection),
                    child_protection_comment=VALUES(child_protection_comment),
                    referred_to=VALUES(referred_to),
                    scheduled_appointment_date=VALUES(scheduled_appointment_date),
                    voided=VALUES(voided);
    END $$
-- end of scheduled updates procedures

    SET sql_mode=@OLD_SQL_MODE $$
-- ----------------------------  scheduled updates ---------------------


DROP PROCEDURE IF EXISTS sp_gbv_scheduled_updates $$
CREATE PROCEDURE sp_gbv_scheduled_updates()
  BEGIN
    DECLARE update_script_id INT(11);
    DECLARE last_update_time DATETIME;
    SELECT max(start_time) into last_update_time from kenyaemr_etl.etl_script_status where stop_time is not null or stop_time !="";

    INSERT INTO kenyaemr_etl.etl_script_status(script_name, start_time) VALUES('gbv_scheduled_updates', NOW());
    SET update_script_id = LAST_INSERT_ID();

    CALL sp_update_etl_gbv_consenting(last_update_time);
    CALL sp_update_etl_gbv_legal_encounter(last_update_time);
    CALL sp_update_etl_gbv_pepmanagementforsurvivor(last_update_time);
    CALL sp_update_etl_gbv_pepmanagement_nonoccupationalexposure(last_update_time);
    CALL sp_update_etl_gbv_pepmanagement_occupationalexposure(last_update_time);
    CALL sp_update_etl_gbv_pepmanagement_followup(last_update_time);
    CALL sp_update_etl_gbv_perpetratorencounter(last_update_time);
    CALL sp_update_etl_gbv_counsellingencounter(last_update_time);
    CALL sp_update_etl_gbv_linkage(last_update_time);
    CALL sp_update_etl_gbv_physicalemotional_violence(last_update_time);
    -- CALL sp_update_etl_gbv_postrapecare(last_update_time);
    -- CALL sp_update_etl_gbv_psychologicalassessment(last_update_time);

    UPDATE kenyaemr_etl.etl_script_status SET stop_time=NOW() where id=update_script_id;
    SELECT update_script_id;

    END $$
-- DELIMITER ;










