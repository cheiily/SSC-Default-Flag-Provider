package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider

import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.toPath

@IPluginBase.RequiresCategory(resourceModuleCategory = "flags")
class FlagProvider : IFlagProvider {
    companion object {
        private const val DEFAULT_FLAG_NAME = "none.png"
    }

    private val pathMap: MutableMap<String, Path> = mutableMapOf()
    private val defaultFlag = javaClass.getResource(DEFAULT_FLAG_NAME)?.toURI()?.toPath()
        ?: "".toPath()

    override fun getFlag(ISO2: String): BufferedImage = ImageIO.read(getFlagURL(ISO2))


    override fun getFlagURL(ISO2: String): URL =
        pathMap.values.firstNotNullOfOrNull {
            it.listDirectoryEntries("$ISO2.*").firstOrNull()
        }?.toUri()?.toURL()
            ?: defaultFlag.toUri().toURL()


    override fun getFlagBase64(ISO2: String): String =
        java.util.Base64.getEncoder()
            .encode(getFlagURL(ISO2).readBytes())
            .toString(Charset.defaultCharset())


    override fun getInfo() = PluginData(
        "Flag Provider",
        """
            This module is responsible for retrieving flag files as they are requested by the core app.
            The requests are handled with a flag name, that should usually refer to the ISO2 country code, 
            but in reality it's almost directly mapped to the according file name, and so it largely depends on the end user
            and resource modules and their naming conventions.
            This module does not contain any actual resources - it is just the file-retrieval logic.
        """.trimIndent(),
        "1.0.0",
        "2025-07-28T01:04:59+02:00",
        "_cheily"
    )

    override fun getHealthStatus(): PluginHealthData {
        val status = if (pathMap.isEmpty()) PluginHealthData.HealthStatus.NOT_READY else PluginHealthData.HealthStatus.READY
        val message = if (status != PluginHealthData.HealthStatus.READY) "Missing resource modules." else "Method primed for modules: ${pathMap.keys}."
        return PluginHealthData(
            listOf(
                PluginHealthData.HealthRecord("getFlag", status, message),
                PluginHealthData.HealthRecord("getFlagURL", status, message),
                PluginHealthData.HealthRecord("getFlagBase64", status, message)
            ),
            message
        )
    }

    override fun acceptRequiredModuleStatus(modules: List<ResourceModuleStatus?>) {
        modules.mapNotNull{ it }
            .filter { it.isEnabled }
            .forEach {
                pathMap.putIfAbsent(it.definition.name, it.installDirPath)
            }

        modules.mapNotNull { it }
            .filter { !it.isEnabled }
            .forEach {
                pathMap.remove(it.definition.name)
            }
    }
}

private fun String.toPath(): Path {
    return Path.of(this)
}
