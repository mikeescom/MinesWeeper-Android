package com.msmikeescom.minesweeper.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.game.RecordRanking
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class PersonalRecordsAdapter : RecyclerView.Adapter<PersonalRecordsAdapter.ViewHolder>() {

    private var dataSet: List<LocalRecordItem> = emptyList()
    private val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recordNumber: TextView = view.findViewById(R.id.record_number)
        val recordDifficulty: TextView = view.findViewById(R.id.record_difficulty)
        val recordTime: TextView = view.findViewById(R.id.record_time)
        val numberOfMines: TextView = view.findViewById(R.id.number_of_mines)
        val fieldSize: TextView = view.findViewById(R.id.field_size)
        val recordDate: TextView = view.findViewById(R.id.record_date)
    }

    // The list is re-ranked on every change, so positions can all move.
    @SuppressLint("NotifyDataSetChanged")
    fun submit(records: List<LocalRecordItem>) {
        dataSet = records
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.personal_record_item, viewGroup, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.recordNumber.text = "${position + 1}.-"
        viewHolder.recordDifficulty.text = "${String.format(Locale.US, "%.2f", RecordRanking.density(item))} mines/sqr."
        viewHolder.recordTime.text = "${item.timeInSeconds} secs."
        viewHolder.numberOfMines.text = "${item.numberOfMines} mines"
        viewHolder.fieldSize.text = "${item.fieldSize} sqr."
        viewHolder.recordDate.text = dateFormat.format(Date(item.timeStamp))
    }

    override fun getItemCount() = dataSet.size
}
