package org.openmrs.module.gbv.domain;

import org.openmrs.BaseOpenmrsObject;

public class GbvComponentState extends BaseOpenmrsObject {

    private Integer componentStateId;

    private String componentId;

    private GbvComponentType componentType;

    private Boolean enabled;

    public GbvComponentState() { }

    public GbvComponentState(String componentId, GbvComponentType componentType, boolean enabled) {
        this.componentId = componentId;
        this.componentType = componentType;
        this.enabled = enabled;
    }

    public Integer getComponentStateId() {
        return componentStateId;
    }

    public void setComponentStateId(Integer componentStateId) {
        this.componentStateId = componentStateId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public GbvComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(GbvComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public Integer getId() {
        return componentStateId;
    }

    @Override
    public void setId(Integer id) {
        setComponentStateId(id);
    }
}
