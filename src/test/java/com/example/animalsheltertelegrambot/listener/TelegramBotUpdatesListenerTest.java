package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {
    @Mock
    private TelegramBot telegramBot;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TelegramBotUpdatesListener out;

    @Test
    public void startTest() throws URISyntaxException, IOException {
        InlineKeyboardMarkup expected = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Узнать информацию о приюте").callbackData("1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Как взять собаку из приюта").callbackData("2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Прислать отчет о питомце").callbackData("3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Позвать волонтера").callbackData("4");
        expected.addRow(button1);
        expected.addRow(button2);
        expected.addRow(button3);
        expected.addRow(button4);

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "/start");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isEqualTo(expected);
    }

    @Test
    public void getUserDataTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "+7-923-456-2345 Петя");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> sendMessageArgumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(sendMessageArgumentCaptor.capture());
        SendMessage actualSendMessage = sendMessageArgumentCaptor.getValue();

        ArgumentCaptor<UserData> userArgumentCaptor = ArgumentCaptor.forClass(UserData.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        UserData actualUser = userArgumentCaptor.getValue();

        assertThat(actualUser.getIdChat()).isEqualTo(123L);
        assertThat(actualUser.getName()).isEqualTo("Петя");
        assertThat(actualUser.getPhoneNumber()).isEqualTo("+7-923-456-2345");

        assertThat(actualSendMessage.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actualSendMessage.getParameters().get("text")).isEqualTo("Контактные данные сохранены!");
    }

    @Test
    public void negativeMessageTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "abcd");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Моя твоя не понимать");
    }

    @Test
    public void callbackButton1Test() throws URISyntaxException, IOException {
        InlineKeyboardMarkup expected = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Рассказать о приюте").callbackData("text1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Расписание работы приюта и адрес, схема проезда").callbackData("text2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Рекомендации о технике безопасности на территории приюта").callbackData("text3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Принять и записать контактные данные для связи").callbackData("BD");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Позвать волонтера").callbackData("5");
        expected.addRow(button1);
        expected.addRow(button2);
        expected.addRow(button3);
        expected.addRow(button4);
        expected.addRow(button5);

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("callback.json")).toURI()));
        Update update = geUpdate(json, "1");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isEqualTo(expected);
    }

    @Test
    public void callbackButtonText1Test() throws URISyntaxException, IOException {
        String expected = "В приюте животных из Астаны находится более 1700 бездомных собак, брошенных, потерянных и оказавшихся на улице при разных обстоятельствах. " +
                "Дворняги, метисы и породистые. У каждой собаки своя история и свой характер. Многие из них в какой-то момент оказались не нужной игрушкой - их предал хозяин. " +
                "Наш бот создан для того чтобы собаки из приюта обрели свой  дом и получили второй шанс на жизнь. " +
                "Так же мы привлекаем новых волонтеров для помощи приютским собакам.";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("callback.json")).toURI()));
        Update update = geUpdate(json, "text1");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void callbackButtonText2Test() throws URISyntaxException, IOException {
        String expected = "Приют животных из Астаны открыт для посещения 6 дней в неделю с 11:00 до 17:00 ч." +
                " Санитарные дни 1-е и 15-е число месяца (на эти дни приют закрыт для посещения).";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("callback.json")).toURI()));
        Update update = geUpdate(json, "text2");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void callbackButtonText3Test() throws URISyntaxException, IOException {
        String expected = "— Обувь должна быть на подошве, исключающей непроизвольное скольжение;" +
                "— верхняя одежда должна соответствовать погоде, исключать промокание, а также должна быть облегающей и исключать возможность непроизвольных зацепов за ограждения, строения и иные конструкции." +
                "Запрещается носить в карманах одежды колющие, режущие и стеклянные предметы." +
                "Возможно использование дополнительных средств индивидуальной защиты. Средства индивидуальной защиты должны соответствовать размеру, применяться в исправном, чистом состоянии по назначению и храниться в специально отведенных и оборудованных местах с соблюдением санитарных правил." +
                "При общении с животными работники и посетители приюта обязаны соблюдать меры персональной и общественной безопасности." +
                "При входе в какое-либо помещение или вольер или выходе из него необходимо обязательно закрыть дверь.";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("callback.json")).toURI()));
        Update update = geUpdate(json, "text3");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void callbackButtonBDTest() throws URISyntaxException, IOException {
        String expected = "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
                "Например: +7-909-945-4367 Андрей";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("callback.json")).toURI()));
        Update update = geUpdate(json, "BD");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void callbackButton5Test() throws URISyntaxException, IOException {
        String expected = "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("callback.json")).toURI()));
        Update update = geUpdate(json, "5");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    private Update geUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }

}