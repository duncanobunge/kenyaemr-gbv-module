package org.openmrs.module.gbv.domain.validators;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.openmrs.module.gbv.domain.AppDescriptor;
import org.openmrs.module.gbv.domain.GbvExtensionPoint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GbvDuplicateExtensionPointValidator implements ConstraintValidator<GbvNoDuplicateExtensionPoint, AppDescriptor> {
    @Override
    public void initialize(GbvNoDuplicateExtensionPoint constraintAnnotation) {
    }

    @Override
    public boolean isValid(AppDescriptor value, ConstraintValidatorContext context) {
        if (null == value || null == value.getExtensionPoints()) return true;

        for(GbvExtensionPoint extensionPointToMatch : value.getExtensionPoints()) {
            for (GbvExtensionPoint extensionPoint : value.getExtensionPoints()) {
                if (extensionPointToMatch != extensionPoint &&
                    extensionPointToMatch.getId().equalsIgnoreCase(extensionPoint.getId()))
                    return false;
            }
        }

        return true;
    }
}
