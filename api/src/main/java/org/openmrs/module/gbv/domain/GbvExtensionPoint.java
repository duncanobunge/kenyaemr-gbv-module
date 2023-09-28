package org.openmrs.module.gbv.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.openmrs.module.gbv.domain.validators.GbvValidationErrorMessages;

import java.util.List;

public class GbvExtensionPoint {

    @NotEmpty(message = GbvValidationErrorMessages.EXTENSION_POINT_ID_NOT_EMPTY_MESSAGE)
    @JsonProperty
    protected String id;

    @JsonProperty
    protected String description;

    @JsonProperty
    protected List<String> supportedExtensionTypes;


    public GbvExtensionPoint() {
    }

    public GbvExtensionPoint(String id) {
        this.id = id;
    }

    public GbvExtensionPoint(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSupportedExtensionTypes() {
        return supportedExtensionTypes;
    }

    public void setSupportedExtensionTypes(List<String> supportedExtensionTypes) {
        this.supportedExtensionTypes = supportedExtensionTypes;
    }
}
