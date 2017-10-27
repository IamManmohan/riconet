package com.rivigo.riconet.core.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {
		"com.rivigo.zoom.delivery",
		"com.rivigo.zoom.test.config",
		"com.rivigo.zoom.common.service",
		"com.rivigo.zoom.common.audit",
		"com.rivigo.zoom.common.queue",
		"com.rivigo.zoom.common.utils",
		"com.rivigo.zoom.component.query.utils"
})
public class TestConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer configurer() throws IOException {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        String profile = "test";
        Resource[] resources = patternResolver.getResources(String.format("classpath*:%s/*.properties", profile));
		List<Resource> resourcesList = new ArrayList<>();
		resourcesList.addAll(Arrays.asList(resources));
		configurer.setLocations(resourcesList.toArray(new Resource[resourcesList.size()]));
		configurer.setOrder(0);
		configurer.setIgnoreUnresolvablePlaceholders(true);
		return configurer;
	}
}
