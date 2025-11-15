package gadget.basic

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTime @JvmOverloads constructor(
    format: String,
    locale: Locale = Locale.getDefault(Locale.Category.FORMAT),
) {

    companion object {

        const val FORMAT1   = "yyyy/MM/dd HH:mm:ss"
        const val FORMAT1MS = "yyyy/MM/dd HH:mm:ss.SSS"
        const val FORMAT2   = "yyyy-MM-dd_HH-mm-ss"
        const val FORMAT2MS = "yyyy-MM-dd_HH-mm-ss.SSS"

        const val ONE_DAY  = 86400_000L
        const val ONE_HOUR = 3600_000L

        private val INSTANCE = DateTime("")

        /**
         * 获取某一天的特定时间点的时间戳
         * @param timestamp 某一天的任意时间戳，比如说获取当天早上8点半的时间戳，那么这里可以传入[System.currentTimeMillis]
         * @param offset 时间偏移量，比如说获取当天早上8点半的时间戳，那么这里传入[(8*60+30)*60*1000]，即[8h30m]，共[30,600,000ms]
         */
        @JvmStatic
        fun getSpecificTimestampOneDay(timestamp: Long, offset: Long): Long {
            val zoneOffset = INSTANCE.formater.timeZone.rawOffset
            val dayOffset = timestamp % ONE_DAY
            return if (dayOffset <= zoneOffset) {
                timestamp / ONE_DAY * ONE_DAY - zoneOffset
            } else if (ONE_DAY - dayOffset > zoneOffset) {
                timestamp / ONE_DAY * ONE_DAY - zoneOffset
            } else {
                timestamp / ONE_DAY * ONE_DAY + ONE_DAY - zoneOffset
            } + offset
        }

        /**
         * @param timestamp1 参考时间
         * @param timestamp2 对比时间
         * @return 对比时间相对参考时间的天数差，如：0=同一天，-n=前n天，+n=后n天
         */
        @JvmStatic
        fun getDayDifference(timestamp1: Long, timestamp2: Long): Int {
            return ((getSpecificTimestampOneDay(timestamp2, 0L) - getSpecificTimestampOneDay(timestamp1, 0L)) / ONE_DAY).toInt()
        }
    }

    private class Formater @JvmOverloads constructor(
        format: String,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT),
    ) : SimpleDateFormat(format, locale) {

        private val date = Date(System.currentTimeMillis())

        fun format(timestamp: Long): String {
            date.time = timestamp
            return format(date)
        }
    }

    private val formater = Formater(format, locale)

    fun format(timestamp: Long): String = formater.format(timestamp)
}