
   ------------- create table kenyaemr_etl.etl_gbv_psychologicalassessment_form-----------------------
  CREATE TABLE kenyaemr_etl.etl_gbv_psychologicalassessment(
              uuid CHAR(38),
              encounter_id INT(11) NOT NULL PRIMARY KEY,
              visit_id INT(11) DEFAULT NULL,
              patient_id INT(11) NOT NULL ,
              location_id INT(11) DEFAULT NULL,
              visit_date DATE,
              encounter_provider INT(11),
              general_appearance VARCHAR(50),
              rapport VARCHAR(50),
              mood VARCHAR(50),
              affect VARCHAR(50),
              speech VARCHAR(50),
              perception VARCHAR(50),
              thought_content VARCHAR(50),
              thought_process VARCHAR(50),
              art_therapy VARCHAR(100),
              child_experience VARCHAR(100),
              memory VARCHAR(50),
              orientation VARCHAR(50),
              concentration VARCHAR(50),
              intelligence VARCHAR(50),
              judgement VARCHAR(50),
              insight_level VARCHAR(50),
              recommendation VARCHAR(255),
              referral_point VARCHAR(100),
              examining_officer VARCHAR(100),
              examining_officer_signature VARCHAR(100),
              date_signed_by_examining_officer DATE,
              police_officer VARCHAR(100),
              police_officer_signature VARCHAR(100),
              date_police_officer_signed DATE,
              voided INT(11),
              CONSTRAINT FOREIGN KEY (patient_id) REFERENCES kenyaemr_etl.etl_patient_demographics(patient_id),
              CONSTRAINT unique_uuid UNIQUE(uuid),
              INDEX(visit_date),
              INDEX(visit_id),
              INDEX(encounter_id),
              INDEX(patient_id),
              INDEX(patient_id, visit_date)
      );
    SELECT "Successfully created kenyaemr_etl.etl_gbv_psychologicalassessment_form";
    ---------- create table kenyaemr_etl.etl_gbv_postrapecare_form-----------------------
      CREATE TABLE kenyaemr_etl.etl_gbv_postrapecare(
                  uuid CHAR(38),
                  encounter_id INT(11) NOT NULL PRIMARY KEY,
                  visit_id INT(11) DEFAULT NULL,
                  patient_id INT(11) NOT NULL ,
                  location_id INT(11) DEFAULT NULL,
                  visit_date DATE,
                  encounter_provider INT(11),
                  date_of_examination DATE,
                  date_of_incident DATE,
                  alleged_perpetrator VARCHAR(50),
                  no_alleged_perpetrators INT(11),
                  specify_the_rship VARCHAR(100),
                  county VARCHAR(100),
                  subcounty VARCHAR(100),
                  landmark VARCHAR(100),
                  indicate_observation VARCHAR(100),
                  indicate_reported VARCHAR(100),
                  circumstantial_incident VARCHAR(100),
                  type_of_sexual_violence VARCHAR(50),
                  specify_type_of_sexual_violence VARCHAR(100),
                  use_of_condom VARCHAR(50),
                  attended_health_facility VARCHAR(50),
                  facility_name VARCHAR(100),
                  date_time DATE,
                  were_you_treated VARCHAR(50),
                  given_referral_notes VARCHAR(50),
                  reported_to_police VARCHAR(50),
                  police_station VARCHAR(100),
                  date_reported DATE,
                  medical_history VARCHAR(255),
                  observation_remark VARCHAR(255),
                  physical_examination VARCHAR(255),
                  parity VARCHAR(80),
                  contraception_type VARCHAR(50),
                  lmp DATE,
                  known_pregnancy VARCHAR(50),
                  consensual_sex_date DATE,
                  blood_pressure INT(11),
                  pulse_rate VARCHAR(50),
                  RR INT(11),
                  temperature INT(11),
                  demeanor VARCHAR(50),
                  survivor_change_cloth VARCHAR(50),
                  state_of_clothes VARCHAR(100),
                  clothes_transported VARCHAR(50),
                  other_clothes_details VARCHAR(255),
                  clothes_handed_to_police VARCHAR(50),
                  survivor_goto_toilet VARCHAR(50),
                  survivor_bathe VARCHAR(50),
                  yes_bathe_details VARCHAR(100),
                  perpetrators_mark VARCHAR(50),
                  yes_perpetrator_details VARCHAR(100),
                  physical_injuries VARCHAR(100),
                  outer_genitalia VARCHAR(100),
                  vagina VARCHAR(100),
                  hymen VARCHAR(100),
                  anus VARCHAR(100),
                  other_orifices VARCHAR(100),
                  pep_1st_dose VARCHAR(50),
                  ecp_given VARCHAR(50),
                  stitching VARCHAR(50),
                  yes_comment VARCHAR(100),
                  sti_txt_given VARCHAR(50),
                  yes_comment_sti_trx VARCHAR(100),
                  medication_comment VARCHAR(100),
                  referrals_to VARCHAR(50),
                  other_referral_specify VARCHAR(100),
                  lab_type VARCHAR(50),
                  comment VARCHAR(255),
                  samples_packed_issued VARCHAR(50),
                  examining_officer VARCHAR(100),
                  examining_officer_signature VARCHAR(100),
                  date_signed_by_examining_officer DATE,
                  police_officer VARCHAR(100),
                  police_officer_signature VARCHAR(100),
                  date_police_officer_signed DATE,
                  voided INT(11),
                  CONSTRAINT FOREIGN KEY (patient_id) REFERENCES kenyaemr_etl.etl_patient_demographics(patient_id),
                  CONSTRAINT unique_uuid UNIQUE(uuid),
                  INDEX(visit_date),
                  INDEX(visit_id),
                  INDEX(encounter_id),
                  INDEX(patient_id),
                  INDEX(patient_id, visit_date)
          );
  SELECT "Successfully created kenyaemr_etl.etl_gbv_postrapecare_form";
