package org.openmrs.module.gbv.calculation.library.gbv;
        import java.util.Collection;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.Set;
        import org.apache.commons.logging.Log;
        import org.apache.commons.logging.LogFactory;
        import org.openmrs.Program;
        import org.openmrs.calculation.patient.PatientCalculationContext;
        import org.openmrs.calculation.result.CalculationResultMap;
        import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
        import org.openmrs.module.kenyacore.calculation.BooleanResult;
        import org.openmrs.module.kenyacore.calculation.Filters;
        import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
        import org.openmrs.module.metadatadeploy.MetadataUtils;

        public class OnGBVProgramCalculation  extends AbstractPatientCalculation implements PatientFlagCalculation {
        protected static final Log log = LogFactory.getLog(OnGBVProgramCalculation.class);

        public OnGBVProgramCalculation() {
        }

        public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program gbvProgram = (Program)MetadataUtils.existing(Program.class, "e41c3d74-37c7-4001-9f19-ef9e35224b70");
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inGbvProgram = Filters.inProgram(gbvProgram, alive, context);
        CalculationResultMap ret = new CalculationResultMap();

        Integer ptId;
        boolean onGbv;
        for(Iterator var9 = cohort.iterator(); var9.hasNext(); ret.put(ptId, new BooleanResult(onGbv, this))) {
        ptId = (Integer)var9.next();
        onGbv = false;
        if (inGbvProgram.contains(ptId)) {
        onGbv = true;
        }
        }

        return ret;
        }

        public String getFlagMessage() {
        return "On GBV";
        }
        }
