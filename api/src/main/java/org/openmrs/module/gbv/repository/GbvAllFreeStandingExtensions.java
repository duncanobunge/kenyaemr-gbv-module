package org.openmrs.module.gbv.repository;

import org.openmrs.module.gbv.domain.GbvExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class represent all <em>free-standing</em> extensions, though it does not include extensions that are included in
 * apps found in {@link GbvAllAppDescriptors}
 */
@Repository
public class GbvAllFreeStandingExtensions {
	
	private List<GbvExtension> extensions = new ArrayList<GbvExtension>();
	
	private Validator validator;
	
	@Autowired
	public GbvAllFreeStandingExtensions(Validator validator) {
		this.validator = validator;
	}
	
	public void add(List<GbvExtension> extensions) {
		for (GbvExtension extension : extensions) {
			add(extension);
		}
	}
	
	public void add(GbvExtension extension) {
		validate(extension);
		
		synchronized (this.extensions) {
			this.extensions.add(extension);
			Collections.sort(this.extensions);
		}
	}
	
	private void validate(GbvExtension extension) {
		Set<ConstraintViolation<GbvExtension>> constraintViolations = validator.validate(extension);
		if (!constraintViolations.isEmpty())
			throw new IllegalArgumentException(constraintViolations.iterator().next().getMessage());
		
		if (this.extensions.contains(extension))
			throw new IllegalArgumentException("Extension already exists: " + extension.getId());
	}
	
	public List<GbvExtension> getExtensions() {
		List<GbvExtension> extensionList = new ArrayList<GbvExtension>();
		extensionList.addAll(this.extensions);
		return extensionList;
	}
	
	public void clear() {
		extensions.clear();
	}
}
