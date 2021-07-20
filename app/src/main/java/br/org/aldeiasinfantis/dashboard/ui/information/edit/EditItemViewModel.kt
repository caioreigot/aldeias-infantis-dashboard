package br.org.aldeiasinfantis.dashboard.ui.information.edit

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.helper.SingleLiveEvent
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditItemViewModel @Inject constructor(
    private val resProvider: ResourceProvider,
    private val databaseService: DatabaseRepository
) : ViewModel() {

    companion object {
        const val VIEW_FLIPPER_BUTTON = 0
        const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    private val _notifyItemEdited: SingleLiveEvent<Unit> = SingleLiveEvent()
    val notifyItemEdited: LiveData<Unit>
        get() = _notifyItemEdited

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _viewFlipperChildToDisplay: MutableLiveData<Int> = MutableLiveData()
    val viewFlipperChildToDisplay: LiveData<Int>
        get() = _viewFlipperChildToDisplay

    fun editValueItem(
        path: String,
        header: String,
        value: Int,
        competence: String,
    ) {
        _viewFlipperChildToDisplay.value = VIEW_FLIPPER_PROGRESS_BAR

        if (TextUtils.isEmpty(header) ||
            TextUtils.isEmpty(value.toString()) ||
            TextUtils.isEmpty(competence))
        {
            _errorMessage.value = resProvider.getString(R.string.empty_field_error_message)
            return
        }

        try {
            value.toInt()
        } catch (e: Throwable) {
            _errorMessage.value = resProvider.getString(R.string.please_enter_an_integer)
            return
        }

        databaseService.editValueItem(
            path,
            header,
            value,
            competence
        ) { result ->
            _viewFlipperChildToDisplay.value = VIEW_FLIPPER_BUTTON

            when (result) {
                is ServiceResult.Success -> _notifyItemEdited.call()

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun editPercentageItem(
        path: String,
        header: String,
        subInfo: MutableList<Information>,
    ) {
        _viewFlipperChildToDisplay.value = VIEW_FLIPPER_PROGRESS_BAR

        if (TextUtils.isEmpty(header)) {
            _errorMessage.value = resProvider.getString(R.string.empty_field_error_message)
            return
        }

        databaseService.editPercentageItem(path, header, subInfo) { result ->
            _viewFlipperChildToDisplay.value = VIEW_FLIPPER_BUTTON

            when (result) {
                is ServiceResult.Success -> _notifyItemEdited.call()

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }
}