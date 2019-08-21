package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        return if (!"".equals(question.validate(answer))) {
            "${question.validate(answer)}\n${question.question}" to status.color
        } else if (question.answer.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        } else {
            if (status == Status.CRITICAL) {
                status = Status.NORMAL
                question = Question.NAME
                "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            } else {
                status = status.nextStatus()
                "Это неправильный ответ\n${question.question}" to status.color
            }
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answer: List<String>) {
        NAME("Как меня зовут?", listOf("Бендер", "Bender")) {
            override fun validate(userAnswer: String): String {
                val pattern = "^[^A-ZА-ЯЁ]+.*".toRegex()

                return if (userAnswer.matches(pattern)) {
                    "Имя должно начинаться с заглавной буквы"
                } else {
                    return ""
                }
            }

            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun validate(userAnswer: String): String {
                val pattern = "^[A-ZА-ЯЁ]+.*".toRegex()

                return if (userAnswer.matches(pattern)) {
                    "Профессия должна начинаться со строчной буквы"
                } else {
                    return ""
                }
            }

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun validate(userAnswer: String): String {
                val pattern = ".*[0-9]+.*".toRegex()

                return if (userAnswer.matches(pattern)) {
                    "Материал не должен содержать цифр"
                } else {
                    return ""
                }
            }

            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun validate(userAnswer: String): String {
                val pattern = ".*[^0-9].*".toRegex()

                return if (userAnswer.matches(pattern)) {
                    "Год моего рождения должен содержать только цифры"
                } else {
                    return ""
                }
            }

            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun validate(userAnswer: String): String {
                val pattern = "^[0-9]{7}".toRegex()

                return if (!userAnswer.matches(pattern)) {
                    "Серийный номер содержит только цифры, и их 7"
                } else {
                    return ""
                }
            }

            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun validate(userAnswer: String): String {
                return ""
            }

            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question
        abstract fun validate(userAnswer: String): String
    }
}