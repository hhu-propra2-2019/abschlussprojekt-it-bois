package mops.gruppen2;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "mops.gruppen2")
public class ArchTests {

    @ArchTest
    public static final ArchRule controllersCorrectlyNamed = classes()
            .that()
            .resideInAPackage("..controllers..")
            .should()
            .haveSimpleNameEndingWith("Controller");

    @ArchTest
    public static final ArchRule repositoriesCorrectlyNamed = classes()
            .that()
            .resideInAPackage("..repositories..")
            .should()
            .haveSimpleNameEndingWith("Repository");

    @ArchTest
    public static final ArchRule servicesCorrectlyAnnotated = classes()
            .that()
            .resideInAPackage("..services..")
            .should()
            .haveSimpleNameEndingWith("Service");

    @ArchTest
    public static final ArchRule controllersCorrectlyAnnotated = classes()
            .that()
            .resideInAPackage("..controllers..")
            .should()
            .beAnnotatedWith(RequestMapping.class);
}
