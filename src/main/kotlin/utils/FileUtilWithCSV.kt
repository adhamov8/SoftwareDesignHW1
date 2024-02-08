package utils

import models.Movie
import models.Session
import models.Ticket
import repositories.MovieRepository
import repositories.SessionRepository
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class FileUtilWithCSV : FileOperations,HandleException {
    override fun readMoviesFromCSV(filePath: String): List<Movie>? {
        val movies = mutableListOf<Movie>()
        try {
            BufferedReader(FileReader(filePath)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val movieData = line?.split(",")
                    if (movieData != null && movieData.size == 5) {
                        val movie = Movie(
                            _id = movieData[0],
                            _title = movieData[1],
                            _type = movieData[2],
                            _duration = movieData[3].toInt(),
                            _description = movieData[4]
                        )
                        movies.add(movie)
                    }
                }
            }
        } catch (e: NumberFormatException) {
            handleException("Ошибка парсинга фильмов в CSV", e)
        } catch (e: Exception) {
            handleException("Ошибка чтения фильмов из CSV", e)
        }
        return movies
    }

    override fun writeMoviesToCSV(filePath: String, movies: List<Movie>) {
        try {
            FileWriter(filePath).use { writer ->
                movies.forEach { movie ->
                    writer.append("${movie.id},${movie.title},${movie.type},${movie.duration}," +
                            "${movie.description}\n")
                }
            }
        } catch (e: Exception) {
            handleException("Ошибка записи фильмов в CSV.", e)
        }
    }

    override fun readSessionsFromCSV(filePath: String, movieRepository: MovieRepository): List<Session> {
        val sessions = mutableListOf<Session>()
        try {
            BufferedReader(FileReader(filePath)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val sessionData = line?.split(",")
                    if (sessionData != null && sessionData.size == 4) {
                        val movieId = sessionData[1]
                        val movie = movieRepository.getMovieById(movieId)
                        if (movie != null) {
                            try {
                                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                val session = Session(
                                    _id = sessionData[0],
                                    _movie = movie,
                                    _showingTime = LocalDateTime.parse(sessionData[2], formatter),
                                    _availableSeats = sessionData[3].split(";").map { it.toInt() }.toMutableList()
                                )
                                sessions.add(session)
                            } catch (e: DateTimeParseException) {
                                val invalidDatetime = sessionData[2]
                                throw InvalidDateTimeFormatException("Неверный формат даты и времени в CSV.: $invalidDatetime", e)
                            }
                            catch (e: Exception) {
                                handleException("Ошибка чтения сеансов из CSV.", e)
                                System.exit(0)
                            }
                        } else {
                            handleException("ID фильма $movieId не найден для сеанса", null)
                            System.exit(0)
                        }
                    }
                }
            }
        } catch (e: NumberFormatException) {
            handleException("Ошибка парсинга в CSV сеанса", e)
        } catch (e: Exception) {
            handleException("Ошибка чтения сеансов из CSV.", e)
        }
        return sessions
    }

    override fun writeSessionsToCSV(filePath: String, sessions: List<Session>) {
        try {
            FileWriter(filePath).use { writer ->
                sessions.forEach { session ->
                    writer.append("${session.id},${session.movie.id},${session.showingTime}," +
                            "${session.availableSeats.joinToString(";")}\n")
                }
            }
        } catch (e: Exception) {
            handleException("Ошибка записи сеансов в CSV.", e)
        }
    }

    override fun readTicketsFromCSV(filePath: String, sessionRepository: SessionRepository): List<Ticket> {
        val tickets = mutableListOf<Ticket>()
        try {
            BufferedReader(FileReader(filePath)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val ticketData = line?.split(",")
                    if (ticketData != null && ticketData.size == 3) {
                        val sessionId = ticketData[1]
                        val session = sessionRepository.getSessionById(sessionId)
                        if (session != null) {
                            val ticket = Ticket(
                                _id = ticketData[0],
                                _session = session,
                                _seatNumber = ticketData[2].toInt()
                            )
                            tickets.add(ticket)
                        } else {
                            handleException("ID сеанса $sessionId не найден для билета", null)
                        }
                    }
                }
            }
        } catch (e: NumberFormatException) {
            handleException("Ошибка парсинга в билетах CSV", e)
        } catch (e: Exception) {
            handleException("Ошибка чтения билетов из CSV.", e)
        }
        return tickets
    }

    override fun writeTicketsToCSV(filePath: String, tickets: List<Ticket>) {
        try {
            FileWriter(filePath).use { writer ->
                tickets.forEach { ticket ->
                    writer.append("${ticket.id},${ticket.session.id},${ticket.seatNumber}\n")
                }
            }
        } catch (e: Exception) {
            handleException("Ошибка записи билетов в CSV.", e)
        }
    }

    override fun handleException(message: String, e: Exception?) {
        println("$message: ${e?.message}")
        System.exit(0)
    }
}
