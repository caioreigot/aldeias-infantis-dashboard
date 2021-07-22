package br.org.aldeiasinfantis.dashboard.ui.information

import androidx.lifecycle.LiveData
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
    private val databaseService: DatabaseRepository
) : ViewModel() {

    private val _errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _showDeletingDialog: MutableLiveData<Boolean> = MutableLiveData()
    val showDeletingDialog: LiveData<Boolean>
        get() = _showDeletingDialog

    private val _refreshInformation: SingleLiveEvent<Unit> = SingleLiveEvent()
    val refreshInformation: LiveData<Unit>
        get() = _refreshInformation

    private val _indicatorTitle: SingleLiveEvent<String> = SingleLiveEvent()
    val indicatorTitle: LiveData<String>
        get() = _indicatorTitle

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
        databaseService.fetchDatabaseInformation(informationType, targetReference) {
            informationData, subInformationParent, result ->

            when (result) {
                is ServiceResult.Success ->
                    informationDataPair.value = Pair(informationData, subInformationParent)

                is ServiceResult.Error -> {
                    _errorMessage.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
                }
            }
        }
    }

    fun deleteItem(path: String) {
        _showDeletingDialog.value = true

        databaseService.deleteItem(path) { result ->
            _showDeletingDialog.value = false

            _errorMessage.value = when (result) {
                /*is ServiceResult.Success -> {}*/

                is ServiceResult.Error -> {
                    _refreshInformation.call()
                    ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
                }

                else -> null
            }
        }
    }

    fun fetchInformationTitle(reference: DatabaseReference) {
        databaseService.fetchInformationTitle(reference) { title, result ->
            when (result) {
                is ServiceResult.Success ->
                    _indicatorTitle.value = title

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }
}