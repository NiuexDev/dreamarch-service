package dev.niuex.dreamarch.TextTemplate;

import dev.niuex.dreamarch.Arch.Area;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class VoteTemplate implements TextTemplate{

    public static final TextColor textColor = TextColor.color(0x67ED43);

    public static Component render(Area area) {
        TextComponent.Builder component = Component.text()
                .append(Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").color(textColor))
                .appendNewline()
                .append(Component.text()
                        .append(Component.text("为 "))
                        .append(Component.text("[")
                        )
                        .append(Component.text(area.id)
                        )
                        .append(Component.text("]")
                        )
                        .append(Component.text(area.getName())
                                .decoration(TextDecoration.BOLD, true)
                        )
                        .append(Component.text(" 打分"))
                        .color(TextColor.color(0xffffff))

                )
                .appendNewline()
                .appendSpace()
                .appendSpace();

        for (int i = 1; i <= 10; i++) {
            Component a = Component.text(i)
                    .clickEvent(ClickEvent.runCommand("/vote "+area.id+" "+i))
                    .color(textColor);


            component.appendSpace()
                    .append(a)
                    .appendSpace();
        }

        component.appendNewline()
                .append(Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").color(textColor));
        return component.build();
    }
}
