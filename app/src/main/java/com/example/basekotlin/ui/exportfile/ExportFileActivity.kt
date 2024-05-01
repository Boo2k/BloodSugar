package com.example.basekotlin.ui.exportfile

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.Note
import com.example.basekotlin.data.history.History
import com.example.basekotlin.databinding.ActivityExportFileBinding
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class ExportFileActivity :
    BaseActivity<ActivityExportFileBinding>(ActivityExportFileBinding::inflate) {

    private var historyList: ArrayList<History> = ArrayList()

    private var dayFormatter: DateFormat? = null
    private var dateFormatter: DateFormat? = null

    private var uri: Uri? = null
    private var isSaved = false
    private var uri1 = ""
    private var path = ""

    private var toast: Toast? = null

    override fun initView() {
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getText(R.string.export_file)

        setUpFileExport()
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }

        binding.btnSave.tap { onSave() }

        binding.btnShare.tap { onShare(path) }
    }

    private fun onSave() {
        if (toast != null) toast!!.cancel()
        if (isSaved) {
            toast = Toast.makeText(this, getString(R.string.file_already_saved), Toast.LENGTH_SHORT)
        } else {
            toast = Toast.makeText(this, getString(R.string.save_successful), Toast.LENGTH_SHORT)
            isSaved = true
        }
        toast!!.show()
    }

    private fun onShare(path: String) {
        val file: File?
        if (path.contains("content://com.")) {
            Uri.parse(path).path?.let { File(it) }?.let { intentFile(it) }
        } else if (path.contains("content://")) {
            uri1 = path
            uri1 = "file://$path"
            file = Uri.parse(uri1).path?.let { File(it) }
            file?.let { intentFile(it) }
        } else {
            uri1 = "file://$path"
            file = Uri.parse(uri1).path?.let { File(it) }
            file?.let { intentFile(it) }
        }
    }

    private fun intentFile(file: File) {

        if (file.exists()) {
            val uri2 = FileProvider.getUriForFile(
                this, "${application.packageName}.fileprovider", file
            )
            val intent2 = Intent(Intent.ACTION_SEND)
            intent2.putExtra("android.intent.extra.SUBJECT", "Video Maker")
            intent2.type = "text/*"
            intent2.putExtra("android.intent.extra.STREAM", uri2)
            intent2.putExtra("android.intent.extra.TEXT", "csv")
            startActivity(Intent.createChooser(intent2, "Where to Share?"))
        }
    }

    @SuppressLint("InlinedApi", "SimpleDateFormat")
    private fun setUpFileExport() {
        historyList = ArrayList()
        historyList .addAll(DatabaseManager.getAllHistory())
        historyList.sortWith(Comparator { (_, _, _, _, timeDate): History, (_, _, _, _, timeDate1): History ->
            if (timeDate!! > timeDate1!!) {
                return@Comparator 1
            } else return@Comparator -1
        })

        dateFormatter = SimpleDateFormat("dd'th' MMM yy, hh:mm ")
        dayFormatter = SimpleDateFormat("dd-MM-yyyy_hh-mm")

        val csv = StringBuilder()
        val calendar = Calendar.getInstance()
        val date = Date()
        date.date = calendar[Calendar.DATE]
        var currentTime = ""
        dayFormatter?.let { currentTime = it.format(date) }

        csv.append(getString(R.string.csv_day))
        csv.append(getString(R.string.csv_time))
        csv.append(getString(R.string.csv_blood_sugar))
        csv.append(getString(R.string.csv_condition))
        csv.append(getString(R.string.csv_notes))
        csv.append(getString(R.string.csv_type))
        csv.append("\n")
        for (h: History in historyList) {
            csv.append(importRow(h))
            csv.append("\n")
        }
        val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = getString(R.string.app_name) + "_$currentTime.csv"
        val f = File(baseDir, fileName)
        val fileWriter: FileWriter
        try {
            fileWriter = FileWriter(f)
            fileWriter.write(csv.toString())
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.tvFileName.text = fileName
        binding.tvFileTime.text = (dateFormatter as SimpleDateFormat).format(date) ?: ""

        uri = FileProvider.getUriForFile(
            this, "${application.packageName}.fileprovider", f.absoluteFile
        )
        path = f.absolutePath
    }

    @SuppressLint("SimpleDateFormat")
    private fun importRow(h: History): String {
        val dateFormatter: DateFormat = SimpleDateFormat("d/MM/yyyy")
        val hourFormatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        val time = Date()
        time.time = h.timeDate!!
        val value = if (SPUtils.getBoolean(
                this, Utils.UNIT, true
            )
        ) h.valueInput?.times(18) else h.valueInput

        return dateFormatter.format(time) + "," + hourFormatter.format(time) + "," + value.toString() + "," + h.targetRange!!.condition!!.name + "," + "\"" + h.notes?.let {
            getAllNote(
                it
            )
        }?.joinToString(",") + "\"" + "," + calculateType(h)
    }

    private fun calculateType(history: History): String {
        return if (history.valueInput!! <= history.targetRange!!.low!!) {
            getString(R.string.low)
        } else if (history.valueInput!! <= history.targetRange!!.normal!!) {
            getString(R.string.normal)
        } else if (history.valueInput!! <= history.targetRange!!.preDiabetes!!) {
            getString(R.string.pre_diabetes)
        } else {
            getString(R.string.diabetes)
        }
    }

    private fun getAllNote(notes: List<Note>): List<String> {
        val note: MutableList<String> = ArrayList()
        for (n: Note in notes) {
            if (n.isSelected == true) {
                n.name?.let { note.add(it) }
            }
        }
        return note
    }

    override fun onBackPressed() {
        finishThisActivity()
    }

}