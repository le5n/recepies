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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
                String keyword  = "";
                Pattern wordPattern = Pattern.compile("(\\S+)$");
                Matcher wordMatcher = wordPattern.matcher(messageText);
                if (wordMatcher.find()) {
                    keyword = wordMatcher.group();
                }
                Integer keywordLength = keyword.length();
                String xye;
                if (keywordLength <= 3) {
                    Pattern firstIsVowel = Pattern.compile("^[аеёийоуэюя]");
                    Matcher matcher = firstIsVowel.matcher(keyword);
                    if (matcher.find()) {
                        xye = getXyefication(keyword, 0);
                    } else {
                        xye = getDefaultXyefication(keyword);
                    }
                } else {
                    Pattern includesVowel = Pattern.compile("[аеёийоуэюя]\\B");
                    Matcher matcher = includesVowel.matcher(keyword);
                    if (matcher.find()) {
                        xye = getXyefication(keyword, matcher.start());
                    } else {
                        xye = getDefaultXyefication(keyword);
                    }
                }
                sendMsg(message, xye);
                System.out.println(xye);
            } else {
                sendMsg(message, searcher.getRandomReceipt(text));
            }
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
    }

    private String getDefaultXyefication(String word) {
        return "хуе" + word;
    }

    private String getXyefication(String word, Integer vowelIndex) {
        char vowel = word.charAt(vowelIndex);
        return getPrefix(vowel) + word.substring(vowelIndex + 1);
    }

    private String getPrefix(char vowelLetter) {
        char fixedVowel;
        switch (vowelLetter) {
            case 'a':
                fixedVowel = 'я';
                break;
            case 'э':
            case 'ё':
            case 'й':
            case 'о':
                fixedVowel = 'е';
                break;
            case 'у':
                fixedVowel = 'ю';
                break;
            default:
                fixedVowel = vowelLetter;
                break;
        }

        return "ху" + fixedVowel;
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
