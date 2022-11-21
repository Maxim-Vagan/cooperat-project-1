package ru.jd6team7.cooperatproject1.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfiguration {
    @Value("${telegram.bot.token}")
    private String tokenKey;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(tokenKey);
        bot.execute(new DeleteMyCommands());
        return bot;
    }
}
