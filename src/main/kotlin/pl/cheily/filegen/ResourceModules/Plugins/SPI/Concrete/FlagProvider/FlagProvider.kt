package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider

import pl.cheily.filegen.ResourceModules.Plugins.SPI.Requires
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.toPath

@Requires(resourceModuleCategories = ["flags-resources"])
class FlagProvider : IFlagProvider {
    companion object {
        private const val DEFAULT_FLAG_NAME = "none.png"
        private val VALID_FORMATS = listOf("png", "jpg", "jpeg", "gif", "bmp")
    }

    private val pathMap: MutableMap<String, Path> = mutableMapOf()
    private val defaultFlag = javaClass.getResource(DEFAULT_FLAG_NAME)?.toURI()?.toPath()
        ?: "".toPath()

    private val definition: ResourceModuleDefinitionData =
        DefinitionParser.parse(javaClass.getResource("definition.sscm.json")!!.readText())


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

    override fun getAvailableFlags(): Set<String> =
        pathMap.values
            .flatMap { it.listDirectoryEntries("*.{${VALID_FORMATS.joinToString(",")}}") }
            .map { it.fileName.toString() }
            .toSet()

    override fun getInfo() = definition

    override fun getHealthStatus(): PluginHealthData {
        val status =
            if (pathMap.isEmpty()) PluginHealthData.HealthStatus.NOT_READY else PluginHealthData.HealthStatus.READY
        val message =
            if (status != PluginHealthData.HealthStatus.READY) "Missing resource modules." else "Method primed for modules: ${pathMap.keys}."
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
        modules.mapNotNull { it }
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
