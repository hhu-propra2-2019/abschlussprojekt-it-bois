package mops.gruppen2.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "mops.gruppen2")
public class ArchUnitTest {

    @ArchTest
    public static final ArchRule controllerClassesShouldBeAnnotatedWithControllerOrRestControllerAnnotation = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(Controller.class)
            .orShould().beAnnotatedWith(RestController.class);

    @ArchTest
    public static final ArchRule controllerClassesShouldHaveControllerInName = classes()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    public static final ArchRule controllerClassesShouldBeInControllerPackage = classes()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..controller..");

    @ArchTest
    public static final ArchRule noClassesWithControllerOrRestControllerAnnotationShouldResideOutsideOfControllerPackage = noClasses()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().resideOutsideOfPackage("..controller..");

    @ArchTest
    public static final ArchRule controllerClassesShouldHaveRequestMappingAnnotation = classes()
            .that().resideInAPackage("..controller..")
            .and().haveSimpleNameEndingWith("Controller")
            .and().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().beAnnotatedWith(RequestMapping.class);

    @ArchTest
    public static final ArchRule controllerClassesShouldNotDependOnEachOther = noClasses()
            .that().haveSimpleNameEndingWith("Controller")
            .should().dependOnClassesThat().haveNameMatching("Controller");

    @ArchTest
    public static final ArchRule serviceClassesShouldHaveServiceInName = classes()
            .that().areAnnotatedWith(Service.class)
            .should().haveSimpleNameEndingWith("Service");

    @ArchTest
    public static final ArchRule serviceClassesShouldBeAnnotatedWithService = classes()
            .that().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class);

    @ArchTest
    public static final ArchRule serviceClassesShouldBeInServicePackage = classes()
            .that().areAnnotatedWith(Service.class)
            .should().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule serviceClassesShouldOnlyBeAccessedByControllerOrServiceClasses = classes()
            .that().resideInAPackage("..service..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

    @ArchTest
    public static final ArchRule domainClassesShouldNotAccessOtherClasses = noClasses()
            .that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAnyPackage("..controller..", "..repository..", "..security..", "..service..");

    @ArchTest
    public static final ArchRule repositoryClassesThatImplementCrudRepositoryShouldBeNamedRepository = classes()
            .that().implement(CrudRepository.class)
            .should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    public static final ArchRule repositoryClassesShouldBeInRepositoryPackage = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..repository..");

}
