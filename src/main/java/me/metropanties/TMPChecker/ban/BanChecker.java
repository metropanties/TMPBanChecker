package me.metropanties.TMPChecker.ban;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BanChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BanChecker.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), r ->
            new Thread(r, "Checker Thread"));
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final WebhookClient WEBHOOK_CLIENT = new WebhookClientBuilder(System.getenv("WEBHOOK_URL"))
            .setHttpClient(CLIENT)
            .build();

    private static final String API_URL = "https://api.truckersmp.com/v2/player/{player_id}";

    private final HashMap<Long, Boolean> banCache = new HashMap<>();
    private final List<Long> ids;

    public BanChecker() {
        this.ids = Objects.requireNonNull(loadIDs());
        start();
    }

    private void start() {
        EXECUTOR.scheduleWithFixedDelay(() -> {
            LOGGER.info("Checking bans ...");
            for (long id : ids) {
                var banData = getBanData(id);
                banData.ifPresent(data -> {
                    if (banCache.containsKey(id) && banCache.get(id) == data.getBanned()) {
                        LOGGER.info(String.format("No ban updates found for %s!", data.getName()));
                        return;
                    }

                    LOGGER.info(String.format("Ban update found for %s!", data.getName()));
                    banCache.put(id, data.getBanned());
                    if (banCache.get(id)) {
                        sendAnnouncement(data);
                    }
                });
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void shutdown() {
        LOGGER.info("Shutting down ...");
        EXECUTOR.shutdownNow();
    }

    @Nullable
    private List<Long> loadIDs() {
        try {
            var is = BanChecker.class.getClassLoader().getResourceAsStream("config.json");
            var tree = MAPPER.readTree(is);
            return MAPPER.readValue(tree.get("ids").toPrettyString(), new TypeReference<>() {
            });
        } catch (IOException e) {
            LOGGER.error("Failed loading IDs from config file!", e);
        }
        return null;
    }

    private void sendAnnouncement(@NotNull BanData data) {
        WEBHOOK_CLIENT.send(new WebhookEmbedBuilder()
                .setColor(Color.RED.getRGB())
                .setDescription(String.format("**%s**, has been banned from TMP!", data.getName()))
                .setFooter(new WebhookEmbed.EmbedFooter(String.format("Banned until: %s", data.getBannedUntil().toString()), data.getAvatar()))
                .build());
    }

    public Optional<BanData> getBanData(long playerID) {
        Request request = new Request.Builder()
                .url(API_URL.replace("{player_id}", String.valueOf(playerID)))
                .build();

        try (var response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                var tree = MAPPER.readTree(response.body().string());
                return Optional.of(MAPPER.readValue(tree.get("response").toPrettyString(), BanData.class));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }

}
