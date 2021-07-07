package br.org.aldeiasinfantis.dashboard.data.remote

import android.app.Service
import android.util.Log
import br.org.aldeiasinfantis.dashboard.data.model.*
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import br.org.aldeiasinfantis.dashboard.ui.information.add.AddItemViewModel
import com.google.firebase.database.*

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
                            info.key?.let { key -> infoText?.uid = key }

                            infoText?.let { itInfoText ->
                                val fullPath = info.ref.root.toString()
                                val path = info.ref.toString().substring(fullPath.length)
                                itInfoText.path = path
                            }

                            infoText?.let { informationData.add(it) }
                        }
                    }

                    InformationType.VALUE -> {
                        for (info in snapshot.children) {
                            val infoValue: Information? = info.getValue(Information::class.java)
                            info.key?.let { key -> infoValue?.uid = key }

                            infoValue?.let { itInfoValue ->
                                val fullPath = info.ref.root.toString()
                                val path = info.ref.toString().substring(fullPath.length)
                                itInfoValue.path = path
                            }

                            infoValue?.let { informationData.add(it) }
                        }
                    }

                    InformationType.PERCENTAGE -> {
                        // Each info within "previous month/year"
                        for (info in snapshot.children) {
                            for (infoChild in info.children) {
                                if (infoChild.key == Global.DatabaseNames.INFORMATION_HEADER) {
                                    val infoPercentage: Information? = info.getValue(Information::class.java)

                                    infoChild.key?.let { key -> infoPercentage?.uid = key }

                                    infoPercentage?.let { itInfoPercentage ->
                                        val fullPath = info.ref.root.toString()
                                        val path = info.ref.toString().substring(fullPath.length)
                                        itInfoPercentage.path = path
                                    }

                                    infoPercentage?.let { informationData.add(it) }
                                } else {
                                    // Each child inside "infos"
                                    for (infosChild in infoChild.children) {
                                        val subInfoPercentage: Information? = infosChild.getValue(
                                            Information::class.java
                                        )

                                        infosChild.key?.let { key -> subInfoPercentage?.uid = key }

                                        subInfoPercentage?.let { itInfoPercentage ->
                                            val fullPath = info.ref.root.toString()
                                            val path = info.ref.toString().substring(fullPath.length)
                                            itInfoPercentage.path = path
                                        }

                                        subInfoPercentage?.let { subInformationData.add(it) }
                                    }

                                    // Adding the "sub information" to parent index
                                    subInformationParent.add(subInformationData.toMutableList())

                                    // Cleaning up after adding it to the parent
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

    override fun deleteItem(
        path: String,
        callback: (result: ServiceResult) -> Unit
    ) {
        val targetReference = Singleton.DATABASE.getReference(path)

        targetReference.removeValue()
            .addOnSuccessListener { callback(ServiceResult.Success) }
            .addOnFailureListener { e ->
                when (e.message) {
                    "Firebase Database error: Permission denied" ->
                        callback(ServiceResult.Error(ErrorType.PERMISSION_DENIED))

                    else ->
                        callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                }
            }
    }

    override fun addValueItem(
        referenceToAdd: DatabaseReference,
        header: String,
        value: String,
        competence: String,
        callback: (result: ServiceResult) -> Unit
    ) {
        val key = referenceToAdd.push().key

        key?.let { uid ->
            with (referenceToAdd.child(uid)) {
                child(Global.DatabaseNames.INFORMATION_HEADER).setValue(header)
                child(Global.DatabaseNames.INFORMATION_VALUE).setValue(value.toInt())
                child(Global.DatabaseNames.INFORMATION_COMPETENCE).setValue(competence)
            }
                .addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> callback(ServiceResult.Success)

                        false -> {
                            when (task.exception?.message) {
                                "Firebase Database error: Permission denied" ->
                                    callback(ServiceResult.Error(ErrorType.PERMISSION_DENIED))

                                else ->
                                    callback(ServiceResult.Error(ErrorType.SERVER_ERROR))
                            }
                        }
                    }
                }
        }
            ?: callback(ServiceResult.Error(ErrorType.SERVER_ERROR))
    }
}