package org.openmrs.module.gbv.domain;

import org.openmrs.module.gbv.context.GbvAppContextModel;

/**
 * An interface for objects which can be required or excluded by evaluating a Javascript expression using {@link org.openmrs.module.gbv.service.GbvAppFrameworkService#checkRequireExpression(Requireable, GbvAppContextModel)}
 */
public interface GbvRequireable {

	String getId();

	String getRequire();
}
