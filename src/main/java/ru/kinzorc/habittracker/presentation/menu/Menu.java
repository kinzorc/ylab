package ru.kinzorc.habittracker.presentation.menu;

import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

public interface Menu {
    void showMenu(ApplicationService applicationService, MenuUtils menuUtils);
}
