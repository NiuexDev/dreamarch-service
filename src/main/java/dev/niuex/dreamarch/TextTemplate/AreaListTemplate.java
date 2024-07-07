package dev.niuex.dreamarch.TextTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;

public class AreaListTemplate implements TextTemplate {

    public static final TextColor tipColor = TextColor.color(0x9E9E9E);
    public static Component render(Component[] areaList, Component paging) {
        TextComponent.Builder component = Component.text()
                .append(Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").color(tipColor));
        Arrays.stream(areaList).forEach(area -> {
            component.append(Component.newline()).append(area).append(Component.newline());
        });
        component.append(Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").color(tipColor));
        component.append(paging);
        return component.build();
    }
}
