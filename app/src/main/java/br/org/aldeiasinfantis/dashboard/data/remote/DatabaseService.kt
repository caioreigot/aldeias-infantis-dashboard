package br.org.aldeiasinfantis.dashboard.data.remote

import android.util.Log
import android.view.View
import android.widget.EditText
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.*
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class DatabaseService : DatabaseRepository {

    override suspend fun getLoggedUserInformation(
        callback: (User?, ServiceResult) -> Unit
    ) {
        withTimeout(Global.GET_USER_INFO_TIME_OUT_IN_MILLIS) {
            try {
                Singleton.AUTH.currentUser?.let { currentUser ->
                    Singleton.DB_ADMINS_REF
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(adminsSnapshot: DataSnapshot) {
                                val isUserAdmin = adminsSnapshot.child(currentUser.uid).exists()

                                getUserObject(currentUser) { user, result ->
                                    val resultObject =
                                        if (result is ServiceResult.Error)
                                            ServiceResult.Error(result.errorType)
                                        else
                                            ServiceResult.Success

                                    user?.isAdmin = isUserAdmin
                                    callback(user, resultObject)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                                return
                            }
                        })
                }
                    ?: callback(null, ServiceResult.Error(ErrorType.NETWORK_EXCEPTION))
            } catch (e: Exception) {
                Log.e("Database Exception", "getLoggedUserInformation: $e")

                if (e is TimeoutCancellationException)
                    callback(null, ServiceResult.Error(ErrorType.NETWORK_EXCEPTION))
            }
        }
    }

    private fun getUserObject(
        currentUser: FirebaseUser,
        callback: (User?, ServiceResult) -> Unit
    ) {
        val loggedUserReference = Singleton.DB_USERS_REF.child(currentUser.uid)

        loggedUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val user = userSnapshot.getValue(User::class.java)
                callback(user, ServiceResult.Success)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Exception", "onCancelled triggered: ${error.message}")
                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
            }
        })
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
                            if (info.key == Global.DatabaseNames.PARENT_INDICATOR_HEADER)
                                continue

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
                            if (info.key == Global.DatabaseNames.PARENT_INDICATOR_HEADER ||
                                info.key == Global.DatabaseNames.INDICADORES_GERAIS_COMPARATIVO)
                            {
                                continue
                            }

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
                Log.e("Database Exception", "Exception: $e")

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

    override fun addPercentageItem(
        referenceToAdd: DatabaseReference,
        header: String,
        subViews: MutableList<View>,
        callback: (result: ServiceResult) -> Unit
    ) {
        val key = referenceToAdd.push().key

        key?.let { uid ->
            with (referenceToAdd.child(uid)) {
                child(Global.DatabaseNames.INFORMATION_HEADER).setValue(header)

                // Another "info" node (sub info)
                val infoRef = child(Global.DatabaseNames.INDICADORES_GERAIS_SUB_INFO)

                for (i in subViews.indices) {
                    val subKey = infoRef.push().key

                    subKey?.let { uid ->
                        with (infoRef.child(uid)) {
                            with (subViews[i]) {
                                val infoHeader = findViewById<EditText>(R.id.item_indicator).text.toString()
                                val infoPercentage = findViewById<EditText>(R.id.item_percentage).text.toString()

                                child(Global.DatabaseNames.INFORMATION_HEADER).setValue(infoHeader)
                                child(Global.DatabaseNames.INFORMATION_PERCENTAGE).setValue(infoPercentage)
                            }
                        }
                            .addOnCompleteListener { task ->
                                when (task.isSuccessful) {
                                    true -> callback(ServiceResult.Success)

                                    false -> {
                                        Log.e("Database Exception", "Exception: ${task.exception}")

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
                }
            }
        } ?: callback(ServiceResult.Error(ErrorType.SERVER_ERROR))
    }

    override fun editValueItem(
        path: String,
        header: String,
        value: Int,
        competence: String,
        callback: (result: ServiceResult) -> Unit
    ) {
        val targetReference = Singleton.DATABASE.getReference(path)

        with (targetReference) {
            child(Global.DatabaseNames.INFORMATION_HEADER).setValue(header)
            child(Global.DatabaseNames.INFORMATION_VALUE).setValue(value)
            child(Global.DatabaseNames.INFORMATION_COMPETENCE).setValue(competence)
        }
            .addOnSuccessListener { callback(ServiceResult.Success) }
            .addOnFailureListener { e ->
                Log.e("Database Exception", "Exception: $e")

                when (e.message) {
                    "Firebase Database error: Permission denied" ->
                        callback(ServiceResult.Error(ErrorType.PERMISSION_DENIED))

                    else ->
                        callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                }
            }
    }

    override fun editPercentageItem(
        path: String,
        header: String,
        subInfo: MutableList<Information>,
        callback: (result: ServiceResult) -> Unit
    ) {

        val targetReference = Singleton.DATABASE.getReference(path)

        with (targetReference) {
            child(Global.DatabaseNames.INFORMATION_HEADER).setValue(header)

            // Another "info" node (sub info)
            val infoRef = child(Global.DatabaseNames.INDICADORES_GERAIS_SUB_INFO)
            infoRef.removeValue()

            for (i in subInfo.indices) {
                val subKey = infoRef.push().key

                subKey?.let { ref ->
                    with (infoRef.child(ref)) {
                        with (subInfo[i]) {
                            child(Global.DatabaseNames.INFORMATION_HEADER).setValue(subInfo[i].header)
                            child(Global.DatabaseNames.INFORMATION_PERCENTAGE).setValue(subInfo[i].percentage)
                        }
                    }
                        .addOnCompleteListener { task ->
                            when (task.isSuccessful) {
                                true -> callback(ServiceResult.Success)

                                false -> {
                                    Log.e("Database Exception", "Exception: ${task.exception}")

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
            }
        }
    }

    override fun fetchGeneralIndicatorsComparative(
        reference: DatabaseReference,
        callback: (comparative: String, result: ServiceResult) -> Unit
    ) {
        reference
            .child(Global.DatabaseNames.INDICADORES_GERAIS_COMPARATIVO)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.value.toString(), ServiceResult.Success)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback("", ServiceResult.Error(ErrorType.SERVER_ERROR))
                }
            })
    }

    override fun fetchInformationTitle(
        reference: DatabaseReference,
        callback: (title: String, result: ServiceResult) -> Unit
    ) {
        reference
            .child(Global.DatabaseNames.PARENT_INDICATOR_HEADER)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.value.toString(), ServiceResult.Success)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback("", ServiceResult.Error(ErrorType.SERVER_ERROR))
                }
            })
    }
}