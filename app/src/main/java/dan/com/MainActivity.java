package dan.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "LOG_TAG";
    public static final int ADD_NOTE_ACTIVITY_REQUEST = 1;
    public static final int EDIT_NOTE_ACTIVITY_REQUEST = 2;

    private FloatingActionButton btnAddNote;

    private NoteViewModel noteViewModel;
    private NoteRepository noteRepository;

    private RecyclerView notesRecyclerView;
    private NoteAdapter adapter= new NoteAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        casting();

        setTitle("To-Do List");
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>()
        {
            @Override
            public void onChanged(List<Note> notes)
            {
                adapter.submitList(notes);
            }
        });
        btnAddNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(i, ADD_NOTE_ACTIVITY_REQUEST);
            }
        });


        //delete with swap
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(notesRecyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Note note)
            {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                i.putExtra(AddEditNoteActivity.EXTERA_ID, note.getId());
                i.putExtra(AddEditNoteActivity.EXTERA_TITLE, note.getTitle());
                i.putExtra(AddEditNoteActivity.EXTERA_DESCRIPTION, note.getDescription());
                i.putExtra(AddEditNoteActivity.EXTERA_PRIORITY, note.getPriority());
                startActivityForResult(i, EDIT_NOTE_ACTIVITY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_ACTIVITY_REQUEST && resultCode == RESULT_OK)
        {
            String title = data.getStringExtra(AddEditNoteActivity.EXTERA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTERA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTERA_PRIORITY, 1);
            //Adding note to database with viewModel
            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == EDIT_NOTE_ACTIVITY_REQUEST && resultCode == RESULT_OK)
        {
            int id=data.getIntExtra(AddEditNoteActivity.EXTERA_ID,-1);
            if(id==-1)
            {
                Toast.makeText(this, "Note cant be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditNoteActivity.EXTERA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTERA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTERA_PRIORITY, 1);
            //Updating note to database with viewModel
            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show();
        }

    }

    public void casting()
    {
        notesRecyclerView = findViewById(R.id.rv_notes);
        btnAddNote = findViewById(R.id.btn_add_note);


        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.deleteAllItemMenu:
            {
                noteViewModel.deleteAllNotes();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
