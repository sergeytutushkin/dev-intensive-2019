package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var timePrefix = ""
    val timeValue: String
    var timePostfix = ""
    val timeDiff = this.time - date.time
    val timeAbs: Long = abs(timeDiff)

    if (timeDiff > 0)
        timePrefix += "через "
    else if (timeDiff < 0)
        timePostfix += " назад"

    when (timeAbs) {
        in 0..SECOND -> {
            timePrefix = ""
            timeValue = "только что"
            timePostfix = ""
        }
        in SECOND..SECOND * 45 -> timeValue = "несколько секунд"
        in SECOND * 45..SECOND * 75 -> timeValue = "минуту"
        in SECOND * 75..MINUTE * 45 -> timeValue = TimeUnits.MINUTE.plural((timeAbs / MINUTE).toInt())
        in MINUTE * 45..MINUTE * 75 -> timeValue = "час"
        in MINUTE * 75..HOUR * 22 -> timeValue = TimeUnits.HOUR.plural((timeAbs / HOUR).toInt())
        in HOUR * 22..HOUR * 26 -> timeValue = "день"
        in HOUR * 26..DAY * 360 -> timeValue = TimeUnits.DAY.plural((timeAbs / DAY).toInt())
        else -> {
            timePrefix = ""
            if (timeDiff > 0) {
                timeValue = "более чем через год"
            } else
                timeValue = "более года назад"
            timePostfix = ""
        }
    }

    return "$timePrefix$timeValue$timePostfix"
}

enum class TimeUnits(
    val value1: String,
    val value2: String,
    val value3: String
) {
    SECOND("секунд", "секунду", "секунды"),
    MINUTE("минут", "минуту", "минуты"),
    HOUR("часов", "час", "часа"),
    DAY("дней", "день", "дня");

    fun plural(value: Int): String {
        return when (value % 10) {
            0, 5, 6, 7, 8, 9 -> "$value $value1"
            1 -> "$value $value2"
            2, 3, 4 -> "$value $value3"
            else -> ""
        }
    }
}
