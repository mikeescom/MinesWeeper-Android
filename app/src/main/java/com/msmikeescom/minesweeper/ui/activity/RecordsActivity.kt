package com.msmikeescom.minesweeper.ui.activity

import android.os.Bundle
import com.msmikeescom.minesweeper.R

class RecordsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout(R.layout.activity_records, "RECORDS", "", true)
    }
}