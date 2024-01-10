package net.deneo.adup.gui.prompts;

import net.deneo.adup.utility.ConversationUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class TimeSpanPrompt extends StringPrompt {
    public abstract void onResult(String in);

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player) context.getForWhom()).closeInventory();
        return "§5§lADUP §8§l» §7Enter the time span, follow this layout: \n§5Older than §7:§5 Newer than\n" +
                "§5§lADUP §8§l» §7Example: §l§51h:2h";
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        UUID uuid = ((Player) con.getForWhom()).getUniqueId();
        if (answer.equals("exit")) {
            return END_OF_CONVERSATION;
        }
        if (!answer.contains(":") || !Character.isDigit(answer.charAt(0)) || !Character.isDigit(answer.charAt(answer.indexOf(":") + 1))) {
            return this;
        }
        ConversationUtil.conversations.get(uuid).abandon();
        onResult(answer);
        ConversationUtil.conversations.remove(uuid);
        return END_OF_CONVERSATION;
    }
}