package uk.ac.ed.notify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * Created by rgood on 21/10/2015.
 */
@Configuration
@EnableResourceServer
public class OAuthResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        resources.resourceId("notification");

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.anonymous().and()
                .authorizeRequests()
                .antMatchers("/", "/lib/*", "/images/*", "/css/*", "/swagger-ui.js", "/api-docs", "/fonts/*", "/api-docs/*", "/api-docs/default/*", "/o2c.html").permitAll()
                .antMatchers(HttpMethod.GET, "/notification/**").access("#oauth2.hasScope('notification.read')")
                .antMatchers(HttpMethod.POST, "/notification/**").access("#oauth2.hasScope('notification.write')")
                .antMatchers(HttpMethod.GET, "/usernotifications/**").access("#oauth2.hasScope('notification.read')")
                .anyRequest().authenticated();
    }

}
