package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import com.msmikeescom.minesweeper.ui.activity.GameBoardActivity
import com.msmikeescom.minesweeper.ui.activity.IGameBoardUIListener
import com.msmikeescom.minesweeper.ui.adapter.PersonalRecordsAdapter
import com.msmikeescom.minesweeper.viewmodel.LogInViewModel

class PersonalRecordsFragment : Fragment() {

    companion object {
        private const val TAG = "PersonalRecordsFragment"
    }

    private lateinit var logInViewModel: LogInViewModel
    private lateinit var listener: IGameBoardUIListener
    private val gameBoardActivity: GameBoardActivity
        get() = activity as GameBoardActivity
    private lateinit var recordsRecyclerview: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = gameBoardActivity

        logInViewModel = ViewModelProvider(gameBoardActivity)[LogInViewModel::class.java]
        logInViewModel.fetchAllRecords()

        bindViews(view)

        logInViewModel.allRecordItems.observe(viewLifecycleOwner) { recordItemList ->
            val sortedList = recordItemList
                .sortedWith(compareByDescending<LocalRecordItem> { it.numberOfMines.toDouble() / it.fieldSize.toDouble() }
                .thenBy {it.timeInSeconds})
            initRecyclerView(sortedList)
        }
    }

    private fun bindViews(view: View) {
        recordsRecyclerview = view.findViewById(R.id.records_recyclerview)
    }

    private fun initRecyclerView(dataSet: List<LocalRecordItem>) {
        layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recordsRecyclerview.layoutManager = layoutManager
        recordsRecyclerview.adapter = PersonalRecordsAdapter(dataSet)
    }
}