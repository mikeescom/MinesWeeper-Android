package com.msmikeescom.minesweeper.repository.remote.realtimedatabase

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem

class RealTimeDatabase {
    private val database = Firebase.database
    private val databaseReference = database.reference

    companion object {
        const val USERS_TABLE = "users"
        private const val TAG = "RealTimeDatabase"
        private var mInstance: RealTimeDatabase? = null

        fun instance(): RealTimeDatabase {
            if (mInstance == null) {
                mInstance = RealTimeDatabase()
                Log.d(TAG, "initialized")
            }

            return mInstance!!
        }
    }

    fun setUserListener(userId: String, listener: ChildEventListener) {
        databaseReference.child(USERS_TABLE).child(userId).addChildEventListener(listener)
    }

    fun setNewRecord(userId: String, itemId: String, remoteRecordItem: RemoteRecordItem) {
        databaseReference.child(USERS_TABLE).child(userId).child(itemId).setValue(remoteRecordItem)
    }
}