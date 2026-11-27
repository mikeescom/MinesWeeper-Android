package com.msmikeescom.minesweeper.repository.remote.realtimedatabase

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseException
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Records are stored at `users/<Firebase Auth uid>/<push id>`. The database rules in
 * `database.rules.json` only let a signed-in user read and write their own node.
 */
@Singleton
class RealTimeDatabase @Inject constructor() {
    private val usersReference = Firebase.database.reference.child(USERS_TABLE)

    companion object {
        const val USERS_TABLE = "users"
        private const val TAG = "RealTimeDatabase"
    }

    fun addUserListener(userId: String, listener: ChildEventListener) {
        usersReference.child(userId).addChildEventListener(listener)
    }

    fun removeUserListener(userId: String, listener: ChildEventListener) {
        usersReference.child(userId).removeEventListener(listener)
    }

    /** Stores [remoteRecordItem] under a new unique key and returns that key. */
    fun addNewRecord(userId: String, remoteRecordItem: RemoteRecordItem): String? {
        val reference = usersReference.child(userId).push()
        reference.setValue(remoteRecordItem)
            .addOnFailureListener { Log.e(TAG, "addNewRecord failed", it) }
        return reference.key
    }

    /** Deletes every record stored for [userId]. */
    suspend fun deleteUserRecords(userId: String) {
        usersReference.child(userId).removeValue().await()
    }

    /**
     * Copies records saved by older app versions under the Google account id ([fromUserId]) to the
     * Firebase uid ([toUserId]). Keys are kept, so running it more than once is harmless. Fails
     * quietly once the database rules no longer allow reading the old node.
     */
    fun copyLegacyRecords(fromUserId: String, toUserId: String) {
        if (fromUserId == toUserId) return
        usersReference.child(fromUserId).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach { child ->
                    val key = child.key ?: return@forEach
                    val item = try {
                        child.getValue(RemoteRecordItem::class.java)
                    } catch (e: DatabaseException) {
                        Log.w(TAG, "Skipping malformed legacy record $key", e)
                        null
                    } ?: return@forEach
                    usersReference.child(toUserId).child(key).setValue(item)
                }
                Log.d(TAG, "Copied ${snapshot.childrenCount} legacy records")
            }
            .addOnFailureListener { Log.w(TAG, "Could not read legacy records", it) }
    }
}
