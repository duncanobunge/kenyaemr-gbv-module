package org.openmrs.module.gbv.factory;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.module.gbv.domain.AppDescriptor;
import org.openmrs.module.gbv.domain.GbvAppTemplate;
import org.openmrs.module.gbv.domain.GbvExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class GbvAppConfigurationLoaderFactory implements GbvAppFrameworkFactory {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectMapper objectMapper = new ObjectMapper();

    private PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    public GbvAppConfigurationLoaderFactory() {
    	// Tell the parser to all // and /* style comments.
    	objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }
    
    @Override
    public List<GbvAppTemplate> getAppTemplates() throws IOException {
        Resource[] appDefinitionJsonResources = resourceResolver.getResources("classpath*:/apps/*AppTemplates.json");
        List<GbvAppTemplate> templates = new ArrayList<GbvAppTemplate>();
        for (Resource appDefinitionResource : appDefinitionJsonResources) {
            List<GbvAppTemplate> forResource;
            try {
                forResource = objectMapper.readValue(appDefinitionResource.getInputStream(), new TypeReference<List<GbvAppTemplate>>() {});
                templates.addAll(forResource);
            } catch (IOException e) {
                logger.error("Error reading AppTemplates configuration file: {}", appDefinitionResource.getDescription(), e);
            }
        }
        return templates;
    }

    @Override
    public List<AppDescriptor> getAppDescriptors() throws IOException {
        Resource[] appDefinitionJsonResources = resourceResolver.getResources("classpath*:/apps/*app.json");
        List<AppDescriptor> appDescriptors = new ArrayList<AppDescriptor>();
        for (Resource appDefinitionResource : appDefinitionJsonResources) {
            try {
                List<AppDescriptor> appDescriptorsForResource = getAppDescriptorsForResource(appDefinitionResource.getInputStream());
                appDescriptors.addAll(appDescriptorsForResource);
            } catch (IOException e) {
                logger.error("Error reading app configuration file: {}", appDefinitionResource.getDescription(), e);
            }
        }
        return appDescriptors;
    }

    public List<AppDescriptor> getAppDescriptorsForResource(InputStream inputStream) throws IOException {
        return objectMapper.readValue(inputStream, new TypeReference<List<AppDescriptor>>() {});
    }

    @Override
    public List<GbvExtension> getExtensions() throws IOException {
        Resource[] extensionDefinitionJsonResources = resourceResolver.getResources("classpath*:/apps/*extension.json");
        List<GbvExtension> extensions = new ArrayList<GbvExtension>();
        for (Resource extensionResource : extensionDefinitionJsonResources) {
            List<GbvExtension> extensionsForResource;
            try {
                extensionsForResource = objectMapper.readValue(extensionResource.getInputStream(), new TypeReference<List<GbvExtension>>() {});
                extensions.addAll(extensionsForResource);
            } catch (IOException e) {
                logger.error("Error reading extension configuration file: {}", extensionResource.getDescription(), e);
            }
        }
        return extensions;
    }
}
