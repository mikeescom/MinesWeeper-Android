package com.msmikeescom.minesweeper.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import java.text.SimpleDateFormat
import java.util.*

class PersonalRecordsAdapter (private var dataSet: List<LocalRecordItem>): RecyclerView.Adapter<PersonalRecordsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recordNumber: TextView
        val recordDifficulty: TextView
        val recordTime: TextView
        val numberOfMines: TextView
        val fieldSize: TextView
        val recordDate: TextView

        init {
            recordNumber = view.findViewById(R.id.record_number)
            recordDifficulty = view.findViewById(R.id.record_difficulty)
            recordTime = view.findViewById(R.id.record_time)
            numberOfMines = view.findViewById(R.id.number_of_mines)
            fieldSize = view.findViewById(R.id.field_size)
            recordDate = view.findViewById(R.id.record_date)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.personal_record_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.recordNumber.text = "${position.plus(1)}.-"
        viewHolder.recordDifficulty.text = "${String.format("%.2f", dataSet[position].numberOfMines.toDouble() /
                dataSet[position].fieldSize.toDouble())} mines/sqr."
        viewHolder.recordTime.text = "${dataSet[position].timeInSeconds} secs."
        viewHolder.numberOfMines.text = "${dataSet[position].numberOfMines} mines"
        viewHolder.fieldSize.text = "${dataSet[position].fieldSize} sqr."
        viewHolder.recordDate.text = getDateTime(dataSet[position].timeStamp.toString())
    }

    override fun getItemCount() = dataSet.size

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}