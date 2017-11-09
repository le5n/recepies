import org.htmlparser.util.ParserException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;

/**
 * @author Elena_Georgievskaia
 * @since 07-Nov-17.
 */
public class TelegramBot extends TelegramLongPollingBot {
    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(new TelegramBot());
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message.getText().equals("/start")){
            sendMsg(message, "Привет, я тупенький бот, который ищет вегетерианские рецепты по наименованию игредиента. Не надо писать мне больше одного слова, задавать вопросы и т.д. Я с этим не справлюсь. Короче, одно слово, желательно какой-то продукт и я кину ссылочку на рандомный рецепт с этим продуктом");
        }
        System.out.println(message.getContact());
        System.out.println(message.getLocation());
        System.out.println(message.getFrom());
        System.out.println(message.getText());
        String text = message.getText();
        ReceiptSearcher searcher = new ReceiptSearcher();
        try {
            if (message.getFrom().getId()==43036486){
                String messageText = message.getText();
                String reply = messageText.substring(2, messageText.length());
                String xye = "хую" + reply;
                sendMsg(message, xye);
                System.out.println(xye);
            }else {
                sendMsg(message, searcher.getRandomReceipt(text));
            }
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "VegeterianRecipesSearcherBot";
    }

    @Override
    public String getBotToken() {
        return "468709960:AAH1kDqc3VLDwDYACfied6YXVHOnITLL_-w";
    }

    private String sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return sendMessage.getText();
    }
}
