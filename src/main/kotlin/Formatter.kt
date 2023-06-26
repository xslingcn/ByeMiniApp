import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.google.gson.Gson
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.MiraiExperimentalApi
import schema.GroupCardFallbackMessage
import schema.GroupMiniAppMessage
import schema.GroupStructMessage
import schema.GroupXMLMessage.msg

/**
 * **Formatter**<br></br>
 * Parse and format all messages, towards in-game interface.
 *
 * @since 2022/4/11 12:33
 */
class Formatter(private val message: MessageChain, private val messageString: String = message.contentToString()) {
    @OptIn(MiraiExperimentalApi::class)
    fun get(): MessageChain? {
        val chain = MessageChainBuilder()
        chain.append(QuoteReply(message))
        // match QQ music share messages
        if (message.size == 3 && message[2] is MusicShare) {
            return chain.append(groupMusicShare(message[2] as MusicShare)).build()
        }

        // match card messages
        if (message.size == 2 && (message[1] is LightApp || message[1] is SimpleServiceMessage)) {
            // QQ XML messages
            if (messageString.startsWith("<?xml")) return chain.append(groupXML()).build()
            // QQ mini app messages
            if (messageString.contains("com.tencent.miniapp")) return chain.append(groupMiniApp()).build()
            // QQ struct messages
            return if (messageString.contains("com.tencent.structmsg")) chain.append(groupStruct())
                .build() else chain.append(
                groupCardFallback()
            ).build()
        }
        return null
    }

    private fun groupMiniApp(): String {
        val fromJson = Gson().fromJson(
            messageString,
            GroupMiniAppMessage::class.java
        )
        val prompt = fromJson.prompt + "-"
        val title = "[" + fromJson.meta?.detail1?.desc + "]"
        val url = fromJson.meta?.detail1?.qqdocurl
        return prompt + title + url
    }

    private fun groupStruct(): String {
        val fromJson = Gson().fromJson(messageString, GroupStructMessage::class.java)
        val prompt = fromJson.prompt + "-"
        val title = fromJson.meta?.news?.desc
        val url = fromJson.meta?.news?.jumpUrl
        return prompt + title + url
    }

    private fun groupXML(): String {
        try {
            val xmlMapper = XmlMapper()
            val fromXML = xmlMapper.readValue(
                messageString,
                msg::class.java
            )
            val prompt = fromXML.brief + "-"
            val title = "[" + fromXML.item?.summary + "]"
            val url = fromXML.url
            return prompt + title + url
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun groupCardFallback(): String {
        val fromJson = Gson().fromJson(
            messageString,
            GroupCardFallbackMessage::class.java
        )
        val prompt = fromJson.prompt + "-"
        val title = fromJson.desc
        return prompt + title
    }

    private fun groupMusicShare(music: MusicShare): String {
        val summary = music.summary + " - "
        val title = music.title
        val url = music.jumpUrl
        return summary + title + url
    }
}