package br.org.aldeiasinfantis.dashboard

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information
import br.org.aldeiasinfantis.dashboard.model.InformationType
import br.org.aldeiasinfantis.dashboard.model.information
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.*

class FetchDatabaseInformations {

    // Informações que serão coletadas do Firebase
    private var _header: String? = ""
    private var _content: String? = ""
    private var _value: String? = ""
    private var _date: String? = ""
    private var _percentage: String? = ""

    fun fetchDatabaseInformations(
            activity: Activity,
            callback: (MutableList<Information>, InformationType, DatabaseReference) -> (Unit),
            informationType: InformationType,
            mReference: DatabaseReference,
            recyclerViewMain: RecyclerView
    ) {
        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val informationData = mutableListOf<Information>()

                when (informationType) {
                    InformationType.TEXT -> {
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
                    }

                    InformationType.VALUE -> {
                        for (ss in snapshot.children) {
                            for (snap_shot in ss.children) {
                                _header = if (snap_shot.key == "indicador")
                                    snap_shot.getValue(String::class.java) else _header

                                _value = if (snap_shot.key == "valor")
                                    snap_shot.getValue(Int::class.java).toString() else _value

                                _date = if (snap_shot.key == "competencia")
                                    snap_shot.getValue(String::class.java) else _date
                            }

                            informationData.add(information {
                                this.header = _header!!
                                this.value = _value!!
                                this.date = _date!!.toLowerCase(Locale.ROOT)
                            })
                        }
                    }

                    InformationType.PERCENTAGE -> {
                        for (ss in snapshot.children) {
                            for (snap_shot in ss.children) {
                                _header = if (snap_shot.key == "indicador")
                                    snap_shot.getValue(String::class.java) else _header

                                if (snap_shot.key == "infos") {
                                    for (snap_shot_child in snap_shot.children) {
                                        TODO("Loop entre todas as informações de cada indicador pai")
                                    }
                                }
                            }

                            informationData.add(information {
                                this.header = _header!!
                            })
                        }
                    }
                }

                callback.invoke(informationData, informationType, mReference)
            }

            override fun onCancelled(error: DatabaseError) {
                val dialog = Dialog(activity)

                fun positiveBtnCallback(view: View) {
                    recyclerViewMain.adapter = null
                    fetchDatabaseInformations(activity, callback, informationType, mReference, recyclerViewMain)
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