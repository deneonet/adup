package net.deneo.adup.gui.prompts;

import net.deneo.adup.utility.ConversationUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class NumberPrompt extends StringPrompt {
    private final String promptText;

    public NumberPrompt(String promptText) {
        this.promptText = promptText;
    }

    public abstract void onResult(Integer number);

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player) context.getForWhom()).closeInventory();
        return "§5§lADUP §8§l» §7" + promptText;
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        UUID uuid = ((Player) con.getForWhom()).getUniqueId();
        if (answer.equals("exit")) {
            return END_OF_CONVERSATION;
        }

        if (!answer.chars().allMatch(Character::isDigit)) {
            return this;
        }

        ConversationUtil.conversations.get(uuid).abandon();
        onResult(Integer.parseInt(answer));
        ConversationUtil.conversations.remove(uuid);
        return END_OF_CONVERSATION;
    }
}