package ru.kinzorc.habittracker.core.repository;

import ru.kinzorc.habittracker.application.dto.HabitDTO;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.entities.Habit;
import ru.kinzorc.habittracker.core.exceptions.HabitAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.HabitNotFoundException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс для управления привычками и их статистикой.
 * <p>
 * Определяет основные методы для добавления, редактирования, удаления, выполнения привычек и работы со статистикой.
 * </p>
 */
public interface HabitRepository {

    /**
     * Добавляет новую привычку для пользователя.
     *
     * @param user  объект {@link UserDTO}, к которому добавляется привычка
     * @param habit объект {@link HabitDTO}, представляющий новую привычку
     * @throws HabitAlreadyExistsException если привычка с таким именем уже существует у пользователя
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void addHabit(UserDTO user, HabitDTO habit) throws HabitAlreadyExistsException, SQLException;

    /**
     * Удаляет привычку по её уникальному идентификатору.
     *
     * @param habitId уникальный идентификатор привычки
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void deleteHabit(long habitId) throws HabitNotFoundException, SQLException;

    /**
     * Удаляет все привычки добавленные пользователем.
     *
     * @param user объект пользователя
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void deleteAllHabitsForUser(UserDTO user) throws HabitNotFoundException, SQLException;

    /**
     * Удаляет все привычки, связанные с пользователем.
     *
     * @param user объект {@link UserDTO}, для которого удаляются все привычки
     * @throws UserNotFoundException  если пользователь не найден
     * @throws HabitNotFoundException если у пользователя нет привычек
     * @throws SQLException           в случае возникновения ошибок при работе с базой данных
     */
    void deleteAllHabit(UserDTO user) throws UserNotFoundException, HabitNotFoundException, SQLException;

    /**
     * Обновляет данные о существующей привычке.
     *
     * @param habit объект {@link HabitDTO} с обновлёнными данными
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void updateHabit(HabitDTO habit) throws HabitNotFoundException, SQLException;

    /**
     * Добавляет отметку о выполнении привычки на указанную дату.
     *
     * @param habit           уникальный идентификатор привычки
     * @param executionDate дата выполнения привычки
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void markExecution(HabitDTO habit, LocalDateTime executionDate) throws HabitNotFoundException, SQLException;

    /**
     * Возвращает список выполнений привычки по уникальному идентификатору.
     *
     * @param id уникальный идентификатор привычки
     * @return список дат выполнений привычки
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    List<LocalDate> getExecutions(long id) throws HabitNotFoundException, SQLException;

    /**
     * Сбрасывает все выполненные действия привычки.
     *
     * @param id уникальный идентификатор привычки
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void resetExecutions(long id) throws HabitNotFoundException, SQLException;

    /**
     * Сбрасывает все выполненные действия всех привычек у пользователя.
     *
     * @param user объект пользователя у которого сбрасываются все выполнения по всем привычкам
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void resetAllExecutionsForUser(UserDTO user) throws HabitNotFoundException, SQLException;

    /**
     * Сбрасывает все выполнения для всех привычек.
     *
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void resetExecutionsAllHabits() throws SQLException;

    /**
     * Возвращает статистику выполнения привычки за указанный период.
     *
     * @param habit           объект привычки
     * @param startPeriodDate дата начала периода
     * @param endPeriodDate   дата окончания периода
     * @return карта, где ключ — дата, а значение — количество выполнений в этот день
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    Map<LocalDate, Integer> getStatisticByPeriod(HabitDTO habit, LocalDateTime startPeriodDate, LocalDateTime endPeriodDate) throws HabitNotFoundException, SQLException;

    /**
     * Сбрасывает статистику выполнения привычки.
     * <p>
     * Метод позволяет сбросить выполненные действия или стрики привычки по её уникальному идентификатору.
     * </p>
     *
     * @param id              уникальный идентификатор привычки
     * @param resetExecutions если true, сбрасываются все выполненные действия
     * @param resetStreaks    если true, сбрасываются все стрики (серии)
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void resetStatistics(long id, boolean resetExecutions, boolean resetStreaks) throws HabitNotFoundException, SQLException;

    /**
     * Рассчитывает процент выполнения привычки на переданный период
     *
     * @param habit привычка по которой рассчитывает успешный процент выполнения
     * @param startPeriodDate начало периода
     * @param endPeriodDate конец периода
     * @return процент выполнения привычки
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    int calculateExecutionPercentage(HabitDTO habit, LocalDateTime startPeriodDate, LocalDateTime endPeriodDate) throws SQLException;

    /**
     * Рассчитывает текущий стрик (серии выполнений) привычки.
     *
     * @param habit объект привычки
     * @return количество дней в текущем стрике
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    int calculateStreak(HabitDTO habit, LocalDateTime newExecutionDate) throws SQLException;

    /**
     * Возвращает список всех привычек в системе.
     *
     * @return список всех объектов {@link Habit}
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    List<HabitDTO> findAllHabits() throws SQLException;

    /**
     * Поиск привычки по её уникальному идентификатору.
     *
     * @param habitId уникальный идентификатор привычки
     * @return объект {@link Optional} с привычкой, если найдена, иначе пустой {@code Optional}
     * @throws HabitNotFoundException если привычка с данным ID не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    Optional<HabitDTO> findHabitByID(long habitId) throws HabitNotFoundException, SQLException;

    /**
     * Поиск привычки по её имени.
     *
     * @param habitName имя привычки
     * @return объект {@link Optional} с привычкой, если найдена, иначе пустой {@code Optional}
     * @throws HabitNotFoundException если привычка с данным именем не найдена
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    Optional<HabitDTO> findHabitByName(String habitName) throws HabitNotFoundException, SQLException;

    /**
     * Возвращает список привычек, связанных с указанным пользователем.
     *
     * @param user объект {@link UserDTO}, для которого нужно найти все привычки
     * @return список привычек, связанных с пользователем
     * @throws UserNotFoundException если пользователь не найден
     * @throws SQLException          если возникает ошибка при работе с базой данных
     */
    List<HabitDTO> findHabitByUser(UserDTO user) throws UserNotFoundException, SQLException;
}