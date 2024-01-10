package net.deneo.adup.gui.prompts;

import net.deneo.adup.utility.ConversationUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AddWhitelistPrompt extends StringPrompt {
    public abstract void onResult(String name);

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player) context.getForWhom()).closeInventory();
        return "§5§lADUP §8§l» §7Enter the name of the player to add to the whitelist";
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        UUID uuid = ((Player) con.getForWhom()).getUniqueId();
        if (answer.equals("exit")) {
            return END_OF_CONVERSATION;
        }

        ConversationUtil.conversations.get(uuid).abandon();
        onResult(answer);
        ConversationUtil.conversations.remove(uuid);
        return END_OF_CONVERSATION;
    }
}