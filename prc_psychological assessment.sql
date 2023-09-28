--Populate etl gbv_postrapecare:
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_postrapecare $$
CREATE PROCEDURE sp_populate_etl_gbv_postrapecare()
BEGIN
SELECT "Processing etl_gbv_postrapecare data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_postrapecare(
            uuid,
            encounter_id,
            visit_id,
            patient_id ,
            location_id ,
            visit_date ,
            encounter_provider,
            date_created ,
            date_of_examination,
            date_of_incident,
            no_alleged_perpetrators,
            alleged_perpetrator ,
            specify_the_rship ,
            county,
            subcounty ,
            landmark,
            indicate_observation,
            indicate_reported,
            circumstantial_incident,
            type_of_sexual_violence,
            specify_type_of_sexual_violence,
            use_of_condom,
            attended_health_facility,
            facility_name,
            date_time,
            were_you_treated,
            given_referral_notes,
            reported_to_police,
            police_station,
            date_reported,
            medical_history,
            observation_remark,
            physical_examination,
            parity,
            contraception_type,
            lmp,
            known_pregnancy ,
            consensual_sex_date,
            blood_pressure,
            pulse_rate,
            RR ,
            temperature,
            demeanor,
            survivor_change_cloth,
            state_of_clothes,
            clothes_transported,
            other_clothes_details,
            clothes_handed_to_police,
            survivor_goto_toilet,
            survivor_bathe,
            yes_bathe_details,
            perpetrators_mark,
            yes_perpetrator_details,
            physical_injuries,
            outer_genitalia,
            vagina,
            hymen,
            anus,
            other_orifices,
            pep_1st_dose,
            ecp_given,
            stitching,
            yes_comment,
            sti_txt_given,
            yes_comment_sti_trx,
            medication_comment,
            referrals_to,
            other_referral_specify,
            lab_type,
            comment,
            samples_packed_issued,
            examining_officer,
            examining_officer_signature,
            date_signed_by_examining_officer,
            police_officer,
            police_officer_signature,
            date_police_officer_signed,
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
	max(if(o.concept_id=159948,o.value_datetime,null)) as date_of_examination,
	max(if(o.concept_id=162869,o.value_datetime,null)) as date_of_incident,
	max(if(o.concept_id=1639,o.value_numeric,null)) as no_alleged_perpetrators,
	max(if(o.concept_id=1119,case o.value_coded when 1066 then 'unknown' when 1065 then 'known' else ,null)) as alleged_perpetrator,
	max(if(o.concept_id=165092,o.value_text,null)) as specify_the_rship,
    max(if(o.concept_id=167131,o.value_text,null)) as county,
    max(if(o.concept_id=161564,o.value_text,null)) as subcounty,
    max(if(o.concept_id=159942,o.value_text,null)) as landmark,
    max(if(o.concept_id=160945,o.value_text,null)) as indicate_observation,
    max(if(o.concept_id=166846,o.value_text,null)) as indicate_reported,
    max(if(o.concept_id=160303,o.value_text,null)) as circumstantial_incident,
	max(if(o.concept_id=123160,case o.value_coded when 166060 then "oral" when 123385 then "vaginal" when 148895 then "Anal" when 5622 then "Other (specify)"
	else "" end,null)) as type_of_sexual_violence,
	max(if(o.concept_id=160979,o.value_text,null)) as specify_type_of_sexual_violence,
	max(if(o.concept_id=1357,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' when 1067 then 'Unknown' else ,null)) as use_of_condom,
	max(if(o.concept_id=166484,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as attended_health_facility,
	max(if(o.concept_id=162724,o.value_text,null)) as facility_name,
	max(if(o.concept_id=164093,o.value_datetime,null)) as date_time,
	max(if(o.concept_id=165052,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as were_you_treated,
	max(if(o.concept_id=165152,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as given_referral_notes,
	max(if(o.concept_id=165193,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as reported_to_police,
	max(if(o.concept_id=161550,o.value_text,null)) as police_station,
	max(if(o.concept_id=165144,o.value_datetime,null)) as date_reported,
    max(if(o.concept_id=160221,o.value_text,null)) as medical_history,
    max(if(o.concept_id=163677,o.value_text,null)) as observation_remark,
    max(if(o.concept_id=1391,o.value_text,null)) as physical_examination,
    max(if(o.concept_id=1053,o.value_text,null)) as  parity,
    max(if(o.concept_id=163400,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as contraception_type,
    max(if(o.concept_id=1427,o.value_datetime,null)) as lmp,
    max(if(o.concept_id=5272,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as known_pregnancy,
    max(if(o.concept_id=163724,o.value_datetime,null)) as consensual_sex_date,
    max(if(o.concept_id=163375,o.value_numeric,null)) as blood_pressure,
    max(if(o.concept_id=163381,o.value_text,null)) as  pulse_rate,
    max(if(o.concept_id=5242,o.value_numeric,null)) as   RR,
    max(if(o.concept_id=5088,o.value_numeric,null)) as temperature,
    max(if(o.concept_id=162056,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as demeanor,
    max(if(o.concept_id=162676,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as survivor_change_cloth,
    max(if(o.concept_id=161031,o.value_text,null)) as  state_of_clothes,
    max(if(o.concept_id=161239,case o.value_coded when 159310 then 'plastic bag' when 161240 then 'non plastic bag' when 5622 then 'other' else ,null)) as clothes_transported,
    max(if(o.concept_id=166363,o.value_text,null)) as  other_clothes_details,
    max(if(o.concept_id=165180,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as clothes_handed_to_police,
    max(if(o.concept_id=160258,case o.value_coded when 1065 then 'long_call' when 1066 then 'short_call' else ,null)) as survivor_goto_toilet,
    max(if(o.concept_id=162997,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as survivor_bathe,
    max(if(o.concept_id=163048,o.value_text,null)) as yes_bathe_details,
    max(if(o.concept_id=165241,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as perpetrators_mark,
    max(if(o.concept_id=161031,o.value_text,null)) as yes_perpetrator_details,
    max(if(o.concept_id=165035,o.value_text,null)) as physical_injuries,
    max(if(o.concept_id=160971,o.value_text,null)) as outer_genitalia,
    max(if(o.concept_id=160969,o.value_text,null)) as vagina,
    max(if(o.concept_id=160981,o.value_text,null)) as hymen,
    max(if(o.concept_id=160962,o.value_text,null)) as anus,
    max(if(o.concept_id=160943,o.value_text,null)) as other_orifices,
    max(if(o.concept_id=165060,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as pep_1st_dose,
    max(if(o.concept_id=374,case o.value_coded when 1066 then 'No' when 1065 then 'Yes' else ,null)) as ecp_given,
    max(if(o.concept_id=1670,case o.value_coded when 1066 then 'No' when 1065 then 'Yes(comment)' else ,null)) as stitching,
    max(if(o.concept_id=165440,o.value_text,null)) as yes_comment,
    max(if(o.concept_id=165200,case o.value_coded when 1066 then 'No' when 1065 then 'Yes(comment)' else ,null)) as sti_txt_given,
    max(if(o.concept_id=167214,o.value_text,null)) as yes_comment_sti_trx,
    max(if(o.concept_id=160632,o.value_text,null)) as medication_comment,
    max(if(o.concept_id=1272,case o.value_coded when 165192 then 'police_station' when 1370 then 'hiv test' when 164422 then 'lab'
    when 135914 then 'legal' when 5460 then 'trauma counselling' when 167254 then 'safe shelter'
    when 160542 then 'opd'  when 5622 then 'other (specify)' else ,null)) as referrals_to,
    max(if(o.concept_id=165138,o.value_text,null)) as other_referral_specify,
    max(if(o.concept_id=164217,case o.value_coded when 160480 then 'National_government lab' when 1537 then 'Health facility lab' else ,null)) as lab_type,
    max(if(o.concept_id=161011,o.value_text,null)) as comment,
    max(if(o.concept_id=165435,o.value_text,null)) as samples_packed_issued,
    max(if(o.concept_id=165225,o.value_text,null)) as examining_officer,
    max(if(o.concept_id=165143,o.value_text,null)) as examining_officer_signature,
    max(if(o.concept_id=1711,date(o.value_datetime),null)) as date_signed_by_examining_officer,
    max(if(o.concept_id=165142,o.value_text,null)) as police_officer,
    max(if(o.concept_id=166847,o.value_text,null)) as police_officer_signature,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as date_police_officer_signed,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid ="c46aa4fd-8a5a-4675-90a7-a6f2119f61d8"
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (166847,165143,160753,159948,162869,1639,165229,165092,167131,161564,159942,
	160945,166846,160303,123160,160979,1357,166484,162724,164093,165052,165152,165193,161550,165144,160221,163677,1391,1053,163400,1427,5272,163724,
	163375,163381,5242,5088,162056,162676,161031,161239,166363,165180,160258,162997,163048,165241,161031,165035,160971,160969,160981,160962,160943,
	165060,374,1670,165440,165200,167214,160632,1272,165138,164217,161011,165435,165225,165142,1711)
where e.voided=0
group by e.patient_id, e.encounter_id;


SELECT "Completed processing gbv_postrapecare data ", CONCAT("Time: ", NOW());
END $$

--Populate etl gbv_psychologicalassessment:
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_psychologicalassessment $$
CREATE PROCEDURE sp_populate_etl_gbv_psychologicalassessment()
BEGIN
SELECT "Processing etl_gbv_psychologicalassessment data", CONCAT("Time: ", NOW());
insert into kenyaemr_etl.etl_gbv_psychologicalassessment(  
            uuid,
            encounter_id,
            visit_id,
            patient_id ,
            location_id ,
            visit_date ,
            encounter_provider,
            date_created ,
            general_appearance,
            rapport,
            mood,
            affect,
            speech,
            perception,
            thought_content,
            thought_process,
            art_therapy,
            child_experience ,
            memory,
            orientation,
            concentration,
            intelligence,
            judgement,
            insight_level,
            recommendation,
            referral_point,
            examining_officer,
            examining_officer_signature,
            date_signed_by_examining_officer,
            police_officer,
            police_officer_signature,
            date_police_officer_signed,
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
	max(if(o.concept_id=1119,case o.value_coded when 460 then 'appear older or younger' when 5201 then 'gait'
	when 5243 then 'dressing' when 136443 then 'grooming (unkempt)' when 159438 then 'grooming(neat)' else ,null)) as general_appearance,
	max(if(o.concept_id=122943,case o.value_coded when 160281 then "easy to establish" when 160282 then "Initially difficult but easier over time"
	when 141762 then "Difficult to establish" else "" end,null)) as rapport,
	max(if(o.concept_id=165159,case o.value_coded when 166050 then "elevated" when 119539 then "sad" when 140487 then "happy" when 163468 then "hopeless"
    when 141611 then "euphoric" when 165536 then "depressed" when 118296 then "irritable" when 140468 then 'anxious'
    when 122996 then "angry"  when 118879 then "easily upset" else "" end,null)) as mood,

    max(if(o.concept_id=167099,case o.value_coded when 136204 then "labile" when 153557 then "blunt/flat" when 162980 then "appropriate to content"
    when 137668 then "Inappropriate to content" else "" end,null)) as affect,

    max(if(o.concept_id=167201,case o.value_coded when 145046 then "Speak rapidly & frenziedly" when 159539 then "Speak clearly"
    when 126351 then "Mumbling" when 137646 then "Monosyllables" when 112842 then "Hesitant" else "" end,null)) as speech,

    max(if(o.concept_id=159707,case o.value_coded when 139146 then "Hallucination" when 126456 then "Feeling of unreality"
    when 150881 then "Hesitant"  else "" end,null)) as perception,

    max(if(o.concept_id=167112,case o.value_coded when 160847 then "Ideas but no plan or intent" when 160848 then "clear plan but no intent"
    when 160849 then "Unclear plan but no intent" when 160844 then "Ideas coupled with clear plan and intent to carry it out" else "" end, null)) as thought_content,

    max(if(o.concept_id=167106,case o.value_coded when 167108 then "Goal-directed/Logical ideas" when 1097 then "Loosened association/flight ideas"
    when 1099 then "Relevant" when 1090 then "Circumstantial" when 1100 then "Ability to abstract" when 1098 then "Perseveration"
    when 167020 then "art_therapy" when 165427 then "child_experience" else "" end, null)) as thought_process,
    max(if(o.concept_id=167020,o.value_text,null)) as art_therapy,
    max(if(o.concept_id=165427,o.value_text,null)) as child_experience,
    max(if(o.concept_id=165295,case o.value_coded when 129507 then "Recent memory" when 121657 then "Long-term memory"
    when 134145 then "Short-term memory"  else "" end,null)) as memory,

    max(if(o.concept_id=167084,case o.value_coded when 167083 then "verily oriented" when 167081 then "oriented"
    when 161427 then "poorly oriented"  else "" end,null)) as orientation,

    max(if(o.concept_id=161427,case o.value_coded when 161565 then "above average" when 114412 then "average"
    when 167082 then "Below average"  else "" end,null)) as concentration,

    max(if(o.concept_id=165241,case o.value_coded when 161565 then "above average" when 114412 then "average"
    when 167082 then "Below average"  else "" end,null)) as intelligence,

    max(if(o.concept_id=167116,case o.value_coded when 159407 then "poor" when 164077 then "very good"
    when 159406 then "fair"  else "" end,null)) as judgement,

    max(if(o.concept_id=167115,case o.value_coded when 159405 then "present" when 159406 then "fair"
    when 1107 then "not present"  else "" end,null)) as insight_level,
    max(if(o.concept_id=164359,o.value_text,null)) as recommendation,
    max(if(o.concept_id=166485,o.value_text,null)) as referral_point,
	max(if(o.concept_id=165225,o.value_text,null)) as examining_officer,
    max(if(o.concept_id=165143,o.value_text,null)) as examining_officer_signature,
    max(if(o.concept_id=1711,date(o.value_datetime),null)) as date_signed_by_examining_officer,
    max(if(o.concept_id=165142,o.value_text,null)) as police_officer,
    max(if(o.concept_id=166847,o.value_text,null)) as police_officer_signature,
    max(if(o.concept_id=160753,date(o.value_datetime),null)) as date_police_officer_signed,
    e.voided
from encounter e
	inner join person p on p.person_id=e.patient_id and p.voided=0
	inner join form f on f.form_id=e.form_id and f.uuid ="9d21275a-7657-433a-b305-a736423cc496"
	left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (165142,165225,166485,164359,167115,167116,165241,161427,
	167084,165295,167020,165427,167106,167112,159707,167201,167099,165159,122943,1119,1711,165143,166847,160753)
where e.voided=0
group by e.patient_id, e.encounter_id;


SELECT "Completed processing gbv_psychologicalassessment data ", CONCAT("Time: ", NOW());
END $$

