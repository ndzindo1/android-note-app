package com.ndzindo.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var numberPickerPriority: NumberPicker

    companion object {
        const val EXTRA_TITLE = "com.ndzindo.noteapp.EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "com.ndzindo.noteapp.EXTRA_DESCRIPTION"
        const val EXTRA_PRIORITY = "com.ndzindo.noteapp.EXTRA_PRIORITY"
        const val EXTRA_ID = "com.ndzindo.noteapp.EXTRA_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        numberPickerPriority = findViewById(R.id.number_picker_priority)

        numberPickerPriority.minValue = 1
        numberPickerPriority.maxValue = 10

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        if(intent.hasExtra(EXTRA_ID)){
            title = "Edit note"

            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE))
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            numberPickerPriority.value = intent.getIntExtra(EXTRA_PRIORITY, 1)
        }
        else {
            title = "Add note"
        }
    }

    private fun saveNote() {
        val title = editTextTitle.text
        val description = editTextDescription.text
        val priority = numberPickerPriority.value

        if(title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Title and description cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        var data = Intent()
        data.putExtra(EXTRA_TITLE, title.toString())
        data.putExtra(EXTRA_DESCRIPTION, description.toString())
        data.putExtra(EXTRA_PRIORITY, priority)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }
        setResult(RESULT_OK, data)
        finish()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}