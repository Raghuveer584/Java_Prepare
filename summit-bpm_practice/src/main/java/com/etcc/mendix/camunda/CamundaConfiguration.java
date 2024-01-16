package com.etcc.mendix.camunda;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.dbcp.BasicDataSource;
import org.camunda.bpm.camel.component.CamundaBpmComponent;
import org.camunda.bpm.camel.spring.CamelServiceImpl;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.rest.CustomJacksonDateFormatListener;
import org.camunda.bpm.engine.rest.security.auth.ProcessEngineAuthenticationFilter;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.camunda.connect.plugin.impl.ConnectProcessEnginePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

//import com.etcc.zero.security.Cryptographer;

import oracle.jms.AQjmsFactory;

@Configuration
public class CamundaConfiguration {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	static final public String ORACLE_QUEUE_HOST = "oracle.queue.host";
	static final public String ORACLE_QUEUE_USER = "oracle.queue.user";
	static final public String ORACLE_QUEUE_PASSWORD = "oracle.queue.password";
	static final public String ORACLE_QUEUE_PORT = "oracle.queue.port";
	static final public String ORACLE_QUEUE_SERVICE = "oracle.queue.service";

	@Autowired
	private Environment env;

	@Bean(name = "reactorPlugin")
	@Order(Ordering.DEFAULT_ORDER + 1)
	public ProcessEnginePlugin reactorPlugin() {
		return CamundaReactor.plugin();
	}

	@Bean(name = "connectPlugin")
	@Order(Ordering.DEFAULT_ORDER + 1)
	public ProcessEnginePlugin connectProcessPlugin() {
		ConnectProcessEnginePlugin plugin = new ConnectProcessEnginePlugin();
		return plugin;
	}

	@Bean
	@Primary
	//@Profile("test")
	public DataSource getDataSource() throws Exception {
		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
		datasource.setUrl(env.getProperty("spring.datasource.url"));
		datasource.setUsername(env.getProperty("spring.datasource.username"));
		datasource.setPassword(env.getProperty("spring.datasource.password"));
		datasource.setMaxActive(Integer.valueOf(env.getProperty("spring.datasource.max.active", "100")));
		datasource.setMaxIdle(Integer.valueOf(env.getProperty("spring.datasource.max.idle", "5")));
		datasource.setMinIdle(Integer.valueOf(env.getProperty("spring.datasource.min.idle", "1")));
		datasource.setInitialSize(Integer.valueOf(env.getProperty("spring.datasource.initial.size", "1")));
		return datasource;

	}

	@Bean("riteDs")
	//@Profile("test")
	public DataSource getRiteDataSource() throws Exception {
		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName(env.getProperty("tagowner.datasource.driverClassName"));
		datasource.setUrl(env.getProperty("tagowner.datasource.url"));
		datasource.setUsername(env.getProperty("tagowner.datasource.username"));
		datasource.setPassword(env.getProperty("tagowner.datasource.password"));
		datasource.setMaxActive(Integer.valueOf(env.getProperty("tagowner.datasource.max.active", "100")));
		datasource.setMaxIdle(Integer.valueOf(env.getProperty("tagowner.datasource.max.idle", "5")));
		datasource.setMinIdle(Integer.valueOf(env.getProperty("tagowner.datasource.min.idle", "1")));
		datasource.setInitialSize(Integer.valueOf(env.getProperty("tagowner.datasource.initial.size", "1")));
		return datasource;

	}

	@Autowired
	private CamelContext camelContext;

//	@Bean(name = "connectionFactoryOracleAQQueue")
	// @ConditionalOnProperty(name="enableOracleQueueConnection",
	// havingValue="true")
	public QueueConnectionFactory defaultXaPooledConnectionFactory() throws Exception {
		String host = env.getProperty(ORACLE_QUEUE_HOST);

		String port = env.getProperty(ORACLE_QUEUE_PORT, "1521");
		String service = env.getProperty(ORACLE_QUEUE_SERVICE);

		try {
			String DB_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST="+host+")(PORT="+port+"))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME="+service+")))";

			log.info("Creating bean " + "connectionFactoryOracleAQQueue." + "with connection info, host: " + host
					+ ". Port: " + port + ". Service: " + service);

			return AQjmsFactory.getQueueConnectionFactory(DB_URL, null);
		} catch (JMSException e) {
			throw new Exception(
					"failed to configure oracle queue connection factory with given parameter values. Host: " + host
							+ " port: " + port + " service: " + service,
					e);
		}
	}

	@Bean(name = "oracleQueueCredentials")
	// @ConditionalOnProperty(name="enableOracleQueueConnection",
	// havingValue="true")
	public UserCredentialsConnectionFactoryAdapter oracleQueueCredentials()
			throws Exception {

		String user = env.getProperty(ORACLE_QUEUE_USER);
		String service = env.getProperty(ORACLE_QUEUE_SERVICE);
		String password = env.getProperty(ORACLE_QUEUE_PASSWORD);

		if (password == null) {
			password = user + service;
		} else {
//			try {
//				password = Cryptographer.decrypt(password);
//			} catch (Exception e) {
//				log.error("Error decypting password for oracle queue connection", e);
//				throw e; 
//			}
		}

		log.info("Creating bean " + "oracleQueueCredentials." + "with connection info, user: " + user + ". Service: "
				+ service);

		UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
		ConnectionFactory connectionFactoryOracleAQQueue = defaultXaPooledConnectionFactory();
		adapter.setTargetConnectionFactory(connectionFactoryOracleAQQueue );

		adapter.setUsername(user);
		adapter.setPassword(password);
		return adapter;
	}

	@Bean("camel")
	// @ConditionalOnProperty(name="enableOracleQueueConnection",
	// havingValue="true")
	public CamelServiceImpl camelService(
			@Qualifier("oracleQueueCredentials") UserCredentialsConnectionFactoryAdapter oracleQueueCredentials,
			RouteBuilder routeBuider, ProcessEngine processEngine) throws Exception {
		CamelServiceImpl camelService = new CamelServiceImpl();
		camelService.setProcessEngine(processEngine);
		camelService.setCamelContext(camelContext);

		CamundaBpmComponent component = new CamundaBpmComponent(processEngine);
		component.setCamelContext(camelContext);
		camelContext.addComponent("camunda-bpm", component);
		JmsComponent oracleQueue = new JmsComponent();
		oracleQueue.setConnectionFactory(oracleQueueCredentials);
		camelContext.addComponent("oracleQueue", oracleQueue);

		camelContext.addRoutes(routeBuider);

		SpringCamelContext.setNoStart(false);
		camelContext.start();
		return camelService;

	}

	@Value("${spring.jersey.application-path}")
	private String contextPath;
	
	
//	@Bean
//	public ServletContextInitializer initializer() {
//		return new ServletContextInitializer() {
//		
//			@Override
//			public void onStartup(ServletContext servletContext) throws ServletException {
//				servletContext.addListener(new CustomJacksonDateFormatListener());
//				servletContext.setInitParameter("org.camunda.bpm.engine.rest.jackson.dateFormat",
//						"yyyy-MM-dd HH:mm:ss");
//				
//			}
//		};
//	}

//	@Bean
//	public FilterRegistrationBean processEngineAuthenticationFilter() {
//		FilterRegistrationBean registration = new FilterRegistrationBean();		
//		registration.setName("camunda-auth");
//		registration.setFilter(getProcessEngineAuthenticationFilter());
//		registration.addInitParameter("authentication-provider",
//				"org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider");
//		registration.addUrlPatterns("/"+contextPath+"/*");
//		return registration;
//	}
//
//	@Bean
//	public Filter getProcessEngineAuthenticationFilter() {
//		return new ProcessEngineAuthenticationFilter();
//	}

}