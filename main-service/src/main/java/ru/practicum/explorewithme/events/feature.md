Для маппинга между сущностью `Event` и DTO (`EventDto`, `NewEventDto`, `LocationDto`), а также связанных
сущностей (`Category`, `User`) и их DTO (`CategoryDto`, `ShortUserDto`), я создам интерфейсы мапперов с использованием
MapStruct. Предполагается, что вы используете Spring, поэтому мапперы будут настроены с `componentModel = "spring"`.
Также учту вложенные объекты, коллекции и специфические преобразования (например, для `EventState` и координат
местоположения).

---

### 1. **Предположения и зависимости**

- У вас есть классы `Category`, `CategoryDto`, `User`, `ShortUserDto`, которые также нужно маппить. Я создам мапперы для
  них, предполагая, что они имеют простую структуру (например, `id` и `name` для `Category`, `id` и `name`
  для `ShortUserDto`).
- Поля `confirmedRequests` и `views` в `EventDto` не имеют аналогов в `Event`, поэтому их маппинг будет зависеть от
  внешней логики (например, сервиса). Я укажу, как их обрабатывать.
- Для работы мапперов добавьте зависимости MapStruct в `pom.xml` или `build.gradle` (см. предыдущий ответ).

---

### 2. **Создание мапперов**

#### 2.1. **Маппер для `Category` и `CategoryDto`**

Предположим, что `Category` и `CategoryDto` выглядят так:

```java

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}

@Data
@Builder
public class CategoryDto {
    private Long id;
    private String name;
}
```

Маппер:

```java
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
```

#### 2.2. **Маппер для `User` и `ShortUserDto`**

Предположим, что `User` и `ShortUserDto` выглядят так:

```java

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
}

@Data
@Builder
public class ShortUserDto {
    private Long id;
    private String name;
}
```

Маппер:

```java
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    ShortUserDto toShortDto(User user);
}
```

#### 2.3. **Маппер для `LocationDto`**

`LocationDto` содержит поля `lat` и `lon`, которые нужно маппить в `locationLat` и `locationLon` сущности `Event`.

```java
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {
    @Mapping(source = "lat", target = "locationLat")
    @Mapping(source = "lon", target = "locationLon")
    Event toEvent(LocationDto locationDto);

    @Mapping(source = "locationLat", target = "lat")
    @Mapping(source = "locationLon", target = "lon")
    LocationDto toDto(Event event);
}
```

#### 2.4. **Маппер для `Event`, `EventDto` и `NewEventDto`**

Этот маппер будет основным и будет использовать `CategoryMapper`, `UserMapper` и `LocationMapper` для маппинга вложенных
объектов. Также учтем преобразование `EventState` (enum в строку и обратно) и обработку полей `confirmedRequests`
и `views`.

```java
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}
)
public interface EventMapper {

    // Маппинг Event -> EventDto
    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "state", target = "state", qualifiedByName = "eventStateToString")
    @Mapping(source = ".", target = "location", qualifiedByName = "toLocationDto")
    @Mapping(target = "confirmedRequests", ignore = true) // Игнорируем, так как нет в Event
    @Mapping(target = "views", ignore = true)
    // Игнорируем, так как нет в Event
    EventDto toDto(Event event);

    // Маппинг NewEventDto -> Event
    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "location", target = ".", qualifiedByName = "toEventFromLocation")
    @Mapping(target = "initiator", ignore = true) // Должен быть установлен в сервисе
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", expression = "java(Event.EventState.PENDING)")
    Event toEntity(NewEventDto newEventDto);

    // Кастомное преобразование EventState в String
    @Named("eventStateToString")
    default String eventStateToString(Event.EventState state) {
        return state != null ? state.name() : null;
    }

    // Кастомное преобразование LocationDto в поля Event
    @Named("toEventFromLocation")
    default Event toEventFromLocation(LocationDto locationDto) {
        return locationDto != null ? LocationMapper.INSTANCE.toEvent(locationDto) : null;
    }

    // Кастомное преобразование полей Event в LocationDto
    @Named("toLocationDto")
    default LocationDto toLocationDto(Event event) {
        return event != null ? LocationMapper.INSTANCE.toDto(event) : null;
    }
}
```

---

### 3. **Объяснение маппинга**

#### 3.1. **Маппинг `Event` -> `EventDto`**

- **Поля `category` и `initiator`**: Используются `CategoryMapper` и `UserMapper` для маппинга вложенных
  объектов (`Category` -> `CategoryDto`, `User` -> `ShortUserDto`).
- **Поле `state`**: Преобразуется из `EventState` (enum) в строку с помощью кастомного метода `eventStateToString`.
- **Поле `location`**: Создается `LocationDto` из полей `locationLat` и `locationLon` с помощью `LocationMapper`.
- **Поля `confirmedRequests` и `views`**: Игнорируются, так как отсутствуют в `Event`. Их нужно устанавливать в
  сервисе (например, через подсчет запросов или просмотров).
- Остальные
  поля (`id`, `title`, `annotation`, `description`, `eventDate`, `createdOn`, `publishedOn`, `participantLimit`, `paid`,
  `requestModeration`)
  мапятся автоматически, так как имена совпадают.

#### 3.2. **Маппинг `NewEventDto` -> `Event`**

- **Поле `category`**: Поле `category` в `NewEventDto` — это `Long` (ID категории), маппится в `category.id`
  сущности `Event`. Полный объект `Category` должен быть установлен в сервисе (например, через репозиторий).
- **Поле `location`**: Преобразуется в поля `locationLat` и `locationLon` с помощью `LocationMapper`.
- **Поле `initiator`**: Игнорируется, так как в `NewEventDto` нет информации об инициаторе. Его нужно установить в
  сервисе (например, текущий пользователь).
- **Поле `createdOn`**: Устанавливается текущее время с помощью выражения `java.time.LocalDateTime.now()`.
- **Поле `publishedOn`**: Игнорируется, так как заполняется позже (например, при публикации).
- **Поле `state`**: Устанавливается в `PENDING` по умолчанию.
- Остальные поля мапятся автоматически.

#### 3.3. **Маппинг `LocationDto`**

- `LocationDto.lat` маппится в `Event.locationLat`.
- `LocationDto.lon` маппится в `Event.locationLon`.
- Обратный маппинг выполняется аналогично.

---

### 4. **Использование мапперов**

#### 4.1. **Внедрение мапперов в сервис**

Так как мапперы настроены с `componentModel = "spring"`, их можно внедрить через `@Autowired`.

```java

@Service
public class EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public EventDto createEvent(NewEventDto newEventDto, Long userId) {
        // Маппинг NewEventDto -> Event
        Event event = eventMapper.toEntity(newEventDto);

        // Установка initiator
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        event.setInitiator(initiator);

        // Установка category
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        event.setCategory(category);

        // Сохранение в БД
        Event savedEvent = eventRepository.save(event);

        // Маппинг Event -> EventDto
        EventDto eventDto = eventMapper.toDto(savedEvent);

        // Установка confirmedRequests и views (пример)
        eventDto.setConfirmedRequests(0); // Логика подсчета запросов
        eventDto.setViews(0L); // Логика подсчета просмотров

        return eventDto;
    }
}
```

#### 4.2. **Пример вызова**

```java
NewEventDto newEventDto = NewEventDto.builder()
        .title("Концерт")
        .annotation("Рок-концерт")
        .description("Большой рок-концерт в центре города")
        .category(1L)
        .eventDate(LocalDateTime.of(2025, 6, 1, 19, 0))
        .location(LocationDto.builder().lat(55.7558f).lon(37.6173f).build())
        .participantLimit(100)
        .paid(true)
        .requestModeration(true)
        .build();

EventDto createdEvent = eventService.createEvent(newEventDto, 1L);
```

---

### 5. **Дополнительные замечания**

- **Поля `confirmedRequests` и `views`**: Эти поля отсутствуют в `Event`, поэтому их нужно устанавливать в сервисе на
  основе данных из других источников (например, подсчет записей в таблице запросов или логов просмотров).
- **Валидация**: Аннотации `@NotNull`, `@NotBlank` в `NewEventDto` обеспечивают валидацию на уровне DTO. Убедитесь, что
  используете `@Valid` в контроллере.
- **Обработка `EventState`**: Если нужно маппить строку `state` из `EventDto` обратно в `EventState`, добавьте метод
  в `EventMapper`:
  ```java
  @Named("stringToEventState")
  default Event.EventState stringToEventState(String state) {
      return state != null ? Event.EventState.valueOf(state) : null;
  }
  ```
  И используйте его в обратном маппинге:
  ```java
  @Mapping(source = "state", target = "state", qualifiedByName = "stringToEventState")
  Event toEntity(EventDto eventDto);
  ```
- **Тестирование**: Напишите юнит-тесты для мапперов, чтобы проверить корректность маппинга, особенно для кастомных
  преобразований.

---

### 6. **Полный код мапперов**

#### `CategoryMapper.java`

```java

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
```

#### `UserMapper.java`

```java

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    ShortUserDto toShortDto(User user);
}
```

#### `LocationMapper.java`

```java

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {
    @Mapping(source = "lat", target = "locationLat")
    @Mapping(source = "lon", target = "locationLon")
    Event toEvent(LocationDto locationDto);

    @Mapping(source = "locationLat", target = "lat")
    @Mapping(source = "locationLon", target = "lon")
    LocationDto toDto(Event event);
}
```

#### `EventMapper.java`

```java

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}
)
public interface EventMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "state", target = "state", qualifiedByName = "eventStateToString")
    @Mapping(source = ".", target = "location", qualifiedByName = "toLocationDto")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventDto toDto(Event event);

    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "location", target = ".", qualifiedByName = "toEventFromLocation")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", expression = "java(Event.EventState.PENDING)")
    Event toEntity(NewEventDto newEventDto);

    @Named("eventStateToString")
    default String eventStateToString(Event.EventState state) {
        return state != null ? state.name() : null;
    }

    @Named("toEventFromLocation")
    default Event toEventFromLocation(LocationDto locationDto) {
        return locationDto != null ? LocationMapper.INSTANCE.toEvent(locationDto) : null;
    }

    @Named("toLocationDto")
    default LocationDto toLocationDto(Event event) {
        return event != null ? LocationMapper.INSTANCE.toDto(event) : null;
    }
}
```

---

### 7. **Заключение**

Эти мапперы обеспечивают полный маппинг между `Event`, `EventDto` и `NewEventDto`, включая вложенные
объекты (`Category`, `User`, `LocationDto`). Они настроены для работы в Spring-приложении и учитывают все особенности,
такие как преобразование `EventState`, обработку координат и игнорирование полей, которые заполняются в сервисе. Если у
вас есть дополнительные требования (например, маппинг коллекций или обновление существующих объектов), напишите, и я
добавлю соответствующие методы!