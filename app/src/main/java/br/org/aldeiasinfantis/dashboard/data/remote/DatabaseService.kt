package br.org.aldeiasinfantis.dashboard.data.remote

import br.org.aldeiasinfantis.dashboard.data.model.*
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class DatabaseService : DatabaseRepository {

    override fun getLoggedUserInformation(
        callback: (User?, ServiceResult) -> Unit
    ) {
        Singleton.AUTH.currentUser?.let { currentUser ->
            val loggedUserReference = Singleton.DB_USERS_REF.child(currentUser.uid)

            Singleton.DB_ADMINS_REF
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(adminsSnapshot: DataSnapshot) {
                        val isUserAdmin = adminsSnapshot.child(currentUser.uid).exists()

                        loggedUserReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val user: User? = userSnapshot.getValue(User::class.java)
                                user?.isAdmin = isUserAdmin

                                callback(user, ServiceResult.Success)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                                return
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                        return
                    }
                })

            return
        }
    }

    override fun fetchDatabaseInformation(
        informationType: InformationType,
        targetReference: DatabaseReference,
        callback: (
            informationData: MutableList<Information>,
            subInformationParent: MutableList<MutableList<Information>>,
            result: ServiceResult
        ) -> Unit
    ) {
        targetReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val informationData = mutableListOf<Information>()

                val subInformationParent = mutableListOf<MutableList<Information>>()
                val subInformationData = mutableListOf<Information>()

                when (informationType) {
                    InformationType.TEXT -> {
                        for (info in snapshot.children) {
                            val infoText: Information? = info.getValue(Information::class.java)

                            infoText?.let { informationData.add(it) }
                        }
                    }

                    InformationType.VALUE -> {
                        for (info in snapshot.children) {
                            val infoValue: Information? = info.getValue(Information::class.java)
                            infoValue?.uid = info.key ?: ""

                            infoValue?.let { informationData.add(it) }
                        }
                    }

                    InformationType.PERCENTAGE -> {
                        // Cada info dentro de "mes/ano anterior"
                        for (info in snapshot.children) {
                            for (infoChild in info.children) {
                                if (infoChild.key == Global.DatabaseNames.INFORMATION_HEADER) {
                                    val infoPercentage: Information? =
                                        info.getValue(Information::class.java)
                                    infoPercentage?.let { informationData.add(it) }
                                } else {
                                    // Cada filho dentro de "infos"
                                    for (infosChild in infoChild.children) {
                                        val subInfoPercentage: Information? = infosChild.getValue(
                                            Information::class.java
                                        )
                                        subInfoPercentage?.let { subInformationData.add(it) }
                                    }

                                    // Adicionando as informações de "infos" para um índice do "pai"
                                    subInformationParent.add(subInformationData.toMutableList())

                                    // Limpando após adicioná-la ao "pai"
                                    subInformationData.clear()
                                }
                            }
                        }
                    }
                }

                callback.invoke(informationData, subInformationParent, ServiceResult.Success)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(
                    mutableListOf(),
                    mutableListOf(),
                    ServiceResult.Error(ErrorType.SERVER_ERROR)
                )
            }
        })
    }
}