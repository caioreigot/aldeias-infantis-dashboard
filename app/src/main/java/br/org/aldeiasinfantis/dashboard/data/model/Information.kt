package br.org.aldeiasinfantis.dashboard.data.model

import com.google.firebase.database.PropertyName

data class Information (
    var uid: String = "",

    @get:PropertyName(Global.DatabaseNames.INFORMATION_HEADER)
    @set:PropertyName(Global.DatabaseNames.INFORMATION_HEADER)
    var header: String = "",

    @get:PropertyName(Global.DatabaseNames.INFORMATION_CONTENT)
    @set:PropertyName(Global.DatabaseNames.INFORMATION_CONTENT)
    var content: String = "",

    @get:PropertyName(Global.DatabaseNames.INFORMATION_VALUE)
    @set:PropertyName(Global.DatabaseNames.INFORMATION_VALUE)
    var value: Int = -1,

    @get:PropertyName(Global.DatabaseNames.INFORMATION_COMPETENCE)
    @set:PropertyName(Global.DatabaseNames.INFORMATION_COMPETENCE)
    var competence: String = "",

    @get:PropertyName(Global.DatabaseNames.INFORMATION_PERCENTAGE)
    @set:PropertyName(Global.DatabaseNames.INFORMATION_PERCENTAGE)
    var percentage: String = ""
)

class InformationBuilder {
    var uid: String = ""
    var header: String = ""
    var content: String = ""
    var value: Int = -1
    var competence: String = ""
    var percentage: String = ""

    fun build(): Information {
        return Information(uid, header, content, value, competence, percentage)
    }
}

fun information(block: InformationBuilder.() -> Unit): Information {
    return InformationBuilder().apply(block).build()
}