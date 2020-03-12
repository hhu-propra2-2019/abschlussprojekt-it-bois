package mops.gruppen2.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "mops.gruppen2", importOptions = { ImportOption.DoNotIncludeTests.class })
public class ControllerTest {

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
    public static final ArchRule classesInControllerPackageShouldHaveControllerInName = classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    public static final ArchRule controllerClassesShouldNotDependOnEachOther = noClasses()
            .that().haveSimpleNameEndingWith("Controller")
            .should().dependOnClassesThat().haveNameMatching("Controller");

}
