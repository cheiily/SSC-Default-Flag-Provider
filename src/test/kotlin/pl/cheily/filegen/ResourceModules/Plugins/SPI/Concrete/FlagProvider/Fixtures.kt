package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider

import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus
import kotlin.io.path.toPath

object Fixtures {
    val mockDefinition = ResourceModuleDefinitionData(
        "1",
        "Test Module",
        "Test Modules",
        "path/to/installation",
        "file.extension",
        "This is a test module for unit testing purposes.",
        "This is a test module for unit testing purposes.",
        "0.0.0-test-version",
        "2025-01-01T00:00:00+00:00",
        "test-author",
        "",
        false,
        "STATICS_COLLECTION",
        "",
        false,
        false,
        ""
    )

    val mockNofuncStatusOff = ResourceModuleStatus(
        false,
        false,
        false,
        mockDefinition,
        "path/to/installation".toPath(),
        "path/to/installation/file".toPath(),
        "path/to/".toPath()
    )

    val mockResourcePathStatusOn = ResourceModuleStatus(
        true,
        true,
        true,
        mockDefinition,
        javaClass.getResource("pl.png")!!.toURI().toPath().parent,
        javaClass.getResource("pl.png")!!.toURI().toPath().parent,
        javaClass.getResource("pl.png")!!.toURI().toPath().parent
    )
}