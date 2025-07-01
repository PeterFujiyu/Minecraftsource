/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.server.filter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import net.minecraft.network.message.FilterMask;
import net.minecraft.server.filter.AbstractTextFilterer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class V0TextFilterer
extends AbstractTextFilterer {
    private static final String CHAT_ENDPOINT = "v1/chat";
    final URL joinEndpoint;
    final ProfileEncoder joinEncoder;
    final URL leaveEndpoint;
    final ProfileEncoder leaveEncoder;
    private final String apiKey;

    private V0TextFilterer(URL chatEndpoint, AbstractTextFilterer.MessageEncoder messageEncoder, URL joinEndpoint, ProfileEncoder joinEncoder, URL leaveEndpoint, ProfileEncoder leaveEncoder, String apiKey, AbstractTextFilterer.HashIgnorer ignorer, ExecutorService threadPool) {
        super(chatEndpoint, messageEncoder, ignorer, threadPool);
        this.joinEndpoint = joinEndpoint;
        this.joinEncoder = joinEncoder;
        this.leaveEndpoint = leaveEndpoint;
        this.leaveEncoder = leaveEncoder;
        this.apiKey = apiKey;
    }

    @Nullable
    public static AbstractTextFilterer load(String config) {
        try {
            AbstractTextFilterer.MessageEncoder lv2;
            JsonObject jsonObject = JsonHelper.deserialize(config);
            URI uRI = new URI(JsonHelper.getString(jsonObject, "apiServer"));
            String string2 = JsonHelper.getString(jsonObject, "apiKey");
            if (string2.isEmpty()) {
                throw new IllegalArgumentException("Missing API key");
            }
            int i = JsonHelper.getInt(jsonObject, "ruleId", 1);
            String string3 = JsonHelper.getString(jsonObject, "serverId", "");
            String string4 = JsonHelper.getString(jsonObject, "roomId", "Java:Chat");
            int j = JsonHelper.getInt(jsonObject, "hashesToDrop", -1);
            int k = JsonHelper.getInt(jsonObject, "maxConcurrentRequests", 7);
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "endpoints", null);
            String string5 = V0TextFilterer.getEndpointPath(jsonObject2, "chat", CHAT_ENDPOINT);
            boolean bl = string5.equals(CHAT_ENDPOINT);
            URL uRL = uRI.resolve("/" + string5).toURL();
            URL uRL2 = V0TextFilterer.resolveEndpoint(uRI, jsonObject2, "join", "v1/join");
            URL uRL3 = V0TextFilterer.resolveEndpoint(uRI, jsonObject2, "leave", "v1/leave");
            ProfileEncoder lv = profile -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("server", string3);
                jsonObject.addProperty("room", string4);
                jsonObject.addProperty("user_id", profile.getId().toString());
                jsonObject.addProperty("user_display_name", profile.getName());
                return jsonObject;
            };
            if (bl) {
                lv2 = (profile, message) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("rule", i);
                    jsonObject.addProperty("server", string3);
                    jsonObject.addProperty("room", string4);
                    jsonObject.addProperty("player", profile.getId().toString());
                    jsonObject.addProperty("player_display_name", profile.getName());
                    jsonObject.addProperty("text", message);
                    jsonObject.addProperty("language", "*");
                    return jsonObject;
                };
            } else {
                String string6 = String.valueOf(i);
                lv2 = (profile, message) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("rule_id", string6);
                    jsonObject.addProperty("category", string3);
                    jsonObject.addProperty("subcategory", string4);
                    jsonObject.addProperty("user_id", profile.getId().toString());
                    jsonObject.addProperty("user_display_name", profile.getName());
                    jsonObject.addProperty("text", message);
                    jsonObject.addProperty("language", "*");
                    return jsonObject;
                };
            }
            AbstractTextFilterer.HashIgnorer lv3 = AbstractTextFilterer.HashIgnorer.dropHashes(j);
            ExecutorService executorService = V0TextFilterer.newThreadPool(k);
            String string7 = Base64.getEncoder().encodeToString(string2.getBytes(StandardCharsets.US_ASCII));
            return new V0TextFilterer(uRL, lv2, uRL2, lv, uRL3, lv, string7, lv3, executorService);
        } catch (Exception exception) {
            LOGGER.warn("Failed to parse chat filter config {}", (Object)config, (Object)exception);
            return null;
        }
    }

    @Override
    public TextStream createFilterer(GameProfile profile) {
        return new AbstractTextFilterer.StreamImpl(profile){

            @Override
            public void onConnect() {
                V0TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, V0TextFilterer.this.joinEndpoint, V0TextFilterer.this.joinEncoder, this.executor);
            }

            @Override
            public void onDisconnect() {
                V0TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, V0TextFilterer.this.leaveEndpoint, V0TextFilterer.this.leaveEncoder, this.executor);
            }
        };
    }

    void sendJoinOrLeaveRequest(GameProfile gameProfile, URL endpoint, ProfileEncoder profileEncoder, Executor executor) {
        executor.execute(() -> {
            JsonObject jsonObject = profileEncoder.encode(gameProfile);
            try {
                this.sendRequest(jsonObject, endpoint);
            } catch (Exception exception) {
                LOGGER.warn("Failed to send join/leave packet to {} for player {}", endpoint, gameProfile, exception);
            }
        });
    }

    private void sendRequest(JsonObject payload, URL endpoint) throws IOException {
        HttpURLConnection httpURLConnection = this.openConnection(payload, endpoint);
        try (InputStream inputStream = httpURLConnection.getInputStream();){
            this.discardRestOfInput(inputStream);
        }
    }

    @Override
    protected void addAuthentication(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", "Basic " + this.apiKey);
    }

    @Override
    protected FilteredMessage filter(String raw, AbstractTextFilterer.HashIgnorer hashIgnorer, JsonObject response) {
        boolean bl = JsonHelper.getBoolean(response, "response", false);
        if (bl) {
            return FilteredMessage.permitted(raw);
        }
        String string2 = JsonHelper.getString(response, "hashed", null);
        if (string2 == null) {
            return FilteredMessage.censored(raw);
        }
        JsonArray jsonArray = JsonHelper.getArray(response, "hashes");
        FilterMask lv = this.createFilterMask(raw, jsonArray, hashIgnorer);
        return new FilteredMessage(raw, lv);
    }

    @FunctionalInterface
    static interface ProfileEncoder {
        public JsonObject encode(GameProfile var1);
    }
}

