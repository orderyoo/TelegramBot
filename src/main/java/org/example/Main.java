package org.example;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyBot("5531030549:AAF3EhL_FBwyHfsNd5yTzPLJe_FFbYdAIPU"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class MyBot extends TelegramLongPollingBot {
    private String botToken;
    public MyBot(String botToken) {
        this.botToken = botToken;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            String currentChatId = String.valueOf(update.getMessage().getChatId());
            long myChatIdWithBot = 986035532;

            if(message.getChatId().equals(myChatIdWithBot)){
                sendMessage.setText(message.getText());
                sendMessage.setChatId("-1001920250094");
                customExecuteAsync(sendMessage);
            }

            if(message.hasContact() && message.getChat().isGroupChat()){
                DataBaseHendler dataBaseHendler = new DataBaseHendler();
                boolean a = dataBaseHendler.addUser(String.valueOf(message.getContact().getUserId()), message.getContact().getFirstName(), message.getContact().getPhoneNumber());
                if(a){
                    sendMessage.setText("Пользователь добавлен");
                } else {
                    sendMessage.setText("Такой пользователь уже есть");
                }
                sendMessage.setChatId(currentChatId);
                customExecuteAsync(sendMessage);
            }

            if (message.hasText() && message.getText().equals("/userInfo")){

                DataBaseHendler db = new DataBaseHendler();

                ResultSet resultSet = db.getUserInfo(String.valueOf(message.getReplyToMessage().getFrom().getId()));

                try {
                    resultSet.next();
                    sendMessage.setText("Имя: " + resultSet.getNString(Const.USERS_NIKNAME) + "\n" +
                                        "Телефон: " + resultSet.getNString(Const.USERS_NUMBERPHONE));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                sendMessage.setChatId(currentChatId);
                customExecuteAsync(sendMessage);
            }

            if (message.hasText() && message.getText().equals("/getChatId") && String.valueOf(message.getFrom().getId()).equals("986035532")){
                sendMessage.setChatId(currentChatId);
                sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
                sendMessage.setText(currentChatId);
                customExecuteAsync(sendMessage);
            }

            if (message.hasText() && message.getText().equalsIgnoreCase("да")) {
                sendMessage.setChatId(currentChatId);
                sendMessage.setText(Math.random() < 0.5 ? "пизда" : "караганда");
                sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
                customExecuteAsync(sendMessage);
            }
            if (message.hasText() && message.getText().equalsIgnoreCase("Какой сегодня праздник")){
                System.out.println("aaa");
                sendMessage.setChatId(currentChatId);
                try {
                    sendMessage.setText(Command.hollyToDay());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                customExecuteAsync(sendMessage);
            }

            if (message.hasText() && message.getText().matches("(.*)(Р|р)андомное число от(.*)")){
                sendMessage.setChatId(currentChatId);
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(message.getText());
                int firstNumber = 0;
                int secondNumber = 0;
                if (matcher.find()) {
                    firstNumber = Integer.parseInt(matcher.group());
                }
                if (matcher.find()) {
                    secondNumber = Integer.parseInt(matcher.group());
                }
                if (firstNumber > secondNumber){
                    sendMessage.setText("Ашибка, первое число больше второго");
                }
                else {
                    sendMessage.setText(Integer.toString((int)(Math.random()*(secondNumber - firstNumber) + firstNumber)));
                }
                customExecuteAsync(sendMessage);
            }
        }
    }
    @Override
    public String getBotUsername() {
        return "MyBot";
    }
    @Override
    public String getBotToken() {
        return botToken;
    }
    public void customExecuteAsync(SendMessage sendMessage){
        try {
            executeAsync(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}