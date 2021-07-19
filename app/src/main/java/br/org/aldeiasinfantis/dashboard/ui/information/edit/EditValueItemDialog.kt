package br.org.aldeiasinfantis.dashboard.ui.information.edit

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditValueItemDialog @Inject constructor(
    private val information: Information,
    private val refreshInformation: (() -> Unit),
    private val showMessageCallback: (
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)?,
        negativeOnClickListener: (() -> Unit)?,
        dismissCallback: ((choice: Boolean?) -> Unit)?) -> Unit
) : DialogFragment(R.layout.dialog_edit_value_info) {

    private val editItemViewModel: EditItemViewModel by viewModels()

    lateinit var headerET: EditText
    lateinit var valueET: EditText
    lateinit var competenceET: EditText
    lateinit var saveBtnCV: CardView
    lateinit var viewFlipper: ViewFlipper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //region Assignments
        with (view) {
            headerET = findViewById(R.id.information_header_et)
            valueET = findViewById(R.id.information_value_et)
            competenceET = findViewById(R.id.information_date_et)
            saveBtnCV = findViewById(R.id.add_btn_cv)
            viewFlipper = findViewById(R.id.add_view_flipper)
        }
        //endregion

        headerET.setText(information.header)
        valueET.setText(information.value.toString())
        competenceET.setText(information.competence)

        saveBtnCV.setOnClickListener {
            editItemViewModel.editValueItem(
                information.path,
                headerET.text.toString(),
                Integer.parseInt(valueET.text.toString()),
                competenceET.text.toString()
            )
        }

        with (editItemViewModel) {
            viewFlipperChildToDisplay.observe(viewLifecycleOwner, {
                it?.let { childToDisplay ->
                    viewFlipper.displayedChild = childToDisplay
                }
            })

            notifyItemEdited.observe(viewLifecycleOwner, {
                refreshInformation()

                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.item_successfully_edited_toast),
                    Toast.LENGTH_SHORT
                ).show()

                dismiss()
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