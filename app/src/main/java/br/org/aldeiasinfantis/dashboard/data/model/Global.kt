package br.org.aldeiasinfantis.dashboard.data.model

object Global {

    const val SPLASH_SCREEN_DURATION_IN_MILLIS: Long = 1200L
    const val LOGIN_ATTEMPT_DELAY_IN_MILLIS: Long = 1500L
    const val AUTH_TIME_OUT_IN_MILLIS: Long = 8000L
    const val GET_USER_INFO_TIME_OUT_IN_MILLIS: Long = 5000L
    const val MAX_LOGIN_ATTEMPTS: Int = 5

    const val PASSWORD_MINIMUM_LENGTH: Int = 6

    object DatabaseNames {
        /*
        dashboard {
            acolhimento em casas lares { ... },
            fortalecimento familiar { ... },
            indicadores gerais {
                ano anterior { comparativo: "", ... },
                mes anterior { comparativo: "", ... }
            },
            juventudes { ... }
        }
        */
        const val DASHBOARD_PARENT = "dashboard"
        const val USERS_PARENT = "users"
        const val ADMINS_PARENT = "admins"

        const val ACOLHIMENTO_EM_CASAS_LARES = "acolhimento_em_casas_lares"
        const val FORTALECIMENTO_FAMILIAR = "fortalecimento_familiar"

        const val INDICADORES_GERAIS = "indicadores_gerais"
        const val INDICADORES_GERAIS_ANO = "ano_anterior"
        const val INDICADORES_GERAIS_MES = "mes_anterior"
        const val INDICADORES_GERAIS_COMPARATIVO = "comparativo"
        const val INDICADORES_GERAIS_SUB_INFO = "infos"

        const val JUVENTUDES = "juventudes"

        const val PARENT_INDICATOR_HEADER = "titulo"

        const val INFORMATION_HEADER = "indicador"
        const val INFORMATION_CONTENT = "conteudo"
        const val INFORMATION_COMPETENCE = "competencia"
        const val INFORMATION_VALUE = "valor"
        const val INFORMATION_PERCENTAGE = "porcentagem"

        const val USER_NAME = "name"
        const val USER_EMAIL = "email"
    }
}