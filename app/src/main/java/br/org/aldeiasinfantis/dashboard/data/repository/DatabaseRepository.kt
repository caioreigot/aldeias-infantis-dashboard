package br.org.aldeiasinfantis.dashboard.data.repository

import android.view.View
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.InformationType
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.User
import com.google.firebase.database.DatabaseReference

interface DatabaseRepository {
    suspend fun getLoggedUserInformation(
        callback: (User?, ServiceResult) -> Unit
    )

    fun fetchDatabaseInformation(
        informationType: InformationType,
        targetReference: DatabaseReference,
        callback: (
            informationData: MutableList<Information>,
            subInformationParent: MutableList<MutableList<Information>>,
            result: ServiceResult
        ) -> Unit
    )

    fun deleteItem(
        path: String,
        callback: (result: ServiceResult) -> Unit
    )

    fun addValueItem(
        referenceToAdd: DatabaseReference,
        header: String,
        value: String,
        competence: String,
        callback: (result: ServiceResult) -> Unit
    )

    fun addPercentageItem(
        referenceToAdd: DatabaseReference,
        header: String,
        subViews: MutableList<View>,
        callback: (result: ServiceResult) -> Unit
    )

    fun editValueItem(
        path: String,
        header: String,
        value: Int,
        competence: String,
        callback: (result: ServiceResult) -> Unit
    )
}