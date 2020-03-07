package mops.gruppen2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
public class Gruppen2Application {
    public static void main(String[] args) {
        SpringApplication.run(Gruppen2Application.class, args);
    }

    @Bean
    public Docket productAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/products/**"))
                .apis(RequestHandlerSelectors.basePackage("mops.gruppen2"))
                .build()
                .apiInfo(apiMetadata());
    }

    private ApiInfo apiMetadata() {
        return new ApiInfo(
                "Gruppenbildung API",
                "API zum anfragen/aktualisieren der Gruppendaten.",
                "0.0.1",
                "Free to use",
                new Contact("gruppen2", "https://github.com/hhu-propra2/abschlussprojekt-it-bois", ""),
                "",
                "",
                Collections.emptyList()
        );
    }
}
