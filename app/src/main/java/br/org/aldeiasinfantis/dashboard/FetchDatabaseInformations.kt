package br.org.aldeiasinfantis.dashboard

import android.app.Activity
import android.app.Dialog
import android.util.Log
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

            callback: (MutableList<Information>,
                       MutableList<MutableList<Information>>,
                       InformationType,
                       DatabaseReference) -> (Unit),

            informationType: InformationType,
            mReference: DatabaseReference,
            recyclerViewMain: RecyclerView
    ) {
        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val informationData = mutableListOf<Information>()

                val subInformationsParent = mutableListOf<MutableList<Information>>()
                val subInformationData = mutableListOf<Information>()

                when (informationType) {
                    InformationType.TEXT -> {
                        for (info in snapshot.children) {
                            for (infoChild in info.children) {
                                _header = if (infoChild.key == "cabecalho")
                                    infoChild.getValue(String::class.java) else _header

                                _content = if (infoChild.key == "conteudo")
                                    infoChild.getValue(String::class.java) else _content
                            }

                            informationData.add(information {
                                this.header = _header!!
                                this.content = _content!!
                            })
                        }
                    }

                    InformationType.VALUE -> {
                        for (info in snapshot.children) {
                            for (infoChild in info.children) {
                                _header = if (infoChild.key == "indicador")
                                    infoChild.getValue(String::class.java) else _header

                                _value = if (infoChild.key == "valor")
                                    infoChild.getValue(Int::class.java).toString() else _value

                                _date = if (infoChild.key == "competencia")
                                    infoChild.getValue(String::class.java) else _date
                            }

                            informationData.add(information {
                                this.header = _header!!
                                this.value = _value!!
                                this.date = _date!!.toLowerCase(Locale.ROOT)
                            })
                        }
                    }

                    InformationType.PERCENTAGE -> {
                        // Cada info dentro de "(mes/ano anterior)"
                        for (info in snapshot.children) {
                            // Valores dentro de info01, info02... de "(mes/ano anterior)"
                            for (infoChild in info.children) {
                                _header = if (infoChild.key == "indicador")
                                    infoChild.getValue(String::class.java) else _header

                                if (infoChild.key != "infos") {
                                    informationData.add(information {
                                        this.header = _header!!
                                    })
                                } else {
                                    // Cada filho dentro de "infos"
                                    for (infosChild in infoChild.children) {
                                        // valores dentro de info01, info02... de "infos"
                                        for (subInfosChild in infosChild.children) {
                                            _header = if (subInfosChild.key == "indicador")
                                                subInfosChild.getValue(String::class.java) else _header

                                            _percentage = if (subInfosChild.key == "porcentagem")
                                                subInfosChild.getValue(String::class.java) else _percentage
                                        }

                                        subInformationData.add(information {
                                            this.header = _header!!
                                            this.percentage = _percentage!!
                                        })
                                    }

                                    // Adicionando as informações de "infos" para um índice do "pai"
                                    subInformationsParent.add(subInformationData.toMutableList())

                                    // Limpando após adicioná-la ao "pai"
                                    subInformationData.clear()
                                }
                            }
                        }
                    }
                }

                callback.invoke(informationData, subInformationsParent, informationType, mReference)
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