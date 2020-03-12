package mops.gruppen2.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "mops.gruppen2", importOptions = { ImportOption.DoNotIncludeTests.class })
public class ServiceTest {

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
    public static final ArchRule classesInServicePackageShouldHaveServiceInName = classes()
            .that().resideInAPackage("..service..")
            .should().haveSimpleNameEndingWith("Service");

    @ArchTest
    public static final ArchRule serviceClassesShouldOnlyBeAccessedByControllerOrServiceClasses = classes()
            .that().resideInAPackage("..service..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..", "..config..");

}
