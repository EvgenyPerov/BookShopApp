package com.example.MyBookShopApp.security.telegram.config;

import com.example.MyBookShopApp.security.telegram.bot.TelegramConfirmationBot;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.logging.Logger;

@Configuration
public class TelegramConfirmationConfig {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramConfirmationBot bot) {
        TelegramBotsApi api = null;
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.warning("Ошибка в работе телеграм бота, либо тайм аут: " + e);
        }

        return api;
    }

    @Bean
    public OkHttpClient getOkHttpClient(){
        return new OkHttpClient();
    }

}
