package dan.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditNoteActivity extends AppCompatActivity
{
    public static final String EXTERA_ID = "AddNoteActivity_EXTERA_ID";
    public static final String EXTERA_TITLE = "AddNoteActivity_EXTERA_TITLE";
    public static final String EXTERA_DESCRIPTION = "AddNoteActivity_EXTERA_DESCRIPTION";
    public static final String EXTERA_PRIORITY = "AddNoteActivity_EXTERA_PRIORITY";

    private EditText edtTitle, edtDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        casting();
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTERA_ID))
        {
            setTitle("Edit note");
            edtTitle.setText(intent.getStringExtra(EXTERA_TITLE));
            edtDescription.setText(intent.getStringExtra(EXTERA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTERA_PRIORITY,1));
        }
        else
        {
            setTitle("Add note");
        }


    }

    private void casting()
    {
        edtTitle = findViewById(R.id.edt_new_title);
        edtDescription = findViewById(R.id.edt_new_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save_note:
            {
                saveNote();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote()
    {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().trim().isEmpty())
        {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTERA_TITLE, title);
        data.putExtra(EXTERA_DESCRIPTION, description);
        data.putExtra(EXTERA_PRIORITY, priority);

        int id=getIntent().getIntExtra(EXTERA_ID,-1);
        if(id != -1)
        {
            data.putExtra(EXTERA_ID,id);
        }

        setResult(RESULT_OK, data);
        finish();


    }
}
