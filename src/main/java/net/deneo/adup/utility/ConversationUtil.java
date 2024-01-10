package net.deneo.adup.utility;

import net.deneo.adup.Adup;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConversationUtil {
    private static final ConversationFactory cf = new ConversationFactory(Adup.getPlugin(Adup.class));
    public static HashMap<UUID, Conversation> conversations = new HashMap<>();

    public static void buildAndBegin(Prompt prompt, Player player) {
        Conversation conversation = cf.withFirstPrompt(prompt).withLocalEcho(true).buildConversation(player);
        conversation.begin();
        conversations.put(player.getUniqueId(), conversation);
    }
}
