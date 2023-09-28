package org.openmrs.module.gbv.reporting.builder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.gbv.reporting.calculation.RegistrationCountyAddressCalculation;
import org.openmrs.module.gbv.reporting.calculation.RegistrationSubCountyAddressCalculation;
import org.openmrs.module.gbv.reporting.calculation.converter.GeneralCalculationResultConverter;
import org.openmrs.module.gbv.reporting.cohort.definition.traumacounselling.TraumaCounsellingEncCohortDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.GbvTraumaCounPrcNumDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.GbvTraumaCounVisitNoDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.GbvTraumaCounsellingEncDateDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.TraumaCounEmergingDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.TraumaCounHIVTestResultDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.TraumaCounPlanofACtionDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.TraumaCounselingTCADateDataDefinition;
import org.openmrs.module.gbv.reporting.data.definition.traumacounselling.TraumaCounPresentingDataDefinition;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
@Builds({ "kenyaemr.gbv.traumacounselling.report" })
public class GbvTraumaCounsellingReportBuilder extends AbstractReportBuilder {
    public static final String DATE_FORMAT = "dd-MM-yyyy";
	
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		
		return Arrays.asList(ReportUtils.map(reportColumns(), "startDate=${startDate},endDate=${endDate}"));
	}
	
	@Override
	protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
		return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
		        Date.class), new Parameter("dateBasedReporting", "", String.class));
	}
	
	/**
	 * Columns for the report
	 * 
	 * @return
	 */
	protected DataSetDefinition reportColumns() {
		EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
		dsd.setName("gbvTraumaCounsellingEncounter");
		dsd.setDescription("GBV Trauma Counselling Services");
		dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		String paramMapping = "startDate=${startDate},endDate=${endDate}";
		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class,
		    CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("id", new PatientIdDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
		dsd.addColumn("County", new CalculationDataDefinition("County", new RegistrationCountyAddressCalculation()), "",
		    new GeneralCalculationResultConverter());
		dsd.addColumn("Sub-County", new CalculationDataDefinition("Sub-County",
		        new RegistrationSubCountyAddressCalculation()), "", new GeneralCalculationResultConverter());
		dsd.addColumn("Village/Estate of residence", new CalculationDataDefinition("Village/Estate of residence",
				new PersonAddressCalculation()), "", new GeneralCalculationResultConverter());
        dsd.addColumn("Encounter Date", new GbvTraumaCounsellingEncDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("VisitNumber", new GbvTraumaCounVisitNoDataDefinition(), "");
		dsd.addColumn("Prc Number", new GbvTraumaCounPrcNumDataDefinition(), "");
        dsd.addColumn("Presenting Issue", new TraumaCounPresentingDataDefinition(), "");
        dsd.addColumn("Emerging Issue", new TraumaCounEmergingDataDefinition(), "");
        dsd.addColumn("Action Plan", new TraumaCounPlanofACtionDataDefinition(), "");
        dsd.addColumn("HIV Test Result", new TraumaCounHIVTestResultDataDefinition(), "");
        dsd.addColumn("Next Appointment", new TraumaCounselingTCADateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        	// new columns 

        TraumaCounsellingEncCohortDefinition cd = new TraumaCounsellingEncCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));

		dsd.addRowFilter(cd, paramMapping);
		return dsd;

	}
    
}
