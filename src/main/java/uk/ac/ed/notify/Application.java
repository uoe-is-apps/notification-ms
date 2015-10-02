package uk.ac.ed.notify;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * Created by rgood on 18/09/2015.
 */
@Configuration
@ComponentScan({"uk.ac.ed.notify"})
@EntityScan("uk.ac.ed.notify.entity")
@EnableAutoConfiguration
@SpringBootApplication
@EnableOAuth2Resource
public class Application {

    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.remoteauthentication}")
    String remoteAuthentication;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static RemoteIpValve createRemoteIpValves() {
        RemoteIpValve remoteIpValve = new RemoteIpValve();
        remoteIpValve.setRemoteIpHeader("x-forwarded-for");
        remoteIpValve.setProtocolHeader("x-forwarded-proto");
        return remoteIpValve;
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        if (tomcatAjpEnabled) {
            Connector ajpConnector = new Connector("AJP/1.3");
            ajpConnector.setProtocol("AJP/1.3");
            ajpConnector.setPort(ajpPort);
            ajpConnector.setSecure(false);
            ajpConnector.setAllowTrace(false);
            ajpConnector.setScheme("http");
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
        }

        return tomcat;
    }

    @Configuration
    @EnableResourceServer
    static class OauthResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.anonymous().and()
                    .authorizeRequests()
                    .antMatchers("/", "/lib/*","/images/*","/css/*","/swagger-ui.js","/api-docs","/fonts/*","/api-docs/*","/api-docs/default/*","/o2c.html").permitAll()
                    .anyRequest().authenticated();
        }

    }

}
