import android.icu.text.SimpleDateFormat
import java.util.Locale

fun simplifyTimestamp(originalTimestamp: String): String {
    val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

    return try {
        val reviewDate = originalFormat.parse(originalTimestamp)
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - reviewDate.time

        when {
            timeDifference < 60_000 -> "Agora mesmo"
            timeDifference < 2 * 60_000 -> "Há 1 minuto"
            timeDifference < 60 * 60_000 -> "Há ${timeDifference / 60_000} minutos"
            timeDifference < 2 * 60 * 60_000 -> "Há uma hora"
            timeDifference < 24 * 60 * 60_000 -> "Há ${timeDifference / (60 * 60_000)} horas"
            timeDifference < 2 * 24 * 60 * 60_000 -> "Ontem"
            timeDifference < 7 * 24 * 60 * 60_000 -> "Há ${timeDifference / (24 * 60 * 60_000)} dias"
            timeDifference < 30 * 24 * 60 * 60_000 -> "Há ${timeDifference / (7 * 24 * 60 * 60_000)} semanas"
            timeDifference < 365 * 24 * 60 * 60_000 -> "Há ${timeDifference / (30 * 24 * 60 * 60_000)} meses"
            timeDifference < 2 * 365 * 24 * 60 * 60_000 -> "Há 1 ano"
            else -> "Há ${timeDifference / (365 * 24 * 60 * 60_000)} anos"
        }
    } catch (e: Exception) {
        originalTimestamp
    }
}