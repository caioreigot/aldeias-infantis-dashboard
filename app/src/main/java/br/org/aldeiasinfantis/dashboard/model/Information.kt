package br.org.aldeiasinfantis.dashboard.model

data class Information (
    val header: String,
    val content: String,
    val value: String,
    val date: String,
    val percentage: String
)

class InformationBuilder {
    var header: String = ""
    var content: String = ""
    var value: String = ""
    var date: String = ""
    var percentage: String = ""

    fun build(): Information {
        return Information(header, content, value, date, percentage)
    }
}

fun information(block: InformationBuilder.() -> Unit): Information {
    return InformationBuilder().apply(block).build()
}