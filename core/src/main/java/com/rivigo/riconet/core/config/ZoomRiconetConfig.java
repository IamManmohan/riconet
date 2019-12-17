package com.rivigo.riconet.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.rivigo.zoom.common.interceptor.XUserAgentInterceptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
// @ComponentScan({
//  "com.rivigo.zoom.common.utils"
// })
public class ZoomRiconetConfig {

  @Value("${service.name}")
  private String serviceName;

  @Bean
  public static PropertySourcesPlaceholderConfigurer configurer() throws IOException {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    String profile = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
    profile = null == profile ? "local" : profile;
    Resource[] resources =
        patternResolver.getResources(String.format("classpath*:%s/*.properties", profile));
    List<Resource> resourcesList = new ArrayList<>();
    List<Resource> resourcesList2 = new ArrayList<>();
    for (Resource resource : resources) {
      if (resource.getURL().toString().contains("zoom-commons")) {
        resourcesList2.add(resource);
      } else {
        resourcesList.add(resource);
      }
    }
    //    removing zoom-commons props for a while
    //    resourcesList.addAll(resourcesList2);
    System.out.println("ACTIVE SPRING PROFILE IS " + profile);

    if (!profile.equals("local")) {
      Resource[] resources2 = patternResolver.getResources("file:/etc/zoom/*.properties");
      resourcesList.addAll(Arrays.asList(resources2));
    }
    configurer.setLocations(resourcesList.toArray(new Resource[resourcesList.size()]));
    configurer.setOrder(0);
    configurer.setIgnoreUnresolvablePlaceholders(true);
    return configurer;
  }

  @Bean
  public Mapper getDoserMapper() {
    return new DozerBeanMapper();
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }

  @Bean
  @Primary
  RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(Collections.singletonList(new XUserAgentInterceptor(serviceName)));
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
        new MappingJackson2HttpMessageConverter();
    mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper());
    restTemplate.setMessageConverters(
        Collections.singletonList(mappingJackson2HttpMessageConverter));
    return restTemplate;
  }

  @Bean
  public MultipartResolver multipartResolver() {
    return new CommonsMultipartResolver();
  }
}
