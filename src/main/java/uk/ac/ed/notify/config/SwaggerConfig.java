package uk.ac.ed.notify.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.*;
import com.mangofactory.swagger.models.dto.builder.OAuthBuilder;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgood on 29/05/2015.
 */
@Configuration
@EnableSwagger
@EnableAutoConfiguration
public class SwaggerConfig {


    @Value("${swagger.oauth.url}")
    private String swaggerOAuthUrl;


    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                //This info will be used in Swagger. See realisation of ApiInfo for more details.
                .apiInfo(new ApiInfo(
                        "Notification Backbone JSON API",
                        "This service provides the ability for publishers and subscribers to create/edit/delete/view notificaitons as appropriate.",
                        null,
                        null,
                        null,
                        null
                ))
                        //Here we disable auto generating of responses for REST-endpoints
                .useDefaultResponseMessages(false)
                        //Here we specify URI patterns which will be included in Swagger docs. Use regex for this purpose.
                .includePatterns("/usernotifications/*","/notification/.*","/emergencynotifications")
                .authorizationTypes(getAuthorizationTypes());

    }

    private List<AuthorizationType> getAuthorizationTypes()
    {
        List<AuthorizationType> authorizationTypes = new ArrayList<>();
        List<AuthorizationScope> scopes = new ArrayList<>();
        scopes.add(new AuthorizationScope("notification.read","Read access on the notification API"));
        scopes.add(new AuthorizationScope("notification.write","Write access on the notification API"));

        List<GrantType> grantTypes = new ArrayList<>();
        ImplicitGrant implicitGrant = new ImplicitGrant(new LoginEndpoint(swaggerOAuthUrl),"access_code");
        grantTypes.add(implicitGrant);

        AuthorizationType oauth = new OAuthBuilder()
                .scopes(scopes)
                .grantTypes(grantTypes)
                .build();
        authorizationTypes.add(oauth);
        return authorizationTypes;
    }

}
