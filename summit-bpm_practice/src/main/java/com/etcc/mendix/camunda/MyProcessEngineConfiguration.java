/*
 * package com.etcc.mendix.camunda;
 * 
 * import org.camunda.bpm.engine.ProcessEngine; import
 * org.camunda.bpm.engine.impl.identity.Authentication; import
 * org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration; import
 * org.camunda.bpm.spring.boot.starter.util.SpringBootProcessEnginePlugin;
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.context.annotation.Configuration;
 * 
 * @Configuration public class MyProcessEngineConfiguration extends
 * SpringBootProcessEnginePlugin {
 * 
 * 
 * @Autowired private ProcessEngine processEngine;
 * 
 * @Override public void preInit(SpringProcessEngineConfiguration
 * processEngineConfiguration) { super.preInit(processEngineConfiguration); if
 * (!processEngineConfiguration.isAuthorizationEnabled()) {
 * processEngineConfiguration.setAuthorizationEnabled(true); } }
 * 
 * 
 * 
 * }
 */