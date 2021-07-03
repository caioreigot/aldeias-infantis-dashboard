package br.org.aldeiasinfantis.dashboard.data.model

object Global {
    object DatabaseNames {
        /*
        dashboard {
            acolhimento em casas lares { ... }
            fortalecimento familiar { ... }
            indicadores gerais { ano anterior { ... }, mes anterior { ... } }
            juventudes { ... }
        }
        */
        const val DASHBOARD_PARENT = "dashboard"

        const val ACOLHIMENTO_EM_CASAS_LARES = "acolhimento em casas lares"
        const val FORTALECIMENTO_FAMILIAR = "fortalecimento familiar"
        const val INDICADORES_GERAIS = "indicadores gerais"
        const val INDICADORES_GERAIS_ANO = "ano anterior"
        const val INDICADORES_GERAIS_MES = "mes anterior"
        const val JUVENTUDES = "juventudes"

        const val INFORMATION_HEADER = "indicador"
        const val INFORMATION_CONTENT = "conteudo"
        const val INFORMATION_COMPETENCE = "competencia"
        const val INFORMATION_VALUE = "valor"
        const val INFORMATION_PERCENTAGE = "porcentagem"
    }
}