/**
 * Copyright � 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.service.device.discovery;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Locale;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.dell.isg.smi.service.device.discovery.config.InnerConfig;
import com.dell.isg.smi.service.device.discovery.manager.threads.RequestScopeDiscoveryCredential;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2
@EnableDiscoveryClient
@EnableAsync
@ComponentScan("com.dell")
@Import(InnerConfig.class)
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new DiscoveryCustomScopeRegisteringBeanFactoryPostProcessor();
    }


    @Bean
    @Scope(value = "discoveryThread")
    public RequestScopeDiscoveryCredential requestScopeDiscoveryCredential() {
        return new RequestScopeDiscoveryCredential();
    }


    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }


    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }


    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("deviceDiscovery").apiInfo(apiInfo()).select().paths(regex("/api.*")).build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("SMI Micro-service : Device discovery ").description("Micro services for device discovery .").termsOfServiceUrl("http://www.dell.com/smi/device/discovery").license("Dell SMI License Version 1.0").licenseUrl("www.dell.com/smi").version("1.0 dev").build();
    }

}