package net.deneo.adup.gui.suspects;

import net.deneo.adup.gui.UMenuPagged;
import net.deneo.adup.gui.prompts.TimeSpanPrompt;
import net.deneo.adup.utility.ConversationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

public class FilterMenu extends Menu {
    @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
    private final Button timeSpan;
    @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
    private final Button back;

    FilterMenu(UMenuPagged<?> parent) {
        setTitle("&5&lADUP §8§l» Suspects");
        setSize(9);

        back = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                parent.update();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.PLAYER_HEAD,
                        "&7Back"
                ).skullOwner("MHF_arrowleft").glow(true).make();
            }
        };

        timeSpan = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                TimeSpanPrompt prompt = new TimeSpanPrompt() {
                    @Override
                    public void onResult(String in) {
                        FilterHandler.setTimeSpan(player.getUniqueId(), in);
                        new FilterMenu(parent).displayTo(player);
                    }
                };

                ConversationUtil.buildAndBegin(prompt, player);
            }

            @Override
            public ItemStack getItem() {
                String timeSpan = FilterHandler.getTimeSpan(getViewer().getUniqueId());
                boolean isTimeSpan = !timeSpan.isEmpty();

                List<String> lore = new ArrayList<>();
                lore.add("&7Only show suspects between a specific time span");

                if (isTimeSpan) lore.add("&7&l" + timeSpan);

                return ItemCreator.of(
                        isTimeSpan ? CompMaterial.LIME_DYE : CompMaterial.GRAY_DYE,
                        "&5&lTime span",
                        lore
                ).glow(true).make();
            }
        };
    }
}