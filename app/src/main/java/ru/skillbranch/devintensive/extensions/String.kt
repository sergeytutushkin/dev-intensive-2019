package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16): String {
    var string: String = this

    if (string.length > length)
        string = string.take(length)

    return "${string.trim()}..."
}

fun String.stripHtml(): String {
    val regexHTML = Regex("<.*?>")
    val regexEscape = Regex("&.*?;")
    val regexSpace = Regex("[ ]+")
    var string = this

    string = regexHTML.replace(string, "")
    string = regexEscape.replace(string, "")
    string = regexSpace.replace(string, " ")

    return string.trim()
}