package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider

import org.json.JSONObject
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData

object DefinitionParser {
    fun parse(definition: String) : ResourceModuleDefinitionData {
        val obj = JSONObject(definition)
        return ResourceModuleDefinitionData(
            obj.getString("definitionVersion"),
            obj.getString("name"),
            obj.optString("category"),
            obj.getString("installPath"),
            obj.getString("shortDescription"),
            obj.optString("description"),
            obj.getString("version"),
            obj.optString("isoDate"),
            obj.getString("author"),
            obj.getString("url"),
            obj.getBoolean("externalUrl"),
            obj.getString("resourceType"),
            obj.optString("archiveType"),
            obj.optString("serviceInterface"),
            obj.optBoolean("autoinstall"),
            obj.optBoolean("autorun"),
            obj.optString("checksum")
        )
    }
}
