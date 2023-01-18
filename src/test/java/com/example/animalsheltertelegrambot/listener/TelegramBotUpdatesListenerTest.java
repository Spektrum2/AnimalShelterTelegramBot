package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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


    @ParameterizedTest
    @MethodSource("provideParamsForMainMenu")
    public void mainMenuTest(String text) throws URISyntaxException, IOException {
        String expected = "Добрый день. Наш бот помогает найти новый дом брошенным животным. Пожалуйста, выберете интересующий Вас приют из меню ниже:";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, text);
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
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

        assertThat(actualUser.getChatId()).isEqualTo(123L);
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

    @ParameterizedTest
    @MethodSource("provideParamsForButtonsMainMenu")
    public void buttonsMainMenuTest(int numbers, String text) throws URISyntaxException, IOException {
        String expected = "Добрый день. Рады приветствовать Вас в нашем приюте.";
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, text);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
        assertThat(out.getAllMapForTests().get(update.message().chat().id())).isEqualTo(numbers);

    }

    @Test
    public void infoMenuTest() throws URISyntaxException, IOException {
        String text = "Пожалуйста, выберете интересующую Вас информацию из списка ниже.";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Узнать информацию о приюте");

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(text);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForStoryInfoMenu")
    public void buttonsStoryInfoMenuTest(int numbers, String text) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Рассказать о приюте");

        out.addMapForTests(update.message().chat().id(), numbers);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(text);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForButtonsAddress")
    public void buttonsAddressInfoMenuTest(int numbers, String text) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Расписание работы приюта и адрес, схема проезда");

        out.addMapForTests(update.message().chat().id(), numbers);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(text);
    }

    @Test
    public void buttonSafeInfoMenuTest() throws URISyntaxException, IOException {
        String expected = "— Обувь должна быть на подошве, исключающей непроизвольное скольжение;" +
                "— верхняя одежда должна соответствовать погоде, исключать промокание, а также должна быть облегающей и исключать возможность непроизвольных зацепов за ограждения, строения и иные конструкции." +
                "Запрещается носить в карманах одежды колющие, режущие и стеклянные предметы." +
                "Возможно использование дополнительных средств индивидуальной защиты. Средства индивидуальной защиты должны соответствовать размеру, применяться в исправном, чистом состоянии по назначению и храниться в специально отведенных и оборудованных местах с соблюдением санитарных правил." +
                "При общении с животными работники и посетители приюта обязаны соблюдать меры персональной и общественной безопасности." +
                "При входе в какое-либо помещение или вольер или выходе из него необходимо обязательно закрыть дверь.";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Рекомендации о технике безопасности на территории приюта");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void buttonSecurityInfoMenuTest() throws URISyntaxException, IOException {
        String expected = "Для получение пропуска на территорию приюта, пожалуйста, езжайте к центральному входу, по приезду наберите номер 5959";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Контактные данные охраны для оформления пропуска на машину");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void buttonDataTest() throws URISyntaxException, IOException {
        String expected = "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
                "Например: +7-909-945-4367 Андрей";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Записать контактные данные для связи");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @Test
    public void buttonVolunteerTest() throws URISyntaxException, IOException {
        String expected = "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Позвать волонтера");
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("provideParamsInfoAboutTheAnimalMenu")
    public void infoAboutTheAnimalMenuTest(int numbers) throws URISyntaxException, IOException {
        String text = "Пожалуйста, выберете интересующую Вас информацию из списка ниже.";

        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Как взять животное из приюта");

        out.addMapForTests(update.message().chat().id(), numbers);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(text);
    }

    @ParameterizedTest
    @MethodSource("provideParamsButtonsInfoAboutTheAnimalMenu")
    public void buttonsInfoAboutTheAnimalMenuTest(String question, String answer) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, question);
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(answer);
    }

    @Test
    public void buttonBackTest() throws URISyntaxException, IOException {
        String expected = "Добрый день. Рады приветствовать Вас в нашем приюте.";
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Назад");

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(expected);
    }



    private Update geUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }

    public static Stream<Arguments> provideParamsForMainMenu() {
        return Stream.of(
                Arguments.of("/start"),
                Arguments.of("Выход")
        );
    }

    public static Stream<Arguments> provideParamsForButtonsMainMenu() {
        return Stream.of(
                Arguments.of(1, "Приют для собак"),
                Arguments.of(2, "Приют для кошек")
        );
    }

    public static Stream<Arguments> provideParamsForStoryInfoMenu() {
        return Stream.of(
                Arguments.of(1, "В приюте животных из Астаны находится более 1700 бездомных собак, брошенных, потерянных и оказавшихся на улице при разных обстоятельствах. " +
                        "Дворняги, метисы и породистые. У каждой собаки своя история и свой характер. Многие из них в какой-то момент оказались не нужной игрушкой - их предал хозяин. " +
                        "Наш бот создан для того чтобы собаки из приюта обрели свой  дом и получили второй шанс на жизнь. " +
                        "Так же мы привлекаем новых волонтеров для помощи приютским собакам."),
                Arguments.of(2, "В приюте животных из Астаны находится более 1700 бездомных котов, брошенных, потерянных и оказавшихся на улице при разных обстоятельствах. " +
                        "Сиамские, рыжие и лысые. У каждого кота своя история и свой характер. Многие из них в какой-то момент оказались не нужной игрушкой - их предал хозяин. " +
                        "Наш бот создан для того чтобы коты из приюта обрели свой  дом и получили второй шанс на жизнь. " +
                        "Так же мы привлекаем новых волонтеров для помощи приютским котам и кошкам.")
        );
    }

    public static Stream<Arguments> provideParamsForButtonsAddress() {
        return Stream.of(
                Arguments.of(1, "Приют животных из Астаны открыт для посещения 6 дней в неделю с 11:00 до 17:00 ч." +
                        " Санитарные дни 1-е и 15-е число месяца (на эти дни приют закрыт для посещения)." +
                        "Адрес: Третья улица строителей, дом 15"),
                Arguments.of(2, "Приют животных из Астаны открыт для посещения 6 дней в неделю с 11:00 до 17:00 ч." +
                        " Санитарные дни 1-е и 15-е число месяца (на эти дни приют закрыт для посещения)." +
                        "Адрес: Шестой замоскворецкий переулок")
        );
    }

    public static Stream<Arguments> provideParamsInfoAboutTheAnimalMenu() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2)
        );
    }

    public static Stream<Arguments> provideParamsButtonsInfoAboutTheAnimalMenu() {
        return Stream.of(
                Arguments.of("Правила знакомства с животным", "Веди себя хорошо, не балуйся"),
                Arguments.of("Список документов, необходимых для того, чтобы взять животное из приюта", "Паспорт"),
                Arguments.of("Рекомендации по транспортировке животного", "Клетка для перевозки"),
                Arguments.of("Рекомендации по обустройству дома щенка", "Мыть, кормить, любить, не бить"),
                Arguments.of("Рекомендации по обустройству дома котенка", "Мыть, кормить, любить, не бить"),
                Arguments.of("Рекомендации по обустройству дома взрослой собаки", "Мыть, кормить, любить, не бить, выводить гулять"),
                Arguments.of("Рекомендации по обустройству дома взрослого кота/кошки", "Мыть, кормить, любить, не бить, выводить гулять"),
                Arguments.of("Советы кинолога по первичному общению с собакой", "Не бить палкой"),
                Arguments.of("Рекомендации по проверенным кинологам для дальнейшего обращения к собакой", "Тетя Зина, Дядя Толя"),
                Arguments.of("Список причин, почему могут отказать в просьбе забрать собаку из приюта", "Плохой запах"),
                Arguments.of("Список причин, почему могут отказать в просьбе забрать кота/кошку из приюта", "Плохой запах"),
                Arguments.of("Рекомендаций по обустройству дома собаки с ограниченными возможностями", "Мыть, кормить, любить, не бить, выводить гулять"),
                Arguments.of("Рекомендаций по обустройству дома кота/кошки с ограниченными возможностями", "Мыть, кормить, любить, не бить, выводить гулять")
        );
    }
}