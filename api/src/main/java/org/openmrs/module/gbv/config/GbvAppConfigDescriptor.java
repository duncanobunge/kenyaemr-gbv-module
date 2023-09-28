package org.openmrs.module.gbv.config;

import org.codehaus.jackson.annotate.JsonProperty;

public class GbvAppConfigDescriptor {

    @JsonProperty
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean isEnabled() {
        return enabled;
    }
}
