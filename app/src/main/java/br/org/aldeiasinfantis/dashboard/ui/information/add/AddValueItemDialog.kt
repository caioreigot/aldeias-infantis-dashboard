package br.org.aldeiasinfantis.dashboard.ui.information.add

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddValueItemDialog @Inject constructor(
    private val referenceToAdd: DatabaseReference,
    private val refreshInformation: (() -> Unit),
    private val showMessageCallback: (
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)?,
        negativeOnClickListener: (() -> Unit)?,
        dismissCallback: ((choice: Boolean?) -> Unit)?) -> Unit
) : DialogFragment(R.layout.dialog_add_value_info) {

    private val addItemViewModel: AddItemViewModel by viewModels()

    lateinit var headerET: EditText
    lateinit var valueET: EditText
    lateinit var competenceET: EditText
    lateinit var addBtnCV: CardView
    lateinit var viewFlipper: ViewFlipper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //region Assignments
        with (view) {
            headerET = findViewById(R.id.information_header_et)
            valueET = findViewById(R.id.information_value_et)
            competenceET = findViewById(R.id.information_date_et)
            addBtnCV = findViewById(R.id.add_btn_cv)
            viewFlipper = findViewById(R.id.add_view_flipper)
        }
        //endregion

        addBtnCV.setOnClickListener {
            addItemViewModel.addValueItem(
                referenceToAdd,
                headerET.text.toString(),
                valueET.text.toString(),
                competenceET.text.toString()
            )
        }

        with (addItemViewModel) {
            viewFlipperChildToDisplay.observe(viewLifecycleOwner, {
                it?.let { childToDisplay ->
                    viewFlipper.displayedChild = childToDisplay
                }
            })

            notifyItemAdded.observe(viewLifecycleOwner, {
                headerET.text.clear()
                valueET.text.clear()
                competenceET.text.clear()

                refreshInformation()
            })

            errorMessage.observe(viewLifecycleOwner, {
                it?.let { message ->
                    showMessageCallback(
                        MessageType.ERROR,
                        message,
                        null,
                        null,
                        null
                    )

                    dismiss()
                }
            })
        }
    }
}