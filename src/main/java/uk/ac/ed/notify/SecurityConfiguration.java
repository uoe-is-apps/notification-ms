package uk.ac.ed.notify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String NOTIFICATION_API_UI = "notification-api-ui";

    /**
     * Username for the 'notification-ui' (service) user.
     */
    @Value("${uk.ac.ed.notify.security.notificationUiUsername:notification-ui}")
    private String notificationUiUsername;

    /**
     * Password for the 'notification-ui' (service) user.
     */
    @Value("${uk.ac.ed.notify.security.notificationUiPassword:CHANGEME}")
    private String notificationUiPassword;

    /**
     * Comma-separated list of authorities that have access to read notifications.
     */
    @Value("${uk.ac.ed.notify.security.readAuthority:notification.read}")
    private String readAuthority;

    /**
     * Comma-separated list of authorities that have access to write notifications.
     */
    @Value("${uk.ac.ed.notify.security.writeAuthority:notification.write}")
    private String writeAuthority;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(notificationUiUsername)
                        .password(notificationUiPassword)
                        .authorities(readAuthority, writeAuthority);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                /*
                 * Use SessionCreationPolicy.STATELESS so that "Spring Security will never create an
                 * HttpSession and it will never use it to obtain the SecurityContext."  This
                 * approach means that every request needs to carry authentication.
                 */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                /*
                 * Disable CSRF (not appropriate for a collection of REST APIs that handle security
                 * on each request)
                 */
                .csrf().disable()

                /*
                 * Set up the CorsFilter
                 */
                .addFilterBefore(corsFilter(), ChannelProcessingFilter.class)

                /*
                 * Configure HTTP Basic Authentication
                 */
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                .and()

                .authorizeRequests()
                // Anyone may access the Swagger UI
                .antMatchers("/", "/lib/*", "/images/*", "/css/*", "/swagger-ui.js","/redoc.html","/swagger-ui.min.js","/swagger-resources","/swagger-resources/*" ,"/v2/api-docs", "/fonts/*", "/v2/api-docs/*", "/api-docs/default/*", "/o2c.html","index.html","/webjars/**","/hystrix/**","/hystrix.stream","/proxy.stream","/healthcheck","/providers","/provider/**").permitAll()
                // APIs require the proper authority
                .antMatchers(HttpMethod.GET, "/notification/**").hasAuthority(readAuthority)
                .antMatchers(HttpMethod.GET, "/notifications/**").hasAuthority(readAuthority)
                .antMatchers(HttpMethod.POST, "/notification/**").hasAuthority(writeAuthority)
                .antMatchers(HttpMethod.PUT, "/notification/**").hasAuthority(writeAuthority)
                .antMatchers(HttpMethod.DELETE, "/notification/**").hasAuthority(writeAuthority)
                .antMatchers(HttpMethod.GET, "/usernotifications/**").hasAuthority(readAuthority)
                .antMatchers(HttpMethod.GET, "/emergencynotifications").hasAuthority(readAuthority)
                .antMatchers(HttpMethod.GET, "/push-subscription").hasAuthority(readAuthority)
                // And other requests require authentication
                .anyRequest().authenticated();

    }

    /**
     * Sends "HTTP 401 Unauthorized" for unauthenticated requests.
     */
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // you USUALLY want this
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
