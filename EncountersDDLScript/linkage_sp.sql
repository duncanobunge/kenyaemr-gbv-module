-- Populate etl gbv_linkage--
DROP PROCEDURE IF EXISTS sp_populate_etl_gbv_linkage 
DELIMITER $$
CREATE PROCEDURE sp_populate_etl_gbv_linkage()
BEGIN
SELECT "Processing gbv_linkage data", CONCAT("Time: ", NOW());
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
where e.voided=0
group by e.patient_id, e.encounter_id;


SELECT "Completed processing gbv_linkage data ", CONCAT("Time: ", NOW());
END $$
delimiter ;