package TelegramQuiz.controller;

import TelegramQuiz.Enum.EnumSubject;
import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.MessageData;
import TelegramQuiz.entity.Subject;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.qrcode.GenerateQRCode;
import TelegramQuiz.service.CustomerService;
import TelegramQuiz.service.SubjectService;
import TelegramQuiz.entity.Question;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.util.InlineButtonConstants;
import TelegramQuiz.util.KeyboardButtonConstants;
import TelegramQuiz.util.KeyboardButtonUtil;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;


public class AdminController {

   static EnumSubject enumSubject;
   static Subject newName;
   static Subject deleteSubject;



    static boolean isDeleted;
    static boolean isAdded;
    public static void handleMessage(User user, Message message) {

     String s= String.valueOf(message.getChatId());
        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        }
           else if (message.hasContact()) {
                Contact contact = message.getContact();
               handleContact(user,message,contact);
            }



    }
    private static void handleContact(User user, Message message, Contact contact) {



        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

            sendMessage.setText("Menu: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

    }


    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (text.equals("/start")) {
            sendMessage.setText("Hello "  +user.getFirstName());
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        }
        else if(text.equalsIgnoreCase(KeyboardButtonConstants.WORK_WITH_SUBJECTS)){
            sendMessage.setText("Choose: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getSubjectMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

           DeleteMessage deleteMessage=new DeleteMessage(chatId,message.getMessageId());
           ComponentContainer.MY_BOT.sendMsg(deleteMessage);
        }else if(enumSubject==EnumSubject.CREATE){
            Subject subject=new Subject();
            Subject subject1 = SubjectService.checkSubject(text);

            DeleteMessage deleteMessage= new DeleteMessage();
            deleteMessage.setChatId(chatId);
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            if(subject1==null){
                sendMessage.setText(text+" added successfully");
                subject.setTitle(text);

                SubjectService.addCallBackData(subject,text);

                WorkWithFiles.writeSubjectsList();
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
            }else {
                sendMessage.setText(text+" already added. Please enter other subject name");
            }


            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            DeleteMessage deleteMessage1=new DeleteMessage(chatId,message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
        }else if(enumSubject==EnumSubject.UPDATE){
            DeleteMessage deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId());
            deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId()-1);
            deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId()-2);
                       ComponentContainer.MY_BOT.sendMsg(deleteMessage3);
            String title = newName.getTitle();
            sendMessage.setText("A new name  "+ text+"  of "+ title);

            SubjectService.changeSubject(title,text);
            enumSubject=null;
            WorkWithFiles.writeSubjectsList();
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
            sendMessage.setText("Hello "  +user.getFirstName());
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.GET_USERS_LIST_EXCEL)) {
            if(Database.customerList.size() == 0){
                sendMessage.setText("No registered customers");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(WorkWithFiles.generateCustomerExcelFile(Database.customerList)));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
        } else if (text.equals(KeyboardButtonConstants.GET_ALL_HISTORY_PDF)) {
            if(Database.historyList.size() == 0){
                sendMessage.setText("History is empty");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                return;
            }
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(WorkWithFiles.generateAllHistoryPdfFile(Database.historyList)));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
            DeleteMessage deleteMessag2=new DeleteMessage(chatId,message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessag2);
        } else if (text.equals(KeyboardButtonConstants.WORK_WITH_ADMIN)) {
            sendMessage.setText("Chose option below: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }else if (text.equals(KeyboardButtonConstants.BACK_TO_MENU)) {
            sendMessage.setText("Main menu: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }else if(text.equals(KeyboardButtonConstants.SHOW_ADMIN_LIST)){
            for (String adminChatId : ComponentContainer.ADMIN_CHAT_IDS) {
                for (Customer customer : Database.customerList) {
                    if(customer.getChatId().equals(adminChatId)){
                        sendMessage.setText("First name: " + customer.getFirstName()+"\n"
                                + "Phone number: " + customer.getPhoneNumber());
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    }
                }
            }
        }else if(text.equals(KeyboardButtonConstants.DELETE_ADMIN)){
            isDeleted=true;
        sendMessage.setText("Enter number "+ "\nExample: +998907777777");
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }  else if (text.equals(KeyboardButtonConstants.ADD_ADMIN)) {
            isAdded = true;
            sendMessage.setText("Enter number " + "\nExample: +998907777777");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }else if (isDeleted) {
            String numSub = text.substring(1);
            Customer customer1 =Database.customerList.stream().filter(customer -> customer.getPhoneNumber().contains(numSub)).findFirst().orElse(null);
            if(!text.matches("(\\+)?998\\d{9}")){
                sendMessage.setText("Wrong number! ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if(customer1==null){
                sendMessage.setText("User not found!");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                isDeleted=false;
            }
            else {
                isDeleted=false;
                customer1.setAdmin(false);
                ComponentContainer.ADMIN_CHAT_IDS.remove(customer1.getChatId());
                WorkWithFiles.writeCustomerList();
                sendMessage.setText("Successfully deleted");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        } else if (isAdded) {
            Customer customer = Database.customerList.stream().filter(customer1 -> customer1.getPhoneNumber().contains(text)).findFirst().orElse(null);
            if(!text.matches("(\\+)?998\\d{9}")){
                sendMessage.setText("Wrong number! ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (ComponentContainer.ADMIN_CHAT_IDS.contains(customer.getChatId())) {
                sendMessage.setText(customer.getPhoneNumber() + " is already on the list");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }else if(customer == null){
                sendMessage.setText("User not found: ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                isAdded = false;
            }
            else {
                isAdded = false;
                customer.setAdmin(true);
                ComponentContainer.ADMIN_CHAT_IDS.add(customer.getChatId());
                sendMessage.setText("Successfully added ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                WorkWithFiles.writeCustomerList();
                WorkWithFiles.readCustomerList();
            }

        }else {
            if (ComponentContainer.adminAnswerMap.containsKey(chatId)){
                MessageData messageData = ComponentContainer.adminAnswerMap.get(chatId);

                Integer customerMessageId = messageData.getMessageId();

                String customerChatId = messageData.getCustomerChatId();
                Integer messageId = messageData.getMessage().getMessageId();
                String messageText = messageData.getMessage().getText();

                sendMessage.setChatId(customerChatId);
                sendMessage.setText("Admin ning javobi: "+text);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);

                editMessageText.setText(messageText+"\n\n xabariga javob: \n\n "+text);
                editMessageText.setMessageId(messageId);
                ComponentContainer.MY_BOT.sendMsg(editMessageText);
                ComponentContainer.adminAnswerMap.remove(chatId);

                List<Message> mustMessageList = null;
                Message mustKey = null;

                for (Message keyMessage : ComponentContainer.messagesMap.keySet()) {
                    if(keyMessage.getMessageId().equals(customerMessageId)){
                        mustKey = keyMessage;
                        mustMessageList = ComponentContainer.messagesMap.get(keyMessage);
                        break;
                    }
                }

                for (Message message1 : mustMessageList) {
                    String adminChatId = message1.getChatId().toString();
                    if (!adminChatId.equals(message.getChatId().toString())) {
                        EditMessageText editMessageText1 = new EditMessageText();
                        editMessageText1.setChatId(adminChatId);
                        editMessageText1.setText("Admin " + message.getFrom().getFirstName() + " answered for this question");
                        editMessageText1.setMessageId(message1.getMessageId());
                        ComponentContainer.MY_BOT.sendMsg(editMessageText1);
                    }
                }

                ComponentContainer.messagesMap.remove(mustKey);
            }
        }
    }




    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if(data.startsWith(InlineButtonConstants.REPLY_CALL_BACK)){
            String customerChatId = data.split("/")[1];
            Integer messageId = Integer.parseInt(data.split("/")[2]);

            ComponentContainer.adminAnswerMap.put(chatId, new MessageData(message, customerChatId, messageId));

            sendMessage.setText("Javobingizni kiriting: ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        }
//        String chatId = String.valueOf(message.getChatId());
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);

        Subject subject = SubjectService.handeCallback(data);
        DeleteMessage deleteMessage11 = new DeleteMessage(chatId.toString(), message.getMessageId());
        deleteMessage11 = new DeleteMessage(chatId.toString(), message.getMessageId()-1);
        deleteMessage11 = new DeleteMessage(chatId.toString(), message.getMessageId()-2);
        if(subject!=null){
            DeleteMessage deleteMessage9 = new DeleteMessage(chatId.toString(), message.getMessageId());

            ComponentContainer.MY_BOT.sendMsg(deleteMessage9);
            if(enumSubject==EnumSubject.DELETE){
                enumSubject=null;
                sendMessage.setText("Really do you want to delete "+subject.getTitle());
                deleteSubject=subject;
                sendMessage.setReplyMarkup(KeyboardButtonUtil.deleteSubjectButton());
              ComponentContainer.MY_BOT.sendMsg(sendMessage);
              return;
            }
            DeleteMessage deleteMessage10 = new DeleteMessage(chatId.toString(), message.getMessageId());
            deleteMessage10 = new DeleteMessage(chatId.toString(), message.getMessageId()-1);
            deleteMessage10 = new DeleteMessage(chatId.toString(), message.getMessageId()-2);
                sendMessage.setText(" Enter new name for "+subject.getTitle());
                enumSubject= EnumSubject.UPDATE;
                newName=subject;
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            DeleteMessage deleteMessage4 = new DeleteMessage(chatId.toString(), message.getMessageId());
            deleteMessage4 = new DeleteMessage(chatId.toString(), message.getMessageId()-1);
            deleteMessage4 = new DeleteMessage(chatId.toString(), message.getMessageId()-2);

            ComponentContainer.MY_BOT.sendMsg(deleteMessage4);
             }else {
             DeleteMessage deleteMessage1=new DeleteMessage(chatId,message.getMessageId());
             ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                  if (data.equalsIgnoreCase(KeyboardButtonConstants.CREATE_SUBJECT)) {
                      DeleteMessage deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                      deleteMessage1 = new DeleteMessage(chatId.toString(), message.getMessageId() - 1);
                      deleteMessage1 = new DeleteMessage(chatId.toString(), message.getMessageId() - 2);


                      ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                      sendMessage.setText("Send title of new subject ");
                      enumSubject = EnumSubject.CREATE;

                      ComponentContainer.MY_BOT.sendMsg(sendMessage);
                      DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage);

                  } else if (data.equalsIgnoreCase(KeyboardButtonConstants.BACK)) {
                      DeleteMessage deleteMessage4 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage4);
                      sendMessage.setText("Choose: ");
                      sendMessage.setReplyMarkup(KeyboardButtonUtil.getSubjectMenu());
                      ComponentContainer.MY_BOT.sendMsg(sendMessage);

                  } else if (data.equalsIgnoreCase(KeyboardButtonConstants.READ_SUBJECT)) {
                      DeleteMessage deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage3);
                      if (Database.subjectsList.size() == 0) {
                          sendMessage.setText("You do not have any subject for reading");
                      } else {
                          sendMessage.setText(allSubjects());

                      }
                      sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
                      ComponentContainer.MY_BOT.sendMsg(sendMessage);

                      DeleteMessage deleteMessage5 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage5);

                  } else if (data.equalsIgnoreCase(KeyboardButtonConstants.UPDATE_SUBJECT)) {
                      DeleteMessage deleteMessage6 = new DeleteMessage(chatId.toString(), message.getMessageId());
                      deleteMessage6=new DeleteMessage(chatId.toString(), message.getMessageId()-1);
                      deleteMessage6=new DeleteMessage(chatId.toString(), message.getMessageId()-2);

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage6);

                      if (Database.subjectsList.size() == 0) {
                          sendMessage.setText("You do not have any subject for updating");
                      }else {
                          sendMessage.setText("which subject you want to update");


                      }
                      enumSubject=EnumSubject.UPDATE;
                      sendMessage.setReplyMarkup(KeyboardButtonUtil.getInlineButtonBySubject());

                      ComponentContainer.MY_BOT.sendMsg(sendMessage);
                      DeleteMessage deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId());
                      deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId()-1);
                       deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId()-2);

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage3);

                  }else if(data.equalsIgnoreCase(KeyboardButtonConstants.DELETE_SUBJECT)){
                      DeleteMessage deleteMessage7 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage7);
                      enumSubject=EnumSubject.DELETE;
                      if (Database.subjectsList.size() == 0) {
                          sendMessage.setText("You do not have any subject for deleting");
                          sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
                      }else {
                          sendMessage.setText("Which subjects do you want to delete ");
                          sendMessage.setReplyMarkup(KeyboardButtonUtil.getInlineButtonBySubject());
                      }

                      ComponentContainer.MY_BOT.sendMsg(sendMessage);


                  }else if(data.equalsIgnoreCase(InlineButtonConstants.YES)){
                      DeleteMessage deleteMessage8 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage8);
                      Database.subjectsList.remove(deleteSubject);
                      WorkWithFiles.writeSubjectsList();
                      sendMessage.setText(deleteSubject.getTitle()+" deleted ");
                       deleteSubject=null;
                       sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
                      ComponentContainer.MY_BOT.sendMsg(sendMessage);

                  }else if(data.equalsIgnoreCase(InlineButtonConstants.NO)){
                      DeleteMessage deleteMessage9 = new DeleteMessage(chatId.toString(), message.getMessageId());

                      ComponentContainer.MY_BOT.sendMsg(deleteMessage9);
                      sendMessage.setText("Choose: ");
                      sendMessage.setReplyMarkup(KeyboardButtonUtil.getSubjectMenu());
                      ComponentContainer.MY_BOT.sendMsg(sendMessage);
                  }
              }


    }

    public  static String allSubjects(){
        StringBuilder allSubject=new StringBuilder("");
        String s="";
        for (int i = 0; i < Database.subjectsList.size(); i++) {
            int index=i+1;
            String title = Database.subjectsList.get(i).getTitle();
            s=s+index+" -> "+title;
            allSubject.append(s+"\n");
            s="";
        }
        String AllSubject= String.valueOf(allSubject);
        return AllSubject;
    }
}

