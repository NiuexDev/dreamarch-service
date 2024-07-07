package dev.niuex.dreamarch.TextTemplate;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class PagingTemplate implements TextTemplate{

    public static final @NotNull TextColor textColor = TextColor.color(0x9E9E9E);

    public static Component render(int page, int pageNumber) {
        TextComponent.Builder component = Component.text()
                .appendNewline();
        component.append(Component.text("   "));
        component.append(Component.text("«")
                        .hoverEvent(HoverEvent.showText(Component.text("第一页")))
                        .clickEvent(ClickEvent.runCommand("/arch list page 1"))
                                .color(textColor)
                                .decoration(TextDecoration.BOLD, true)
                )
                .appendSpace()
                .append(Component.text("‹")
                        .hoverEvent(HoverEvent.showText(Component.text("上一页")))
                        .clickEvent(ClickEvent.runCommand("/arch list page "+Math.max(pageNumber-1, 1)))
                        .color(textColor)
                        .decoration(TextDecoration.BOLD, true)
                )
                .appendSpace()
                .appendSpace();
        for (int i = 1; i <= page; i++) {
            Component a = Component.text(i)
                    .hoverEvent(HoverEvent.showText(Component.text("切换到第"+i+"页")))
                    .clickEvent(ClickEvent.runCommand("/arch list page "+i))
                    .color(textColor)
                    .decoration(TextDecoration.BOLD, true);
            if (i == pageNumber) {
                a = a.decoration(TextDecoration.UNDERLINED, true);
            }

            component.appendSpace()
                    .appendSpace()
                    .append(a)
                    .appendSpace()
                    .appendSpace();
        }
        component.appendSpace()
                .appendSpace()
                .append(Component.text("›")
                        .hoverEvent(HoverEvent.showText(Component.text("下一页")))
                        .clickEvent(ClickEvent.runCommand("/arch list page "+Math.min(pageNumber+1, page)))
                        .color(textColor)
                        .decoration(TextDecoration.BOLD, true)
                )
                .appendSpace()
                .append(Component.text("»")
                        .hoverEvent(HoverEvent.showText(Component.text("最后一页")))
                        .clickEvent(ClickEvent.runCommand("/arch list page "+page))
                        .color(textColor)
                        .decoration(TextDecoration.BOLD, true)
                );

        component.appendNewline();
        return component.build();
    }
}
