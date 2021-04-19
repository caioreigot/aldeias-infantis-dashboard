package br.org.aldeiasinfantis.dashboard.model

data class Information (
    val header: String,
    val content: String,
    val value: String,
    val date: String
)

class InformationBuilder {
    var header: String = ""
    var content: String = ""
    var value: String = ""
    var date: String = ""

    fun build(): Information {
        return Information(header, content, value, date)
    }
}

fun information(block: InformationBuilder.() -> Unit): Information {
    return InformationBuilder().apply(block).build()
}