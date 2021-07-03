package br.org.aldeiasinfantis.dashboard.data.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Singleton {
    // Database Instance
    private fun getDatabaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()
    val DATABASE: FirebaseDatabase = getDatabaseInstance()

    // Database Dashboard Instance
    private fun getDatabaseDashboardInstance(): DatabaseReference =
        DATABASE.reference.child(Global.DatabaseNames.DASHBOARD_PARENT)

    // Acolhimento em Casas Lares Reference
    private fun getAcolhimentoEmCasasLaresReference(): DatabaseReference =
        DB_DASHBOARD_REF.child(Global.DatabaseNames.ACOLHIMENTO_EM_CASAS_LARES)

    // Fortalecimento Familiar Reference
    private fun getFortalecimentoFamiliarReference(): DatabaseReference =
        DB_DASHBOARD_REF.child(Global.DatabaseNames.FORTALECIMENTO_FAMILIAR)

    // Juventudes Reference
    private fun getJuventudesReference(): DatabaseReference =
        DB_DASHBOARD_REF.child(Global.DatabaseNames.JUVENTUDES)

    // Indicadores Gerais Mes Reference
    private fun getIndicadoresGeraisMesReference(): DatabaseReference =
        DB_DASHBOARD_REF.child(Global.DatabaseNames.INDICADORES_GERAIS)
            .child(Global.DatabaseNames.INDICADORES_GERAIS_MES)

    // Indicadores Gerais Ano Reference
    private fun getIndicadoresGeraisAnoReference(): DatabaseReference =
        DB_DASHBOARD_REF.child(Global.DatabaseNames.INDICADORES_GERAIS)
            .child(Global.DatabaseNames.INDICADORES_GERAIS_ANO)

    val DB_DASHBOARD_REF = getDatabaseDashboardInstance()
    val DB_ACOLHIMENTO_CASAS_LARES_REF = getAcolhimentoEmCasasLaresReference()
    val DB_FORTALECIMENTO_FAMILIAR_REF = getFortalecimentoFamiliarReference()
    val DB_JUVENTUDES_REF = getJuventudesReference()
    val DB_INDICADORES_GERAIS_MES_REF = getIndicadoresGeraisMesReference()
    val DB_INDICADORES_GERAIS_ANO_REF = getIndicadoresGeraisAnoReference()
}