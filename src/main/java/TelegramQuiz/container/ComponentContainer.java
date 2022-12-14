package TelegramQuiz.container;

import TelegramQuiz.Bot.QuizBot;
import TelegramQuiz.enums.QuestionStatus;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

public class ComponentContainer {
    public static QuizBot MY_BOT = null;

    public static String BOT_USERNAME = "SheengoQuizBot";
    public static String BOT_TOKEN = "5518242458:AAHbOY9WfNOHyTstlZSYLVstxlGsqQ07qcQ";
    public static List<String> ADMIN_CHAT_IDS = new ArrayList<>(List.of("5246644409","1490827145","609762012"));
//    public static String ADMIN_CHAT_IDS = "5246644409";


    // adminChatId, QuestionStatus
    public static Map<String, QuestionStatus> statusMap = new HashMap<>();
    public static Map<String, Boolean> customerMap = new HashMap<>();
    public static Map<String, MessageData> adminAnswerMap = new HashMap<>();

    public static Map<Message,List<Message>> messagesMap = new HashMap<>();

}
