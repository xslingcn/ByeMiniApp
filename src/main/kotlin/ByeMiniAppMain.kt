import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.utils.info
object ByeMiniAppMain : KotlinPlugin(
    JvmPluginDescription(
        id = "sh.xsl.byeMiniApp",
        name = "ByeMiniApp",
        version = "1.0.0-SNAPSHOT"
    ) {
        author("xsling")
        info(
            """
            消除小程序分享
        """.trimIndent()
        )
    }
) {
    // TODO: provide configuration
    private val enabled_groups = intArrayOf(123456)
    override fun onEnable() {
        logger.info { "ByeMiniApp Loaded" }
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent> {
//            logger.info("[ByeMiniApp]" + message.contentToString())
            enabled_groups.forEach { id ->
                if(group.id == id.toLong()){
                    Formatter(message).get()?.let {
                        group.sendMessage(it)
                    }
                }
            }
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
//            logger.info("[ByeMiniApp]" + message.contentToString())
        }
        eventChannel.subscribeAlways<NewFriendRequestEvent> {
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            accept()
        }

    }
}