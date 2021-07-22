package br.org.aldeiasinfantis.dashboard.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.helper.SingleLiveEvent
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resProvider: ResourceProvider,
    private val databaseService: DatabaseRepository
) : ViewModel() {

    private val _errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _showLoadingDialog: MutableLiveData<Boolean> = MutableLiveData()
    val showLoadingDialog: LiveData<Boolean>
        get() = _showLoadingDialog

    private val _comparativeMonth: MutableLiveData<String> = MutableLiveData()
    val comparativeMonth: LiveData<String>
        get() = _comparativeMonth

    private val _comparativeYear: MutableLiveData<String> = MutableLiveData()
    val comparativeYear: LiveData<String>
        get() = _comparativeYear

    fun getGeneralIndicatorsComparative(reference: DatabaseReference) {
        _showLoadingDialog.value = true

        databaseService.fetchGeneralIndicatorsComparative(reference) { comparative, result ->
            _showLoadingDialog.value = false

            when (result) {
                is ServiceResult.Success -> {
                    when (reference) {
                        Singleton.DB_INDICADORES_GERAIS_MES_REF ->
                            _comparativeMonth.value = comparative

                        Singleton.DB_INDICADORES_GERAIS_ANO_REF ->
                            _comparativeYear.value = comparative
                    }
                }

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }
}