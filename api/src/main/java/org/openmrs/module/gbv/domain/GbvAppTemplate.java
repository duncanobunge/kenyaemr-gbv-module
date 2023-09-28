package org.openmrs.module.gbv.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

/**
 * Descriptor that can be customized and instantiated to create an app.
 * For example a generic registration module might provide a template for a registration app, and expect
 * implementations to instantiate one app per clinical service, each with slightly different configurations.
 */
public class GbvAppTemplate {

    @NotEmpty
    @JsonProperty
    private String id;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<GbvExtensionPoint> extensionPoints;

    @JsonProperty
    private List<String> contextModel;

    @Valid
    @JsonProperty
    private List<GbvAppTemplateConfigurationOption> configOptions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GbvExtensionPoint> getExtensionPoints() {
        return extensionPoints;
    }

    public void setExtensionPoints(List<GbvExtensionPoint> extensionPoints) {
        this.extensionPoints = extensionPoints;
    }

    public List<String> getContextModel() {
        return contextModel;
    }

    public void setContextModel(List<String> contextModel) {
        this.contextModel = contextModel;
    }

    public List<GbvAppTemplateConfigurationOption> getConfigOptions() {
        return configOptions;
    }

    public void setConfigOptions(List<GbvAppTemplateConfigurationOption> configOptions) {
        this.configOptions = configOptions;
    }
}
