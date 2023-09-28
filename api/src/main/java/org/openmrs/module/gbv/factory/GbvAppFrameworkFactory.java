package org.openmrs.module.gbv.factory;

import org.openmrs.module.gbv.domain.AppDescriptor;
import org.openmrs.module.gbv.domain.GbvAppTemplate;
import org.openmrs.module.gbv.domain.GbvExtension;

import java.io.IOException;
import java.util.List;

public interface GbvAppFrameworkFactory {

    List<AppDescriptor> getAppDescriptors() throws IOException;

    List<GbvExtension> getExtensions() throws IOException;

    List<GbvAppTemplate> getAppTemplates() throws IOException;

}
