package uk.ac.ed.notify.config;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.SecurityScheme;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.List;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 *
 * @author rgood
 */
@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class SwaggerConfig {

    @Value("${swagger.oauth.url}")
    private String swaggerOAuthUrl;

    @Value("${swagger.basepath}")
    private String swaggerBasePath;

    @Value("${swagger.docPath}")
    private String swaggerDocPath;

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("notification-ms")
                .select()
                .paths(paths())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(newArrayList(oauth2()));
    }

    private Predicate<String> paths() {
        return or(
                regex("/emergencynotifications"), 
                regex("/usernotifications/.*"), 
                regex("/notification/.*"), 
                regex("/notifications/.*")
                );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Notification Backbone JSON API")
                .description("This service provides the ability for publishers and subscribers to create/edit/delete/view notifications as appropriate.")
                .version("2.0")
                .build();
    }

    @Bean
    SecurityScheme oauth2() {
        return new OAuthBuilder()
                .name("oauth2")
                .grantTypes(grantTypes())
                .scopes(scopes())
                .build();
    }

    List<AuthorizationScope> scopes() {
        return newArrayList(
                (new AuthorizationScope("notification.read","Read access on the notification API")),
                (new AuthorizationScope("notification.write","Write access on the notification API"))
                );
    }

    List<GrantType> grantTypes() {
        ImplicitGrant implicitGrant = new ImplicitGrant(new LoginEndpoint(swaggerOAuthUrl),"access_code");
        return newArrayList(implicitGrant);
    }
    
}
