package com.example.MyBookShopApp.security.telegram.bot;

import com.example.MyBookShopApp.security.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Logger;

@Component
public class TelegramConfirmationBot extends TelegramLongPollingBot {

    @Autowired
    private SmsService smsService;

    private Logger logger = Logger.getLogger(this.getClass().getName());


    @Value("${telegrambot.userName}")
    private String botUserName;

    private static final String START = "/start";

    public TelegramConfirmationBot(@Value("${telegrambot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }

        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (text.equals(START)){
            sendMessage(chatId, "Код подтверждения Bookshop - "+ smsService.getGenerateRandomSMSCode());
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    public void sendMessage(Long chatId, String text){
        var chatIdString = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdString, text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e){
            logger.warning("Ошибка отправки сообщения в Telegram - " + e);
        }
    }
}
