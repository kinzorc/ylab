package ru.kinzorc.habittracker.presentation;

import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.core.repository.HabitRepository;
import ru.kinzorc.habittracker.core.repository.UserRepository;
import ru.kinzorc.habittracker.infrastructure.repository.jdbc.JdbcHabitRepository;
import ru.kinzorc.habittracker.infrastructure.repository.jdbc.JdbcUserRepository;
import ru.kinzorc.habittracker.infrastructure.repository.utils.JdbcConnector;
import ru.kinzorc.habittracker.presentation.menu.MenuNavigator;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

import java.util.concurrent.*;

/**
 * Класс представляет точку входа в консольное приложение Habit Tracker.
 * <p>
 * Приложение запускается с инициализацией базовых компонентов, таких как репозитории и сервисы,
 * и предоставляет пользовательский интерфейс через консоль.
 * </p>
 * <p>
 * При завершении работы приложения вызывается {@link Runtime#addShutdownHook(Thread)} для корректного
 * завершения сессий пользователя.
 * </p>
 */
public class ConsoleApp {

    /**
     * Метод main является точкой входа в консольное приложение Habit Tracker.
     * <p>
     * Приложение инициализирует соединение с базой данных, репозитории для работы с пользователями и привычками,
     * а также запускает главное меню для взаимодействия с пользователем через консоль.
     * В процессе завершения работы приложения, если активны сессии, они удаляются с использованием
     * механизма {@link ExecutorService} с таймаутом в 5 секунд.
     * </p>
     *
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {

        // Инициализация необходимых компонентов приложения
        JdbcConnector jdbcConnector = new JdbcConnector();
        UserRepository userRepository = new JdbcUserRepository(jdbcConnector);
        HabitRepository habitRepository = new JdbcHabitRepository(jdbcConnector);
        ApplicationService applicationService = new ApplicationService(userRepository, habitRepository);
        MenuUtils menuUtils = new MenuUtils();

        // Запуск основного меню
        MenuNavigator.MAIN_MENU.showMenu(applicationService, menuUtils);

        // Добавление shutdown hook для корректного завершения работы приложения
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Завершение работы приложения. Выполняется удаление сессий (таймаут 5 секунд).");

            // Создание потока для выполнения задачи удаления сессий с таймаутом
            ExecutorService executor = Executors.newSingleThreadExecutor();
            // Удаление всех активных сессий пользователей
            Future<?> future = executor.submit(applicationService::removeAllSessions);

            try {
                // Ждем завершения задачи с таймаутом в 5 секунд
                future.get(5, TimeUnit.SECONDS);
                System.out.println("Все сессии успешно удалены.");
            } catch (TimeoutException e) {
                System.err.println("Время на удаление сессий истекло. Задача прервана.");
                future.cancel(true);
            } catch (ExecutionException | InterruptedException e) {
                System.err.println("Ошибка при удалении сессий: " + e.getMessage());
            } finally {
                // Завершаем выполнение потока
                executor.shutdownNow();
            }
        }));
    }
}
