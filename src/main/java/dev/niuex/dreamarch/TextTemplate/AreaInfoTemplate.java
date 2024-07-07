package dev.niuex.dreamarch.TextTemplate;

import dev.niuex.dreamarch.Arch.Area;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class AreaInfoTemplate implements TextTemplate {
    private static final TextColor tipColor = TextColor.color(0x9E9E9E);
    public static Component render(Area area) {
        return Component.empty()
                .append(Component.empty()
                        .append(Component.text("[")
                                .color(tipColor)
                        )
                        .append(Component.text(area.id)
                                .color(TextColor.color(0xF5F5F5))
                        )
                        .append(Component.text("]")
                                .color(tipColor)
                        )
                        .appendSpace()
                        .append(Component.text(area.getName())
                                .color(TextColor.color(0xEAE322))
                                .decoration(TextDecoration.BOLD, true)
                        )
                        .appendSpace()
                        .append(Component.text("建筑师：")
                                .color(tipColor)
                        )
                        .append(Component.text(area.getOwner())
                                .color(TextColor.color(0xEAE322))
                                .decoration(TextDecoration.BOLD, true)
                        )
                )
                .appendNewline()
                .append(Component.empty()
                        .append(Component.text("介绍：")
                                .color(tipColor)
                        )
                        .append(Component.text(area.getDescription())
                                .color(TextColor.color(0xFAFAFA))
                        )
                )
                .appendNewline()
                .append(Component.empty()
                        .append(Component.text("去看看")
                                .decoration(TextDecoration.BOLD, true)
                                .color(TextColor.color(0x6AC93E))
                                .clickEvent(ClickEvent.runCommand("/arch tp "+area.id))
                                .hoverEvent(HoverEvent.showText(Component.text("点击传送至该建筑")))
                        )
                        .append(Component.text("(/arch tp "+area.id+")")
                                .color(tipColor)
                        )
                        .appendSpace()
                        .append(Component.text("去打分")
                                .decoration(TextDecoration.BOLD, true)
                                .color(TextColor.color(0x6AC93E))
                                .clickEvent(ClickEvent.runCommand("/vote "+area.id+" gui"))
                                .hoverEvent(HoverEvent.showText(Component.text("点击打开投票界面")))
                        )
                        .append(Component.text("(/vote "+area.id+" gui)")
                                .color(tipColor)
                        )
                );
    }
}
