package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.Firebase
import com.msmikeescom.minesweeper.game.Board
import com.msmikeescom.minesweeper.game.RecordRanking
import com.msmikeescom.minesweeper.repository.MainRepository
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem
import kotlinx.coroutines.launch

class GameBoardViewModel : ViewModel() {

    companion object {
        private const val TAG = "GameBoardViewModel"
    }

    private lateinit var mainRepository: MainRepository

    /** Firebase Auth uid of the signed-in player; records are stored under it. */
    private var userId: String? = null

    /** The player's records, best first, updated as new ones are saved or synced. */
    val rankedRecords: LiveData<List<LocalRecordItem>> by lazy {
        mainRepository.observeAllRecords().map { RecordRanking.rank(it) }
    }

    /** Current round. Kept here so it survives configuration changes. */
    var board: Board? = null
        private set

    /** [SystemClock.elapsedRealtime] when the timer started, or null if not started yet. */
    var timerStartedAt: Long? = null
        private set

    private var timerStoppedAt: Long? = null

    val isTimerRunning: Boolean
        get() = timerStartedAt != null && timerStoppedAt == null

    fun newGame(width: Int, height: Int, numberOfMines: Int): Board {
        timerStartedAt = null
        timerStoppedAt = null
        return Board(width, height, numberOfMines).also { board = it }
    }

    fun startTimer() {
        if (timerStartedAt == null) timerStartedAt = SystemClock.elapsedRealtime()
    }

    fun stopTimer() {
        if (timerStartedAt != null && timerStoppedAt == null) timerStoppedAt = SystemClock.elapsedRealtime()
    }

    fun elapsedSeconds(): Int {
        val start = timerStartedAt ?: return 0
        val end = timerStoppedAt ?: SystemClock.elapsedRealtime()
        return ((end - start) / 1000).toInt()
    }

    /** Copies records from the server into the local database. */
    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val key = snapshot.key ?: return
            val remote = try {
                snapshot.getValue(RemoteRecordItem::class.java)
            } catch (e: DatabaseException) {
                Log.w(TAG, "onChildAdded: skipping malformed record $key", e)
                null
            } ?: return
            viewModelScope.launch {
                if (!mainRepository.loadAllItemIds().contains(key)) {
                    Log.d(TAG, "onChildAdded: adding record $key to local db")
                    mainRepository.insertRecord(LocalRecordItem.fromRemote(key, remote))
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit

        override fun onChildRemoved(snapshot: DataSnapshot) = Unit

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "onCancelled: $error")
        }
    }

    /**
     * Sets up the repository synchronously so it is ready before any fragment reads from it.
     * Safe to call again after the activity is recreated.
     */
    fun initViewModel(context: Context) {
        if (::mainRepository.isInitialized) return
        Log.d(TAG, "initViewModel")
        mainRepository = MainRepository.create(context)

        userId = Firebase.auth.currentUser?.uid
        userId?.let { mainRepository.addUserListener(it, childEventListener) }
            ?: Log.w(TAG, "initViewModel: no signed-in Firebase user, records won't sync")
    }

    override fun onCleared() {
        userId?.let { mainRepository.removeUserListener(it, childEventListener) }
        super.onCleared()
    }

    fun getNumberOfMines() = mainRepository.getNumberOfMines()

    fun getUserPhotoUrl() = mainRepository.getUserPhotoUrl()

    fun saveFieldSize(fieldSize: Int) {
        mainRepository.saveFieldSize(fieldSize)
    }

    fun saveNumberOfMines(numberOfMines: Int) {
        mainRepository.saveNumberOfMines(numberOfMines)
    }

    /** Saves a finished game. The server copy flows back into the local db via the listener. */
    fun setNewRecord(localRecordItem: LocalRecordItem) {
        val uid = userId ?: run {
            Log.w(TAG, "setNewRecord: no signed-in user, record not saved")
            return
        }
        val key = mainRepository.addNewRecord(uid, localRecordItem.getRemoteRecordItem())
        Log.d(TAG, "setNewRecord: saved as $key")
    }

}
