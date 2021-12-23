package com.ndzindo.noteapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: ViewModel
    private lateinit var buttonAddNote: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteViewModel = ViewModelProviders.of(this)[ViewModel::class.java]

        val note: Note = Note(null, "test", "testde", 2)
        noteViewModel.insert(note)

        var launchAddNoteActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val title = data?.getStringExtra(AddEditNoteActivity.EXTRA_TITLE).toString()
                    val description =
                        data?.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION).toString()
                    val priority = data?.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1)

                    val note = Note(null, title, description, priority)
                    noteViewModel.insert(note)

                    Toast.makeText(this, "Note added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Note not added!", Toast.LENGTH_SHORT).show()
                }
            }

        var launchEditNoteActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val id = data?.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1)
                    val title = data?.getStringExtra(AddEditNoteActivity.EXTRA_TITLE).toString()
                    val description =
                        data?.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION).toString()
                    val priority = data?.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1)

                    if (id == -1) {
                        Toast.makeText(this, "Note not edited! -1", Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult

                    }

                    val note = Note(id, title, description, priority)
                    noteViewModel.update(note)

                    Toast.makeText(this, "Note edited!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Note not edited!", Toast.LENGTH_SHORT).show()
                }
            }

        buttonAddNote = findViewById(R.id.button_add_note)
        buttonAddNote.setOnClickListener {
            var intent = Intent(this, AddEditNoteActivity::class.java)
            launchAddNoteActivity.launch(intent)
        }

        var recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        var noteAdapter: NoteAdapter = NoteAdapter()
        recyclerView.adapter = noteAdapter
        noteAdapter.onItemClick = { note ->

            var intent = Intent(this, AddEditNoteActivity::class.java)

            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.id)
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.title)
            intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.description)
            intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.priority)

            launchEditNoteActivity.launch(intent)

        }

        noteViewModel.getAllNotes()?.observe(this, { notes ->
            noteAdapter.differ.submitList(notes)
        })

        val helper = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.adapterPosition))
                Toast.makeText(this@MainActivity, "test", Toast.LENGTH_SHORT).show()
            }
        }

        ItemTouchHelper(helper).apply { attachToRecyclerView(recyclerView) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                noteViewModel.deleteAll()
                Toast.makeText(this, "All notes deleted.", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}