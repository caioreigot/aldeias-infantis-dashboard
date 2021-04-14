package br.org.aldeiasinfantis.dashboard.model

data class Information (
    val header: String,
    val content: String
)

class InformationBuilder {
    var header: String = ""
    var content: String = ""

    fun build(): Information {
        return Information(header, content)
    }
}

fun information(block: InformationBuilder.() -> Unit): Information {
    return InformationBuilder().apply(block).build()
}