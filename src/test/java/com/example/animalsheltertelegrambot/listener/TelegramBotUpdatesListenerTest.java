package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.*;
import com.example.animalsheltertelegrambot.repository.ParametersRepository;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
import com.example.animalsheltertelegrambot.service.PhotoOfAnimalService;
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
import java.util.*;
import java.util.stream.Stream;

import static com.example.animalsheltertelegrambot.model.Constants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {
    @Mock
    private TelegramBot telegramBot;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParametersRepository parametersRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private PhotoOfAnimalService photoOfAnimalService;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private TelegramBotUpdatesListener out;


    @ParameterizedTest
    @MethodSource("provideParamsForMainMenu")
    public void mainMenuTest(String text) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, text);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING);
    }

    @ParameterizedTest
    @MethodSource("provideNumbers")
    public void getUserDataTest(int numbers) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "+7-923-456-2345 Петя");
        Parameters parameters = new Parameters(update.message().chat().id(), numbers);
        parameters.setAdd(1);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        SendMessage actualSendMessage = argumentCaptor.getValue();

        ArgumentCaptor<UserData> userArgumentCaptor = ArgumentCaptor.forClass(UserData.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        UserData actualUser = userArgumentCaptor.getValue();

        assertThat(actualUser.getChatId()).isEqualTo(123L);
        assertThat(actualUser.getName()).isEqualTo("Петя");
        assertThat(actualUser.getPhoneNumber()).isEqualTo("+7-923-456-2345");

        assertThat(actualSendMessage.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actualSendMessage.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @Test
    public void messageToTheVolunteer() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Вопрос");
        List<Volunteer> volunteers = new ArrayList<>();
        Volunteer volunteer = new Volunteer();
        volunteers.add(volunteer);
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setChat(1);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));
        when(volunteerRepository.findAll()).thenReturn(volunteers);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Волонтеры заняты");

    }

    @Test
    public void negativeMessageToTheVolunteer() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Вопрос");
        List<Volunteer> volunteers = new ArrayList<>();
        Volunteer volunteer = new Volunteer();
        volunteer.setChatId(update.message().chat().id());
        volunteers.add(volunteer);
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setChat(1);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));
        when(volunteerRepository.findAll()).thenReturn(volunteers);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Пользователь - " + 123L + " задал вопрос : " + "Вопрос");
    }

    @Test
    public void defaultTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Вопрос");
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setChat(0);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));


        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(STANDARD_RESPONSE);

    }

    @Test
    public void default1Test() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Вопрос");
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setAdd(1);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));


        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(STANDARD4_RESPONSE);
    }

    @Test
    public void default2Test() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Вопрос");
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setReport(1);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));


        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(STANDARD3_RESPONSE);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForButtonsMainMenu")
    public void buttonsMainMenuTest(int numbers, String text) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, text);
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setShelter(numbers);

        when(parametersRepository.save(any())).thenReturn(parameters);


        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @Test
    public void infoMenuTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, INFORMATION_SHELTER);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(MENU_SELECTION);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForStoryInfoMenu")
    public void buttonsStoryInfoMenuTest(int numbers, String text) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, STORY_SHELTER);
        Parameters parameters = new Parameters(update.message().chat().id(), numbers);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

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
        Update update = geUpdate(json, JOB_DESCRIPTION);
        Parameters parameters = new Parameters(update.message().chat().id(), numbers);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));


        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(text);
    }

    @Test
    public void buttonSafeInfoMenuTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, SAFETY);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(SECURITY_MEASURES);
    }

    @Test
    public void buttonSecurityInfoMenuTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, SECURITY);
        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(SECURITY_DATA);
    }

   @Test
    public void buttonDataTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, RECORDING_CONTACT_DETAILS);
        Parameters parameters = new Parameters();
        parameters.setChatId(update.message().chat().id());
        parameters.setAdd(1);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();


        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(EXAMPLE_OF_A_MESSAGE);
    }


    @ParameterizedTest
    @MethodSource("provideNumbers")
    public void infoAboutTheAnimalMenuTest(int numbers) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, TAKE_AN_ANIMAL_FROM_A_SHELTER);
        Parameters parameters = new Parameters(update.message().chat().id(), numbers);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(MENU_SELECTION);
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
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, BACK);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @Test
    public void negativeUserVerification() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "+7-923-456-2345 Петя");
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setChatId(update.message().chat().id());
        userData.setName("Петя");
        userData.setPhoneNumber("+7-923-456-2345");
        Parameters parameters = new Parameters();

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        when(userRepository.findByChatId(update.message().chat().id())).thenReturn(userData);

        out.userVerification(update.message().text(), update.message().chat().id());

        ArgumentCaptor<SendMessage> sendMessageArgumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(sendMessageArgumentCaptor.capture());
        SendMessage actual = sendMessageArgumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @ParameterizedTest
    @MethodSource("provideButtonsChat")
    public void buttonsChatMenu(String question, String answer, int numbers) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, question);
        Parameters parameters = new Parameters();
        parameters.setChatId(numbers);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(answer);
    }

    @Test
    public void sendReportTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, SEND_REPORT);
        Parameters parameters = new Parameters();
        parameters.setReport(1);
        Animal animal = new Animal();
        UserData userData = new UserData();
        userData.setChatId(update.message().chat().id());
        userData.setAnimal(animal);
        List<UserData> users = new ArrayList<>();
        users.add(userData);

        when(userRepository.findAll()).thenReturn(users);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(INFORMATION_ABOUT_THE_REPORT);
    }

    @Test
    public void negativeSendReportTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, SEND_REPORT);
        Parameters parameters = new Parameters();
        parameters.setReport(1);
        UserData userData = new UserData();
        userData.setChatId(update.message().chat().id());
        List<UserData> users = new ArrayList<>();
        users.add(userData);

        when(userRepository.findAll()).thenReturn(users);

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(FIND_USER);
    }
    @Test
    public void negative2SendReportTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, SEND_REPORT);
        Parameters parameters = new Parameters();
        parameters.setReport(1);
        Animal animal = new Animal();
        UserData userData = new UserData();
        userData.setChatId(update.message().chat().id());
        userData.setAnimal(animal);
        List<UserData> users = new ArrayList<>();
        users.add(userData);

        when(userRepository.findAll()).thenReturn(users);

        when(parametersRepository.findById(any())).thenReturn(Optional.empty());

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING);
    }

    @Test
    public void responseToTheUserTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "123 = Привет");

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Ответ волонтера: Привет");
    }

    @Test
    public void setVolunteerChatId() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Прими меня-1");
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1);

        when(volunteerRepository.findById(any())).thenReturn(Optional.of(volunteer));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("ID присвоен");

    }

    @Test
    public void negativeSetVolunteerChatId() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Прими меня-1");

        when(volunteerRepository.findById(1L)).thenReturn(Optional.empty());

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Волонтер не найден");

    }

    @ParameterizedTest
    @MethodSource("provideNegativeParameters")
    public void negativeParametersTest(String text) throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, text);

        when(parametersRepository.findById(any())).thenReturn(Optional.empty());

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING);
    }

    @Test
    public void reportPhotoTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("photo.json")).toURI()));
        Update update = geUpdate(json, "AgADAgADw6gxG_mCPAjHE7knq2P_UUJfLyLw4AAgI");
        Parameters parameters = new Parameters();
        PhotoOfAnimal photoOfAnimal = new PhotoOfAnimal();
        photoOfAnimal.setId(1);
        parameters.setReport(1);
        parameters.setPhotoOfAnimal(photoOfAnimal);

        when(photoOfAnimalService.uploadPhoto(update.message().photo())).thenReturn(photoOfAnimal);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(LOADING_THE_REPORT);
    }

    @Test
    public void negativeReportPhotoTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("photo.json")).toURI()));
        Update update = geUpdate(json, "AgADAgADw6gxG_mCPAjHE7knq2P_UUJfLyLw4AAgI");
        Parameters parameters = new Parameters();
        parameters.setReport(0);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(STANDARD2_RESPONSE);
    }

    @Test
    public void reportDocumentTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("document.json")).toURI()));
        Update update = geUpdate(json, "image/jpeg");
        Parameters parameters = new Parameters();
        PhotoOfAnimal photoOfAnimal = new PhotoOfAnimal();
        photoOfAnimal.setId(1);
        parameters.setReport(1);
        parameters.setPhotoOfAnimal(photoOfAnimal);

        when(photoOfAnimalService.uploadPhoto(update.message().document())).thenReturn(photoOfAnimal);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(LOADING_THE_REPORT);
    }
    @Test
    public void negativeReportDocumentTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("document.json")).toURI()));
        Update update = geUpdate(json, "image/jpeg");
        Parameters parameters = new Parameters();
        parameters.setReport(1);

        when(photoOfAnimalService.uploadPhoto(update.message().document())).thenReturn(null);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Неправильный формат. Пришлите фотографию с форматом jpeg или png");
    }

    @Test
    public void reportTextTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, "Корм / Хорошее, привыкание отличное / Изменений в поведении нет");
        Parameters parameters = new Parameters();
        parameters.setReport(1);
        parameters.setText("Корм / Хорошее, привыкание отличное / Изменений в поведении нет");

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(UPLOAD_PHOTO);
    }

    @Test
    public void reportTextAndPhotoTest() throws URISyntaxException, IOException {
        String json1 = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update1 = geUpdate(json1, "Корм / Хорошее, привыкание отличное / Изменений в поведении нет");
        String json2 = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("document.json")).toURI()));
        Update update2 = geUpdate(json2, "AgADAgADw6gxG_mCPAjHE7knq2P_UUJfLyLw4AAgI");
        Parameters parameters = new Parameters();
        PhotoOfAnimal photoOfAnimal = new PhotoOfAnimal();
        photoOfAnimal.setId(1);
        Report report = new Report();
        report.setPhotoOfAnimal(photoOfAnimal);
        parameters.setReport(1);
        parameters.setPhotoOfAnimal(photoOfAnimal);

        when(photoOfAnimalService.uploadPhoto(update2.message().document())).thenReturn(photoOfAnimal);

        when(reportRepository.save(any())).thenReturn(report);

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update2));

        out.process(Collections.singletonList(update1));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(3)).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @Test
    public void closeReportTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, CLOSE_THE_REPORT);
        Parameters parameters = new Parameters();

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @Test
    public void closeAddUser() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("message.json")).toURI()));
        Update update = geUpdate(json, CLOSE_THE_ADD_USER);
        Parameters parameters = new Parameters();

        when(parametersRepository.findById(any())).thenReturn(Optional.of(parameters));

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        assertThat(actual.getParameters().get("text")).isEqualTo(GREETING_SHELTER);
    }

    @Test
    public void stickerTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(Objects.requireNonNull(TelegramBotUpdatesListenerTest.class.getResource("sticker.json")).toURI()));
        Update update = geUpdate(json, "AgADAgADw6gxG_mCPAjHE7knq2P_UUJfLyLw4AAgI");

        out.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo(GET_STICKER);
    }

    private Update geUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }

    public static Stream<Arguments> provideParamsForMainMenu() {
        return Stream.of(
                Arguments.of(START),
                Arguments.of(EXIT)
        );
    }

    public static Stream<Arguments> provideParamsForButtonsMainMenu() {
        return Stream.of(
                Arguments.of(1, DOG_SHELTER),
                Arguments.of(2, CAT_SHELTER)
        );
    }

    public static Stream<Arguments> provideParamsForStoryInfoMenu() {
        return Stream.of(
                Arguments.of(1, INFORMATION_ABOUT_THE_SHELTER_DOG),
                Arguments.of(2, INFORMATION_ABOUT_THE_SHELTER_CAT)
        );
    }

    public static Stream<Arguments> provideParamsForButtonsAddress() {
        return Stream.of(
                Arguments.of(1, WORKING_HOURS_DOG),
                Arguments.of(2, WORKING_HOURS_CAT)
        );
    }

    public static Stream<Arguments> provideNumbers() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2)
        );
    }

    public static Stream<Arguments> provideParamsButtonsInfoAboutTheAnimalMenu() {
        return Stream.of(
                Arguments.of(RULES_OF_ACQUAINTANCE_WITH_ANIMALS, RULES_FOR_GETTING_TO_KNOW_AN_ANIMAL),
                Arguments.of(LIST_OF_DOCUMENTS_FOR_ANIMALS, LIST_OF_DOCUMENTS),
                Arguments.of(TRANSPORTATION_RECOMMENDATION, ANIMAL_TRANSPORTATION),
                Arguments.of(ARRANGEMENT_OF_THE_PUPPY, ANIMAL_ADAPTATION),
                Arguments.of(ARRANGEMENT_OF_THE_KITTEN, ANIMAL_ADAPTATION),
                Arguments.of(ARRANGEMENT_OF_THE_DOG, ADULT_ANIMAL_ADAPTATION),
                Arguments.of(ARRANGEMENT_OF_THE_CAT, ADULT_ANIMAL_ADAPTATION),
                Arguments.of(RECOMMENDATIONS, TIPS_FROM_DOG_HANDLER),
                Arguments.of(RECOMMENDATIONS_DOG_HANDLER, RECOMMENDATION_FOR_DOG_HANDLERS),
                Arguments.of(REASONS_FOR_REFUSAL_DOG, REASON_FOR_REFUSAL),
                Arguments.of(REASONS_FOR_REFUSAL_CAT, REASON_FOR_REFUSAL),
                Arguments.of(RECOMMENDATIONS_DISABLED_DOG, ADAPTATION_OF_AN_ANIMAL_WITH_DISABILITIES),
                Arguments.of(RECOMMENDATIONS_DISABLED_CAT, ADAPTATION_OF_AN_ANIMAL_WITH_DISABILITIES)
        );
    }

    public static Stream<Arguments> provideButtonsChat() {
        return Stream.of(
                Arguments.of(CALLING_A_VOLUNTEER, USER_QUESTION, 1),
                Arguments.of(CLOSE_THE_CHAT, GREETING_SHELTER, 0)
        );
    }

    public static Stream<Arguments> provideNegativeParameters() {
        return Stream.of(
                Arguments.of(STORY_SHELTER),
                Arguments.of(JOB_DESCRIPTION),
                Arguments.of(START_A_CHAT),
                Arguments.of(CLOSE_THE_CHAT),
                Arguments.of(TAKE_AN_ANIMAL_FROM_A_SHELTER),
                Arguments.of(RECORDING_CONTACT_DETAILS),
                Arguments.of(CALLING_A_VOLUNTEER),
                Arguments.of(CLOSE_THE_ADD_USER),
                Arguments.of("Корм / Хорошее, привыкание отличное / Изменений в поведении нет"),
                Arguments.of("+7-923-456-2345 Петя"),
                Arguments.of("Привет")
        );
    }

    public static Stream<Arguments> provideButtonData() {
        return Stream.of(
                Arguments.of(EXAMPLE_OF_A_MESSAGE, 1),
                Arguments.of(GREETING, 0)
        );
    }

     public static Stream<Arguments> provideMediaType() {
        return Stream.of(
                Arguments.of("image/jpeg", LOADING_THE_REPORT),
                Arguments.of("Excel", "Неправильный формат. Пришлите фотографию с форматом jpeg или png")
        );
    }

}