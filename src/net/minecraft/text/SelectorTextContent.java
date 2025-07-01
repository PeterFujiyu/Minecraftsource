/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsedSelector;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextContent;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;

public record SelectorTextContent(ParsedSelector selector, Optional<Text> separator) implements TextContent
{
    public static final MapCodec<SelectorTextContent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)ParsedSelector.CODEC.fieldOf("selector")).forGetter(SelectorTextContent::selector), TextCodecs.CODEC.optionalFieldOf("separator").forGetter(SelectorTextContent::separator)).apply((Applicative<SelectorTextContent, ?>)instance, SelectorTextContent::new));
    public static final TextContent.Type<SelectorTextContent> TYPE = new TextContent.Type<SelectorTextContent>(CODEC, "selector");

    @Override
    public TextContent.Type<?> getType() {
        return TYPE;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null) {
            return Text.empty();
        }
        Optional<MutableText> optional = Texts.parse(source, this.separator, sender, depth);
        return Texts.join(this.selector.comp_3068().getEntities(source), optional, Entity::getDisplayName);
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, this.selector.comp_3067());
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.selector.comp_3067());
    }

    @Override
    public String toString() {
        return "pattern{" + String.valueOf(this.selector) + "}";
    }
}

