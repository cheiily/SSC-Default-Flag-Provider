package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus
import java.nio.file.Path
import kotlin.io.path.toPath
import kotlin.test.assertEquals

class FlagProviderTest {
    @Test
    fun getDefaultFlag() {
        val flagProvider = FlagProvider()
        val defaultFlag = flagProvider.getFlag("none")
        assertNotNull(defaultFlag, "Default flag should not be null")
        assertTrue(defaultFlag.width > 0 && defaultFlag.height > 0, "Default flag should have valid dimensions")
    }

    @Test
    fun getDefaultFlagURL() {
        val flagProvider = FlagProvider()
        val defaultFlagURL = flagProvider.getFlagURL("none")
        assertNotNull(defaultFlagURL, "Default flag URL should not be null")
        assertTrue(defaultFlagURL.toString().endsWith("none.png"), "Default flag URL should point to the default flag image")
    }

    @Test
    fun getDefaultFlagBase64() {
        val flagProvider = FlagProvider()
        val defaultFlagBase64 = flagProvider.getFlagBase64("none")
        assertNotNull(defaultFlagBase64, "Default flag Base64 should not be null")
        assertTrue(defaultFlagBase64.isNotEmpty(), "Default flag Base64 should not be empty")
    }

    @Test
    fun getFlag() {
        val flagProvider = FlagProvider()
        flagProvider.acceptRequiredModuleStatus(
            listOf(mockResourcePathStatusOn())
        )
        var flag = flagProvider.getFlag("pl")
        val plFlag = flag
        assertNotNull(flag, "Flag should not be null")
        assertTrue(flag.width > 0 && flag.height > 0, "Flag should have valid dimensions")

        flag = flagProvider.getFlag("PL")
        assertNotNull(flag, "Flag should not be null regardless of case")
        assertTrue(flag.width > 0 && flag.height > 0, "Flag should have valid dimensions regardless of case")
        assertEquals(plFlag.height, flag.height, "Flag should be the same for files found with different cases")
        assertEquals(plFlag.width, flag.width, "Flag should be the same for files found with different cases")

        flag = flagProvider.getFlag("al")
        assertNotNull(flag, "Flag should not be null for 'al'")
        assertTrue(flag.width > 0 && flag.height > 0, "Flag should have valid dimensions for 'al'")

        val defaultFlag = flagProvider.getFlag("none")
        flag = flagProvider.getFlag("")
        assertNotNull(flag, "Flag should not be null when no valid flag is found")
        assertEquals(defaultFlag.width, flag.width, "Flag width should match default flag width when no valid flag is found")
        assertEquals(defaultFlag.height, flag.height, "Flag height should match default flag height when no valid flag is found")
    }

    @Test
    fun getFlagURL() {
        val flagProvider = FlagProvider()
        flagProvider.acceptRequiredModuleStatus(
            listOf(mockResourcePathStatusOn())
        )
        var flagURL = flagProvider.getFlagURL("pl")
        val plUrl = flagURL
        assertNotNull(flagURL, "Flag URL should not be null")
        assertTrue(flagURL.toString().endsWith("pl.png"), "Flag URL should point to the correct flag image")
        flagURL = flagProvider.getFlagURL("PL")
        assertNotNull(flagURL, "Flag URL should not be null")
        assertTrue(flagURL.toString().endsWith("pl.png"), "Flag URL should point to the correct flag image regardless of case")
        assertEquals(plUrl, flagURL, "Flag URL should be the same for files found with different cases")

        flagURL = flagProvider.getFlagURL("al")
        assertNotNull(flagURL, "Flag URL should not be null")
        assertTrue(flagURL.toString().endsWith("al.png"), "Flag URL should point to the correct flag image")

        val noneUrl = flagProvider.getFlagURL("none")
        flagURL = flagProvider.getFlagURL("")
        assertNotNull(flagURL, "Flag URL should not be null")
        assertTrue(flagURL.toString().endsWith("none.png"), "Flag URL should point to the default flag image when no valid flag is found")
        assertEquals(noneUrl, flagURL, "Flag URL should be the same as the default flag URL when no valid flag is found")
    }

    @Test
    fun getFlagBase64() {
        val flagProvider = FlagProvider()
        flagProvider.acceptRequiredModuleStatus(
            listOf(mockResourcePathStatusOn())
        )
        var flagBase64 = flagProvider.getFlagBase64("pl")
        val plEnc = flagBase64
        assertNotNull(flagBase64, "Flag Base64 should not be null")
        assertTrue(flagBase64.isNotEmpty(), "Flag Base64 should not be empty")

        flagBase64 = flagProvider.getFlagBase64("PL")
        assertNotNull(flagBase64, "Flag Base64 should not be null")
        assertTrue(flagBase64.isNotEmpty(), "Flag Base64 should not be empty regardless of case")
        assertEquals(plEnc, flagBase64, "Flag Base64 should be the same for files found with different cases")

        flagBase64 = flagProvider.getFlagBase64("al")
        assertNotNull(flagBase64, "Flag Base64 should not be null")
        assertTrue(flagBase64.isNotEmpty(), "Flag Base64 should not be empty")

        val defaultFlagBase64 = flagProvider.getFlagBase64("none")
        flagBase64 = flagProvider.getFlagBase64("")
        assertNotNull(flagBase64, "Flag Base64 should not be null")
        assertTrue(flagBase64.isNotEmpty(), "Flag Base64 should not be empty when no valid flag is found")
        assertEquals(defaultFlagBase64, flagBase64, "Flag Base64 should be the same as the default flag Base64 when no valid flag is found")
    }

    @Test
    fun getInfo() {
        val flagProvider = FlagProvider()
        val info = flagProvider.getInfo()
        assertNotNull(info, "Plugin info should not be null")
        assertTrue(info.name.isNotEmpty(), "Plugin name should not be empty")
        assertEquals(info.name, "Flag Provider (Monocle)", "Plugin name should match expected value")
        assertTrue(info.description.isNotEmpty(), "Plugin description should not be empty")
        assertTrue(info.version.isNotEmpty(), "Plugin version should not be empty")
        assertEquals(info.version, "1.3.1", "Plugin version should match expected value")
        assertTrue(info.author.isNotEmpty(), "Plugin author should not be empty")
        assertEquals(info.author, "cheily", "Plugin author should match expected value")
    }

    @Test
    fun getHealthStatus() {
        val flagProvider = FlagProvider()
        val healthStatus = flagProvider.getHealthStatus()
        assertNotNull(healthStatus, "Health status should not be null")
        assertTrue(healthStatus.message.contains("Missing"), "Default health status message should indicate missing resources")
        healthStatus.healthRecords.forEach{ record ->
            assertNotNull(record, "Health record should not be null")
            assertTrue(record.status == PluginHealthData.HealthStatus.NOT_READY, "Default health record status should be NOT_READY")
            assertTrue(record.message.contains("Missing"), "Default health record message should indicate missing resources")
        }
    }

    @Test
    fun acceptRequiredModuleStatus() {
        val flagProvider = FlagProvider()
        flagProvider.healthStatus.healthRecords.forEach { record ->
            assertTrue(record.status == PluginHealthData.HealthStatus.NOT_READY, "Before initialization the health record status should be NOT_READY")
        }
        flagProvider.acceptRequiredModuleStatus(listOf(mockNofuncStatusOn()))
        flagProvider.healthStatus.healthRecords.forEach { record ->
            assertTrue(record.status == PluginHealthData.HealthStatus.READY, "After initialization the health record status should be READY")
        }
        flagProvider.acceptRequiredModuleStatus(listOf(mockNofuncStatusOff()))
        flagProvider.healthStatus.healthRecords.forEach { record ->
            assertTrue(record.status == PluginHealthData.HealthStatus.NOT_READY, "After disabling the module the health record status should be NOT_READY")
        }
    }

    private fun mockDefinition() = ResourceModuleDefinitionData(
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

    private fun mockNofuncStatusOn() = ResourceModuleStatus(
        true,
        true,
        true,
        mockDefinition(),
        "path/to/installation".toPath(),
        "path/to/installation/file".toPath(),
        "path/to/".toPath()
    )

    private fun mockNofuncStatusOff() = ResourceModuleStatus(
        false,
        false,
        false,
        mockDefinition(),
        "path/to/installation".toPath(),
        "path/to/installation/file".toPath(),
        "path/to/".toPath()
    )

    private fun mockResourcePathStatusOn() = ResourceModuleStatus(
        true,
        true,
        true,
        mockDefinition(),
        javaClass.getResource("pl.png")!!.toURI().toPath().parent,
        javaClass.getResource("pl.png")!!.toURI().toPath().parent,
        javaClass.getResource("pl.png")!!.toURI().toPath().parent
    )
}

private fun String.toPath() = Path.of(this)
