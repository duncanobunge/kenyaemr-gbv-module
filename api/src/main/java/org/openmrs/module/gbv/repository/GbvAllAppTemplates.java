package org.openmrs.module.gbv.repository;

import org.openmrs.module.gbv.domain.GbvAppTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Repository
public class GbvAllAppTemplates {

    private List<GbvAppTemplate> appTemplates = new ArrayList<GbvAppTemplate>();

    @Autowired
    private Validator validator;

    public GbvAllAppTemplates() {
    }

    public GbvAllAppTemplates(Validator validator) {
        this.validator = validator;
    }

    public void add(Collection<GbvAppTemplate> templates) {
        for (GbvAppTemplate template : templates) {
            add(template);
        }
    }

    public void add(GbvAppTemplate template) {
        validate(template);
        appTemplates.add(template);
    }

    public List<GbvAppTemplate> getAppTemplates() {
        return Collections.unmodifiableList(appTemplates);
    }

    public void clear() {
        appTemplates.clear();
    }

    private void validate(GbvAppTemplate appTemplate) {
        Set<ConstraintViolation<GbvAppTemplate>> constraintViolations = validator.validate(appTemplate);
        if (!constraintViolations.isEmpty()) {
            throw new IllegalArgumentException(constraintViolations.iterator().next().getMessage());
        }

        if (this.appTemplates.contains(appTemplate)) {
            throw new IllegalArgumentException("AppTemplate already exists: " + appTemplate.getId());
        }
    }

    /**
     * Gets a template by its id
     * @param id
     * @return
     */
    public GbvAppTemplate getAppTemplate(String id) {
        for (GbvAppTemplate candidate : appTemplates) {
            if (candidate.getId().equals(id)) {
                return candidate;
            }
        }
        return null;
    }

}
