package com.example.animalsheltertelegrambot.model;

import org.springframework.stereotype.Component;

@Component
public final class Constants {
    /**
     * Обьявление перменной informationAboutTheShelter с описанием информации о приюте.
     */
    public static final String INFORMATION_ABOUT_THE_SHELTER_DOG = "В приюте животных из Астаны находится более 1700 бездомных собак, брошенных, потерянных и оказавшихся на улице при разных обстоятельствах. " +
            "Дворняги, метисы и породистые. У каждой собаки своя история и свой характер. Многие из них в какой-то момент оказались не нужной игрушкой - их предал хозяин. " +
            "Наш бот создан для того чтобы собаки из приюта обрели свой  дом и получили второй шанс на жизнь. " +
            "Так же мы привлекаем новых волонтеров для помощи приютским собакам.";

    public static final String INFORMATION_ABOUT_THE_SHELTER_CAT = "В приюте животных из Астаны находится более 1700 бездомных котов, брошенных, потерянных и оказавшихся на улице при разных обстоятельствах. " +
            "Сиамские, рыжие и лысые. У каждого кота своя история и свой характер. Многие из них в какой-то момент оказались не нужной игрушкой - их предал хозяин. " +
            "Наш бот создан для того чтобы коты из приюта обрели свой  дом и получили второй шанс на жизнь. " +
            "Так же мы привлекаем новых волонтеров для помощи приютским котам и кошкам.";
    /**
     * Обьявлние переменной workingHours с описанием работы приюта адреса.
     */
    public static final String WORKING_HOURS_DOG = "Приют животных из Астаны открыт для посещения 6 дней в неделю с 11:00 до 17:00 ч." +
            " Санитарные дни 1-е и 15-е число месяца (на эти дни приют закрыт для посещения)." +
            "Адрес: Третья улица строителей, дом 15";

    public static final String WORKING_HOURS_CAT = "Приют животных из Астаны открыт для посещения 6 дней в неделю с 11:00 до 17:00 ч." +
            " Санитарные дни 1-е и 15-е число месяца (на эти дни приют закрыт для посещения)." +
            "Адрес: Шестой замоскворецкий переулок";

    /**
     * Обьявлние переменной securityMeasures с рекомендацией о технике безопасности на территории приюта.
     */
    public static final String SECURITY_MEASURES = "— Обувь должна быть на подошве, исключающей непроизвольное скольжение;" +
            "— верхняя одежда должна соответствовать погоде, исключать промокание, а также должна быть облегающей и исключать возможность непроизвольных зацепов за ограждения, строения и иные конструкции." +
            "Запрещается носить в карманах одежды колющие, режущие и стеклянные предметы." +
            "Возможно использование дополнительных средств индивидуальной защиты. Средства индивидуальной защиты должны соответствовать размеру, применяться в исправном, чистом состоянии по назначению и храниться в специально отведенных и оборудованных местах с соблюдением санитарных правил." +
            "При общении с животными работники и посетители приюта обязаны соблюдать меры персональной и общественной безопасности." +
            "При входе в какое-либо помещение или вольер или выходе из него необходимо обязательно закрыть дверь.";
    /**
     * Контактные данные охраны для оформления пропуска на машину
     */
    public static final String SECURITY_DATA = "Для получение пропуска на территорию приюта, пожалуйста, езжайте к центральному входу, по приезду наберите номер 5959";

    /**
     * Правила знакомства с животным
     */
    public static final String RULES_FOR_GETTING_TO_KNOW_AN_ANIMAL = "Веди себя хорошо, не балуйся";
    /**
     * Список документов, необходимых для того, чтобы взять животное из приюта
     */
    public static final String LIST_OF_DOCUMENTS = "Паспорт";
    /**
     * Рекомендации по транспортировке животного
     */
    public static final String ANIMAL_TRANSPORTATION = "Клетка для перевозки";
    /**
     * екомендации по обустройству дома щенка или котенка
     */
    public static final String ANIMAL_ADAPTATION = "Мыть, кормить, любить, не бить";
    /**
     * Рекомендации по обустройству дома взрослой собаки или кота/кошки
     */
    public static final String ADULT_ANIMAL_ADAPTATION = "Мыть, кормить, любить, не бить, выводить гулять";
    /**
     * Рекомендаций по обустройству дома собаки или кота/кошки с ограниченными возможностями
     */
    public static final String ADAPTATION_OF_AN_ANIMAL_WITH_DISABILITIES = "Мыть, кормить, любить, не бить, выводить гулять";
    /**
     * Советы кинолога по первичному общению с собакой
     */
    public static final String TIPS_FROM_DOG_HANDLER = "Не бить палкой";
    /**
     * Рекомендации по проверенным кинологам для дальнейшего обращения к собакой
     */
    public static final String RECOMMENDATION_FOR_DOG_HANDLERS = "Тетя Зина, Дядя Толя";
    /**
     * Список причин, почему могут отказать в просьбе забрать собаку или кота/кошку из приюта
     */
    public static final String REASON_FOR_REFUSAL = "Плохой запах";
    public static final String INFORMATION_ABOUT_THE_REPORT = "Пожалуйста, пришлите данные для ежедневного отчета. В отчет входят 1 - Фото животного (Отправляется отдельным сообщением) 2 - Рацион животного 3 - Общее самочувствие и привыкание к новому месту" +
            " 4 - Изменение в поведении: отказ от старых привычек, приобретение новых. Например: Корм / Хорошее, привыкание отличное / Изменений в поведении нет";
    public static final String UPLOAD_PHOTO = "Пожалуйста, загрузите фотографию";
    public static final String LOADING_THE_REPORT = "Пожалуйста, введите текст отчета";
    public static final String START = "/start";
    public static final String EXIT = "Выход";
    public static final String DOG_SHELTER = "Приют для собак";
    public static final String CAT_SHELTER = "Приют для кошек";
    public static final String INFORMATION_SHELTER = "Узнать информацию о приюте";
    public static final String STORY_SHELTER = "Рассказать о приюте";
    public static final String JOB_DESCRIPTION = "Расписание работы приюта и адрес, схема проезда";
    public static final String SAFETY = "Рекомендации о технике безопасности на территории приюта";
    public static final String SECURITY = "Контактные данные охраны для оформления пропуска на машину";
    public static final String CALLING_A_VOLUNTEER = "Позвать волонтера";
    public static final String TAKE_AN_ANIMAL_FROM_A_SHELTER = "Как взять животное из приюта";
    public static final String RULES_OF_ACQUAINTANCE_WITH_ANIMALS = "Правила знакомства с животным";
    public static final String LIST_OF_DOCUMENTS_FOR_ANIMALS = "Список документов, необходимых для того, чтобы взять животное из приюта";
    public static final String TRANSPORTATION_RECOMMENDATION = "Рекомендации по транспортировке животного";
    public static final String ARRANGEMENT_OF_THE_PUPPY = "Рекомендации по обустройству дома щенка";
    public static final String ARRANGEMENT_OF_THE_KITTEN = "Рекомендации по обустройству дома котенка";
    public static final String ARRANGEMENT_OF_THE_DOG = "Рекомендации по обустройству дома взрослой собаки";
    public static final String ARRANGEMENT_OF_THE_CAT = "Рекомендации по обустройству дома взрослого кота/кошки";
    public static final String RECOMMENDATIONS = "Советы кинолога по первичному общению с собакой";
    public static final String RECOMMENDATIONS_DOG_HANDLER = "Рекомендации по проверенным кинологам для дальнейшего обращения к собакой";
    public static final String REASONS_FOR_REFUSAL_DOG = "Список причин, почему могут отказать в просьбе забрать собаку из приюта";
    public static final String REASONS_FOR_REFUSAL_CAT = "Список причин, почему могут отказать в просьбе забрать кота/кошку из приюта";
    public static final String RECORDING_CONTACT_DETAILS = "Записать контактные данные для связи";
    public static final String RECOMMENDATIONS_DISABLED_DOG = "Рекомендаций по обустройству дома собаки с ограниченными возможностями";
    public static final String RECOMMENDATIONS_DISABLED_CAT = "Рекомендаций по обустройству дома кота/кошки с ограниченными возможностями";
    public static final String BACK = "Назад";
    public static final String SEND_REPORT = "Прислать отчет о питомце";
    public static final String GREETING = "Добрый день. Наш бот помогает найти новый дом брошенным животным. Пожалуйста, выберете интересующий Вас приют из меню ниже:";
    public static final String GREETING_SHELTER = "Добрый день. Рады приветствовать Вас в нашем приюте.";
    public static final String MENU_SELECTION = "Пожалуйста, выберете интересующую Вас информацию из списка ниже.";
    public static final String EXAMPLE_OF_A_MESSAGE = "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
            "Например: +7-909-945-4367 Андрей";
    public static final String START_A_CHAT = "Начать чат";
    public static final String USER_QUESTION = "Пожалуйста введите Ваш вопрос";
    public static final String CLOSE_THE_CHAT = "Закрыть чат с волонтером";
    public static final String STANDARD_RESPONSE = "К сожалению, я не знаю ответа на данный вопрос. Вы можете задать данный вопрос волонтеру через меню Позвать волонтера";
    public static final String STANDARD2_RESPONSE = "Фотографию можно добавить только в отчет. Другие документы бот не обробатывает";
}
