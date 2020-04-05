package mops.gruppen2.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "mops.gruppen2", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureTest {

    private static final Architectures.LayeredArchitecture layeredArchitecture = Architectures
            .layeredArchitecture()
            .layer("Domain").definedBy("..domain..")
            .layer("Service").definedBy("..service")
            .layer("Controller").definedBy("..controller..")
            .layer("Repository").definedBy("..repository..");

    @ArchTest
    public static final ArchRule domainLayerShouldOnlyBeAccessedByServiceAndControllerLayer = layeredArchitecture
            .whereLayer("Domain")
            .mayOnlyBeAccessedByLayers("Service", "Controller");

    @ArchTest
    public static final ArchRule serviceLayerShouldOnlyBeAccessedByControllerLayer = layeredArchitecture
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("Controller");

    @ArchTest
    public static final ArchRule repositoryLayerShouldOnlyBeAccessedByServiceLayer = layeredArchitecture
            .whereLayer("Repository")
            .mayOnlyBeAccessedByLayers("Service");

    @ArchTest
    public static final ArchRule controllerLayerShouldNotBeAccessedByAnyLayer = layeredArchitecture
            .whereLayer("Controller")
            .mayNotBeAccessedByAnyLayer();

}
