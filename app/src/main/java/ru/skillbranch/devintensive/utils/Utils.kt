package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        if (fullName == "" || fullName == " ") return null to null

        val parts: List<String>? = fullName?.split(" ")

        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var buf = ""

        for (s in payload)
            when (s) {
                'а' -> buf += "a"
                'б' -> buf += "b"
                'в' -> buf += "v"
                'г' -> buf += "g"
                'д' -> buf += "d"
                'е' -> buf += "e"
                'ё' -> buf += "e"
                'ж' -> buf += "zh"
                'з' -> buf += "z"
                'и' -> buf += "i"
                'й' -> buf += "i"
                'к' -> buf += "k"
                'л' -> buf += "l"
                'м' -> buf += "m"
                'н' -> buf += "n"
                'о' -> buf += "o"
                'п' -> buf += "p"
                'р' -> buf += "r"
                'с' -> buf += "s"
                'т' -> buf += "t"
                'у' -> buf += "u"
                'ф' -> buf += "f"
                'х' -> buf += "h"
                'ц' -> buf += "c"
                'ч' -> buf += "ch"
                'ш' -> buf += "sh"
                'щ' -> buf += "sh'"
                'ъ' -> buf += ""
                'ы' -> buf += "i"
                'ь' -> buf += ""
                'э' -> buf += "e"
                'ю' -> buf += "yu"
                'я' -> buf += "ya"
                'А' -> buf += "A"
                'Б' -> buf += "B"
                'В' -> buf += "V"
                'Г' -> buf += "G"
                'Д' -> buf += "D"
                'Е' -> buf += "E"
                'Ё' -> buf += "E"
                'Ж' -> buf += "Zh"
                'З' -> buf += "Z"
                'И' -> buf += "I"
                'Й' -> buf += "I"
                'К' -> buf += "K"
                'Л' -> buf += "L"
                'М' -> buf += "M"
                'Н' -> buf += "N"
                'О' -> buf += "O"
                'П' -> buf += "P"
                'Р' -> buf += "R"
                'С' -> buf += "S"
                'Т' -> buf += "T"
                'У' -> buf += "U"
                'Ф' -> buf += "F"
                'Х' -> buf += "H"
                'Ц' -> buf += "C"
                'Ч' -> buf += "Ch"
                'Ш' -> buf += "Sh"
                'Щ' -> buf += "Sh'"
                'Ъ' -> buf += ""
                'Ы' -> buf += "I"
                'Ь' -> buf += ""
                'Э' -> buf += "E"
                'Ю' -> buf += "Yu"
                'Я' -> buf += "Ya"
                ' ' -> buf += divider
                else -> buf += s.toString()
            }

        return buf
    }

    fun toInitials(firstName: String?, lastName: String?): String? {

        val firstInitial: String?
        val lastInitial: String?

        if (firstName.isNullOrBlank()) firstInitial = "" else firstInitial = firstName.get(0).toString()
        if (lastName.isNullOrBlank()) lastInitial = "" else lastInitial = lastName.get(0).toString()

        if (firstInitial == "" && lastInitial == "") return null

        return (firstInitial + lastInitial).toUpperCase()
    }
}