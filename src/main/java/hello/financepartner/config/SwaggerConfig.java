package hello.financepartner.config;



//@OpenAPIDefinition(
//        info = @Info(title = "Finance Partner API 명세서",
//                description = "가계부 공유서비스 API 명세서",
//                version = "v1"))
//@RequiredArgsConstructor
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public GroupedOpenApi chatOpenApi() {
//        String[] paths = {"/v1/**"};
//
//        return GroupedOpenApi.builder()
//                .group("Finance Partner API v1")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//
//}

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@OpenAPIDefinition(
        info = @Info(title = "Finance Partner API 명세서",
                description = "가계부 공유서비스 API 명세서",
                version = "v1"))
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearer");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer", securityScheme))
                .security(Arrays.asList(securityRequirement));
    }
}