/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsedSelector;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.jetbrains.annotations.Nullable;

public record ScoreTextContent(Either<ParsedSelector, String> name, String objective) implements TextContent
{
    public static final MapCodec<ScoreTextContent> INNER_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.either(ParsedSelector.CODEC, Codec.STRING).fieldOf("name")).forGetter(ScoreTextContent::name), ((MapCodec)Codec.STRING.fieldOf("objective")).forGetter(ScoreTextContent::objective)).apply((Applicative<ScoreTextContent, ?>)instance, ScoreTextContent::new));
    public static final MapCodec<ScoreTextContent> CODEC = INNER_CODEC.fieldOf("score");
    public static final TextContent.Type<ScoreTextContent> TYPE = new TextContent.Type<ScoreTextContent>(CODEC, "score");

    @Override
    public TextContent.Type<?> getType() {
        return TYPE;
    }

    private ScoreHolder getScoreHolder(ServerCommandSource source) throws CommandSyntaxException {
        Optional<ParsedSelector> optional = this.name.left();
        if (optional.isPresent()) {
            List<? extends Entity> list = optional.get().comp_3068().getEntities(source);
            if (!list.isEmpty()) {
                if (list.size() != 1) {
                    throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
                }
                return list.getFirst();
            }
            return ScoreHolder.fromName(optional.get().comp_3067());
        }
        return ScoreHolder.fromName(this.name.right().orElseThrow());
    }

    private MutableText getScore(ScoreHolder scoreHolder, ServerCommandSource source) {
        ReadableScoreboardScore lv3;
        ServerScoreboard lv;
        ScoreboardObjective lv2;
        MinecraftServer minecraftServer = source.getServer();
        if (minecraftServer != null && (lv2 = (lv = minecraftServer.getScoreboard()).getNullableObjective(this.objective)) != null && (lv3 = lv.getScore(scoreHolder, lv2)) != null) {
            return lv3.getFormattedScore(lv2.getNumberFormatOr(StyledNumberFormat.EMPTY));
        }
        return Text.empty();
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null) {
            return Text.empty();
        }
        ScoreHolder lv = this.getScoreHolder(source);
        ScoreHolder lv2 = sender != null && lv.equals(ScoreHolder.WILDCARD) ? sender : lv;
        return this.getScore(lv2, source);
    }

    @Override
    public String toString() {
        return "score{name='" + String.valueOf(this.name) + "', objective='" + this.objective + "'}";
    }
}

