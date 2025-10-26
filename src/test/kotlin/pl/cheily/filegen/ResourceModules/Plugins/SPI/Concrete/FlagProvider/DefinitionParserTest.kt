package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider

import org.json.JSONException
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.Fixtures.mockDefinition
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefinitionParserTest {
    @Test
    fun providerParsesAnyString() {
        val obj = JSONObject("""{"any":"value"}""")
        val value = obj.getString("any")
        assertEquals(value, "value")
    }

    @Test
    fun providerParsesNullAsNull() {
        val obj = JSONObject("""{"any":null}""")
        val value = obj.optString("any", null)
        assertNull(value)
    }

    @Test
    fun providerThrowsOnGetNull() {
        val obj = JSONObject("""{"any":null}""")
        assertThrows<JSONException> {
            obj.getString("any")
        }
    }

    @Test
    fun parsesSampleDefinition() {
        val defition = DefinitionParser.parse(
            """
            {
                "definitionVersion": "${mockDefinition.definitionVersion}",
                "name": "${mockDefinition.name}",
                "installPath": "${mockDefinition.installPath}",
                "installFileName": "${mockDefinition.installFileName}",
                "shortDescription": "${mockDefinition.shortDescription}",
                "version": "${mockDefinition.version}",
                "author": "${mockDefinition.author}",
                "url": "${mockDefinition.url}",
                "externalUrl": ${mockDefinition.externalUrl},
                "resourceType": "${mockDefinition.resourceType}"
            }
            """.trimIndent()
        )
        assertNotNull(defition)
    }

    @Test
    fun parsesRealDefinition() {
        val definitionContent = javaClass.getResource("definition.sscm.json")!!.readText()
        val definition = DefinitionParser.parse(definitionContent)
        assertEquals("1", definition.definitionVersion)
        assertEquals("pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider", definition.serviceInterface)
        assertEquals("PLUGIN_JAR", definition.resourceType)

        assertEquals(null, definition.checksum)
    }
}