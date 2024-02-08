
# Приложение для работников кинотеатра

## Обзор
Создано приложение, которое позволит сотруднику кинотеатра (с единственным зрительным залом) управлять данными о фильмах, находящихся в прокате, и расписании сеансов их показа, продавать, осуществлять возврат билетов и отмечать посетителей сеансов.

## Главное меню
После запуска программы вам откроется главное меню:
1. Вход
2. Регистрация
3. Выход
### Вариант 1 (Вход):
Введите имя пользователя и пароль для доступа к программе.
При успешном входе перейдите в главное меню.
### Вариант 2 (Регистрация):
Если у вас нет учетной записи, зарегистрируйтесь, введя новое имя пользователя и уникальный пароль.
Та есть пароль должен удовлетворять условиям:
- состоять из символов таблицы ASCII с кодами от 33 до 126;
- быть не короче 8 символов и не длиннее 14;
- из 4 классов символов — большие буквы, маленькие буквы, цифры, прочие символы — в пароле должны присутствовать не менее трёх любых.
Зарегистрированные пользователи хранятся в файле «users.csv».
### Вариант 3 (Выход):
Завершение программы.

## Меню приложения
После входа в систему отобразится главное меню приложения:
1. Просмотр фильмов
2. Редактировать фильм или сеанс
3. Выход
### Вариант 1 (Просмотр фильмов):
Просмотрите доступные фильмы с подробной информацией (ID, название, тип, продолжительность, описание).
Выберите фильм для просмотра сеансов.
### Вариант 2 (Редактировать фильм или сеанс):
Изменение сведений о выбранном фильме или сеансе
### Вариант 3 (Выход):
Сохраните данные в файлы CSV и выйдите из приложения.

## Меню для сеансов фильмов
Выбрав фильм в меню «Обзор фильмов», вы увидите:
Введите ID фильма для просмотра сеансов:
## Меню для сеанса
После ввода действительного ID фильма отобразится меню сеанса:
1. <Показано время сеанса 1>
2. <Сеанс 2 показывает время>
   ...
   Варианты 1, 2, ..., N:
   Выберите сеанс для управления билетами или возврата средств.

## Управление билетами
В выбранном сеансе вы увидите:
Доступные места: <место 1, место 2, ..., место>
1. Забронировать билет
2. Возврат билета

### Вариант 1 (Забронировать билет):
Введите номер места, чтобы забронировать билет.
Информация о билете будет отображена.

### Вариант 2 (Возврат билета):
Введите ID билета, чтобы вернуть билет.

## Редактирование данных
В меню сеанса вы можете редактировать данные о фильмах и сеансах:
1. Редактировать фильм
2. Редактировать сеанс
3. Вернуться в главное меню.
### Вариант 1 (Редактировать фильм):
Изменить информацию о выбранном фильме.

### Вариант 2 (Редактирование сеанса):
Измените сведения о выбранном сеансе, например время или наличие мест.
### Вариант 3 (возврат в главное меню):
Вернуться в главное меню

## Дополнительные замечания
Данные о фильмах, сеансах и билетах хранятся в файлах CSV (movies.csv, session.csv, Tickets.csv).