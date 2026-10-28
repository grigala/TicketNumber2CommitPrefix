package io.github.grigala

import org.junit.Assert.assertTrue
import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory

/**
 * For the sake of sanity check.
 */
class PluginXmlTest {

    @Test
    fun notificationGroupIdMatchesPluginXml() {
        val pluginXml = javaClass.classLoader.getResourceAsStream("META-INF/plugin.xml")!!
        val groups = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pluginXml)
            .getElementsByTagName("notificationGroup")
        val ids = (0 until groups.length).map { groups.item(it).attributes.getNamedItem("id").nodeValue }

        assertTrue(
            "plugin.xml registers notification groups $ids but the code uses '${InsertTicketPrefixAction.NOTIFICATION_GROUP_ID}'",
            InsertTicketPrefixAction.NOTIFICATION_GROUP_ID in ids,
        )
    }
}
