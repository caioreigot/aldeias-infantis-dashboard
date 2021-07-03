package br.org.aldeiasinfantis.dashboard.ui.information

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.helper.SingleLiveEvent
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.InformationType
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val resProvider: ResourceProvider,
    private val database: DatabaseRepository
) : ViewModel() {

    val errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val informationDataPair: MutableLiveData
        <Pair
            <
                MutableList<Information>,
                MutableList<MutableList<Information>>
            >
        > = MutableLiveData()

    fun fetchDatabaseInformation(
        informationType: InformationType,
        targetReference: DatabaseReference
    ) {
        database.fetchDatabaseInformation(informationType, targetReference) {
            informationData, subInformationParent, result ->

            when (result) {
                is ServiceResult.Success ->
                    informationDataPair.value = Pair(informationData, subInformationParent)

                is ServiceResult.Error -> {
                    errorMessage.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
                }
            }
        }
    }
}