package br.org.aldeiasinfantis.dashboard

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information
import br.org.aldeiasinfantis.dashboard.model.information
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FetchDatabaseInformations {

    // Informações que serão coletadas do Firebase
    private var _header: String? = ""
    private var _content: String? = ""

    fun fetchDatabaseInformations(
            activity: Activity,
            callback: (MutableList<Information>, DatabaseReference) -> (Unit),
            mReference: DatabaseReference,
            recyclerViewMain: RecyclerView
    ) {
        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val informationData = mutableListOf<Information>()

                for (ss in snapshot.children) {
                    for (snap_shot in ss.children) {
                        _header = if (snap_shot.key == "cabecalho")
                            snap_shot.getValue(String::class.java) else _header

                        _content = if (snap_shot.key == "conteudo")
                            snap_shot.getValue(String::class.java) else _content
                    }

                    informationData.add(information {
                        this.header = _header!!
                        this.content = _content!!
                    })
                }

                callback.invoke(informationData, mReference)
            }

            override fun onCancelled(error: DatabaseError) {
                val dialog = Dialog(activity)

                fun positiveBtnCallback(view: View) {
                    recyclerViewMain.adapter = null
                    fetchDatabaseInformations(activity, callback, mReference, recyclerViewMain)
                    dialog.dismiss()
                }

                fun negativeBtnCallback(view: View) {
                    dialog.dismiss()
                }

                Utils.createConnectionErrorDialog(
                        dialog, activity, ::positiveBtnCallback, ::negativeBtnCallback
                ).show()
            }
        })
    }

}