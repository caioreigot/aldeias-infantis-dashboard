package br.org.aldeiasinfantis.dashboard.data.model

object Global {

    const val LOGIN_ATTEMPT_DELAY_IN_MILLIS: Long = 1000L
    const val SPLASH_SCREEN_DURATION_IN_MILLIS: Long = 1200L
    const val MAX_LOGIN_ATTEMPTS: Int = 5
    const val PASSWORD_MINIMUM_LENGTH: Int = 6

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
        const val USERS_PARENT = "users"
        const val ADMINS_PARENT = "admins"

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

        const val USER_NAME = "name"
        const val USER_EMAIL = "email"
    }
}