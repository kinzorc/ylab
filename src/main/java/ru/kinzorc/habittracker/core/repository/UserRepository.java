package ru.kinzorc.habittracker.core.repository;

import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.exceptions.UserAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с репозиторием пользователей.
 * <p>
 * Определяет основные методы для добавления, редактирования, удаления и поиска пользователей в системе.
 * Реализует операции по управлению пользователями, такие как блокировка, обновление привилегий и управление сессиями.
 * </p>
 */
public interface UserRepository {

    /**
     * Добавление нового пользователя в систему.
     * <p>
     * Метод добавляет объект {@link UserDTO} в репозиторий. Если пользователь с таким уникальным идентификатором уже существует,
     * выбрасывается исключение {@link UserAlreadyExistsException}.
     * </p>
     *
     * @param user объект {@link UserDTO} для добавления
     * @throws UserAlreadyExistsException если пользователь с таким ID уже существует
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void createUser(UserDTO user) throws UserAlreadyExistsException, SQLException;

    /**
     * Редактирование информации о пользователе.
     * <p>
     * Обновляет данные о пользователе на основе переданного объекта {@link UserDTO}.
     * Если пользователь с таким идентификатором не найден, выбрасывается исключение {@link UserNotFoundException}.
     * </p>
     *
     * @param user объект {@link UserDTO} с обновлёнными данными
     * @throws UserNotFoundException если пользователь с данным ID не найден
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void updateUser(UserDTO user) throws UserNotFoundException, SQLException;

    /**
     * Удаление пользователя по его идентификатору (ID).
     * <p>
     * Если пользователь с переданным идентификатором не найден, выбрасывается исключение {@link UserNotFoundException}.
     * </p>
     *
     * @param userId уникальный идентификатор пользователя
     * @throws UserNotFoundException если пользователь с данным ID не найден
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void deleteUser(long userId) throws UserNotFoundException, SQLException;

    /**
     * Поиск всех пользователей в системе.
     * <p>
     * Возвращает список всех зарегистрированных пользователей.
     * </p>
     *
     * @return список всех пользователей в системе
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    List<UserDTO> findAllUsers() throws SQLException;

    /**
     * Поиск пользователя по уникальному идентификатору (ID).
     * <p>
     * Возвращает объект {@link Optional} с пользователем, если пользователь с переданным идентификатором найден.
     * В противном случае возвращается пустой {@code Optional}.
     * </p>
     *
     * @param userId значение для поиска
     * @return {@code Optional} с объектом {@link UserDTO}, если пользователь найден, иначе пустой {@code Optional}
     * @throws UserNotFoundException если пользователь с указанными данными не найден
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    Optional<UserDTO> findUserById(long userId) throws UserNotFoundException, SQLException;

    /**
     * Поиск пользователя по имени.
     * <p>
     * Возвращает объект {@link Optional} с пользователем, если пользователь с переданным именем найден.
     * В противном случае возвращается пустой {@code Optional}.
     * </p>
     *
     * @param userName значение для поиска
     * @return {@code Optional} с объектом {@link UserDTO}, если пользователь найден, иначе пустой {@code Optional}
     * @throws UserNotFoundException если пользователь с указанными данными не найден
     * @throws SQLException          в случае возникновения ошибок при работе с базой данных
     */
    Optional<UserDTO> findUserByUserName(String userName) throws UserNotFoundException, SQLException;

    /**
     * Поиск пользователя по email.
     * <p>
     * Возвращает объект {@link Optional} с пользователем, если пользователь с переданным email найден.
     * В противном случае возвращается пустой {@code Optional}.
     * </p>
     *
     * @param userEmail значение для поиска
     * @return {@code Optional} с объектом {@link UserDTO}, если пользователь найден, иначе пустой {@code Optional}
     * @throws UserNotFoundException если пользователь с указанными данными не найден
     * @throws SQLException          в случае возникновения ошибок при работе с базой данных
     */
    Optional<UserDTO> findUserByEmail(String userEmail) throws UserNotFoundException, SQLException;

    /**
     * Добавление сессии для пользователя.
     * <p>
     * Метод добавляет сессию для пользователя с указанным идентификатором.
     * </p>
     *
     * @param userId уникальный идентификатор пользователя
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void addSession(long userId) throws SQLException;

    /**
     * Удаление сессии для пользователя.
     * <p>
     * Метод удаляет сессию для пользователя с указанным идентификатором.
     * </p>
     *
     * @param userId уникальный идентификатор пользователя
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void removeSession(long userId) throws SQLException;

    /**
     * Удаление всех активных сессий пользователей.
     * <p>
     * Метод удаляет все активные сессии пользователей.
     * </p>
     *
     * @throws SQLException в случае возникновения ошибок при работе с базой данных
     */
    void removeAllSessions() throws SQLException;
}
