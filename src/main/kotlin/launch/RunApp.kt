package launch

import repositories.MovieRepository
import repositories.SessionRepository
import repositories.TicketRepository
import services.CinemaService
import services.UserService
import utils.FileUtilWithCSV
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RunApp {
    fun authentication(){
        val userService = UserService()

        // Import existing users from CSV
        userService.importUsersFromCSV("users.csv")

        while (true) {
            println("1. Вход")
            println("2. Регистрация")
            println("3. Выход")

            print("Выберите пожалуйста: ")

            when (readLine()?.toIntOrNull()) {
                1 -> {
                    // User Login
                    print("Введите имя пользователя: ")
                    val authUsername = readLine().toString()
                    print("Введите пароль: ")
                    val authPassword = readLine().toString()

                    if (userService.authenticateUser(authUsername, authPassword)) {
                        println("Успешный вход.\n")
                        break
                    } else {
                        println("Вход не удался.\n")
                    }

                }
                2 -> {
                    // User Registration
                    print("Введите новое имя пользователя: ")
                    val username = readLine().toString()
                    print("Введите новый пароль: ")
                    val password = readLine().toString()

                    if(isGoodPassword(password)){
                        userService.registerUser(username, password)
                        // Save registered user to CSV
                        userService.exportUsersToCSV("users.csv")

                        println("Регистрация прошла успешно!")
                        println("Пожалуйста войдите в свой аккаунт")
                    }
                    else{
                        println("состоять из символов таблицы ASCII с кодами от 33 до 126;\n" +
                                "быть не короче 8 символов и не длиннее 14;\n" +
                                "из 4 классов символов:\n" +
                                " большие буквы, маленькие буквы, цифры, прочие символ\n " +
                                "— в пароле должны присутствовать не менее трёх любых.")
                    }

                }
                3 -> {
                    println("Выход из программы")
                    return
                }
                else -> println("Invalid choice. Please enter a valid option.\n")
            }
        }
    }
    fun mainMenu() {
        val movieRepository = MovieRepository()
        val sessionRepository = SessionRepository()
        val ticketRepository = TicketRepository()
        val fileUtil = FileUtilWithCSV()
        val cinemaService = CinemaService(movieRepository, sessionRepository, ticketRepository,fileUtil)

        cinemaService.importDataFromCSV("movies.csv", "sessions.csv", "tickets.csv")

        while (true) {
            println("1. Просмотр названия фильмов")
            println("2. Редактировать фильм или сеанс")
            println("3. Выход")

            print("\nВыберите пожалуйста: ")
            when (readLine()?.toIntOrNull()) {
                1 -> {
                    browseMovies(cinemaService)
                }
                2 -> {
                    editMovieOrSession(cinemaService)
                }
                3 -> {
                    // Export data to CSV
                    cinemaService.exportDataToCSV("movies.csv",
                        "sessions.csv", "tickets.csv")
                    println("Данные обновлены в файлы.")
                    println("Выход из приложения")
                    return
                }
                else -> println("Неверный номер. Выберите пожалуйста среди существующих!")
            }
        }
    }

    private fun isGoodPassword(inputPassword: String) : Boolean {
        if (inputPassword.length < 8 || inputPassword.length > 14) {
            return false
        }

        var upper = 0
        var lower = 0
        var digit = 0
        var other = 0

        for (c in inputPassword) {
            if (c < 33.toChar() || c > 126.toChar()) {
                return false
            }
            when {
                c in 'A'..'Z' -> upper = 1
                c in 'a'..'z' -> lower = 1
                c in '0'..'9' -> digit = 1
                else -> other = 1
            }
        }

        return upper + lower + digit + other >= 3
    }

    private fun editMovieOrSession(cinemaService: CinemaService) {
        println("\n1. Редактировать фильм")
        println("2. Редактировать сеанс")
        println("3. Вернуться в меню")
        print("\nEnter your choice: ")

        when (readLine()?.toIntOrNull()) {
            1 -> {
                editMovie(cinemaService)
            }
            2 -> {
                editSession(cinemaService)
            }
            3 -> {
                println("Вернуться в главное меню")
                return
            }
            else -> {
                println("Неверный выбор.")
            }
        }
    }

    private fun editMovie(cinemaService: CinemaService) {
        print("Введите ID фильма для редактирования: ")
        val movieId = readLine()?.trim()
        if (movieId != null && movieId.isNotEmpty()) {
            print("Введите новое название: ")
            val title = readLine().toString()
            print("Введите новый тип фильма: ")
            val type = readLine().toString()
            print("Введите новую продолжительность фильма (Int) : ")
            val duration = readLine()?.toIntOrNull() ?: 0
            if(duration == 0){
                println("Недопустимая продолжительность. Пожалуйста, попробуйте еще раз!\n")
                return
            }
            print("Введите новое описание: ")
            val description = readLine().toString()

            if (cinemaService.editMovie(movieId, title, type, duration, description)) {
                println("Фильм успешно отредактирован.")
            } else {
                println("Неверный ID фильма.")
            }
        } else {
            println("Неверный ID фильма.")
        }
    }

    private fun editSession(cinemaService: CinemaService) {
        print("Введите ID сеанса для редактирования: ")
        val sessionId = readLine()?.trim()
        if (sessionId != null && sessionId.isNotEmpty()) {
            print("Введите новое ID фильма: ")
            val movieId = readLine().toString()

            // Read and parse LocalDateTime input
            print("Введите новое время показа (дд/мм/гггг чч:мм) ")
            val showingTimeInput = readLine().toString()
            val formatter = DateTimeFormatter.ofPattern("(дд/мм/гггг чч:мм)")
            val showingTime = try {
                LocalDateTime.parse(showingTimeInput, formatter)
            } catch (e: Exception) {
                null
            }

            if (showingTime != null) {
                print("Введите новые доступные места (через запятую): ")
                val availableSeatsStr = readLine().toString()

                val availableSeats = availableSeatsStr.split(",").map { it.toIntOrNull() }
                if (availableSeats.all { it != null }) {
                    if (cinemaService.editSession(sessionId, movieId, showingTime, availableSeats.filterNotNull())) {
                        println("Сеанс успешно изменен!")
                    } else {
                        println("Неверный ID сеанса или ID фильма!")
                    }
                } else {
                    println("Неверный формат доступных мест.")
                }
            } else {
                println("Неверный формат отображения времени. Пожалуйста, используйте дд/ММ/гггг ЧЧ:мм.")
            }
        } else {
            println("Неверный ID сеанса.")
        }
    }

    private fun browseMovies(cinemaService: CinemaService) {
        println("\nДоступные фильмы:")
        val movies = cinemaService.getMovies()
        movies.forEach { movie ->
            println("\n${movie.id}. ${movie.title}")
            println("Описание: ${movie.description}, с продолжительностью ${movie.duration} мин ")
        }

        print("Введите ID фильма для просмотра сеансов: ")
        val movieId = readLine()?.trim()

        if (movieId != null && movieId.isNotEmpty()) {
            val sessions = cinemaService.getSessionsByMovieId(movieId)
            if (sessions.isNotEmpty()) {
                println("Сеансы для ${sessions.first().movie.title}:")
                sessions.forEach { session ->
                    println("${session.id}. ${session.showingTime}")
                }

                print("\nВведите ID сеанса для управления билетами: ")
                val sessionId = readLine()?.trim()

                if (sessionId != null && sessionId.isNotEmpty()) {
                    val availableSeats = cinemaService.displayAvailableSeats(sessionId)
                    if (availableSeats != null) {
                        println("Available Seats: $availableSeats")

                        println("\n1. Забронировать билет")
                        println("2. Возврат билета")

                        print("\nEnter your choice: ")
                        when (readLine()?.toIntOrNull()) {
                            1 -> {
                                bookTicket(cinemaService, sessionId, availableSeats)
                            }
                            2 -> {
                                refundTicket(cinemaService)
                            }
                            else -> {
                                println("Неверный выбор.")
                            }
                        }
                    } else {
                        println("Неверный ID сеанса.")
                    }
                } else {
                    println("Неверный ID сеанса.")
                }
            } else {
                println("Для выбранного фильма нет доступных сеансов.")
            }
        } else {
            println("Неверный ID фильма.")
        }
    }
    private fun bookTicket(cinemaService: CinemaService, sessionId: String, availableSeats: List<Int>) {
        print("\nВведите номер места, чтобы забронировать: ")
        val seatNumber = readLine()?.toIntOrNull()

        if (seatNumber != null && seatNumber in availableSeats) {
            val ticket = cinemaService.sellTicket(sessionId, seatNumber)
            if (ticket != null) {
                println("Билет продан: $ticket")
            } else {
                println("Не удалось продать билет.")
            }
        } else {
            println("Неверный номер места или место уже забронировано.")
        }
    }
    private fun refundTicket(cinemaService: CinemaService) {
        print("Введите ID билета для возврата: ")
        val ticketId = readLine()?.trim()
        if (ticketId != null && ticketId.isNotEmpty()) {
            if (cinemaService.refundTicket(ticketId)) {
                println("Билет успешно возвращен.")
            } else {
                println("Неверный ID билета или билет не найден.")
            }
        } else {
            println("Неверный ID билета.")
        }
    }
}
