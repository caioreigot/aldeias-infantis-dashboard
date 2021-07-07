package br.org.aldeiasinfantis.dashboard.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    protected fun createMessageDialog(
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)? = null,
        negativeOnClickListener: (() -> Unit)? = null,
        dismissCallback: ((choice: Boolean?) -> Unit)? = null
    ): DialogFragment {
        return MessageDialog(
            messageType,
            content,
            positiveOnClickListener,
            negativeOnClickListener,
            dismissCallback
        )
    }
}