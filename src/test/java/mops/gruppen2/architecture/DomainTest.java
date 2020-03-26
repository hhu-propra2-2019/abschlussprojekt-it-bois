package mops.gruppen2.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.gruppen2.domain.exception.EventException;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "mops.gruppen2", importOptions = ImportOption.DoNotIncludeTests.class)
public class DomainTest {

    @ArchTest
    public static final ArchRule domainClassesShouldNotAccessClassesFromOtherPackagesExceptDomainItself = noClasses()
            .that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAnyPackage("..controller..", "..repository..", "..security..", "..service..");

    @ArchTest
    public static final ArchRule eventClassesShouldBeInEventPackage = classes()
            .that().haveSimpleNameEndingWith("Event")
            .should().resideInAPackage("..domain.event..");

    @ArchTest
    public static final ArchRule classesInEventPackageShouldHaveEventInName = classes()
            .that().resideInAPackage("..domain.event..")
            .should().haveSimpleNameEndingWith("Event");

    @ArchTest
    public static final ArchRule exceptionClassesShouldBeInExceptionPackage = classes()
            .that().haveSimpleNameEndingWith("Exception")
            .should().resideInAPackage("..domain.exception..");

    @ArchTest
    public static final ArchRule classesInExceptionPackageShouldHaveExceptionInName = classes()
            .that().resideInAPackage("..domain.exception..")
            .should().haveSimpleNameEndingWith("Exception");

    @ArchTest
    public static final ArchRule classesThatAreAssignableToExceptionShouldHaveExceptionInName = classes()
            .that().areAssignableTo(Exception.class)
            .should().haveSimpleNameEndingWith("Exception");

    @ArchTest
    public static final ArchRule classesThatHaveExceptionInNameShouldBeAssignableToExceptionClass = classes()
            .that().haveSimpleNameEndingWith("Exception")
            .should().beAssignableTo(EventException.class);

    @ArchTest
    public static final ArchRule classesInDtoPackageShouldHaveDtoInName = classes()
            .that().resideInAPackage("..domain.dto..")
            .should().haveSimpleNameEndingWith("DTO");

    @ArchTest
    public static final ArchRule dtoClassesShouldBeInDtoPackage = classes()
            .that().haveSimpleNameEndingWith("DTO")
            .should().resideInAPackage("..domain.dto..");

}
