package dan.com;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NoteDao
{
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM tbl_note")
    void deleteAllNotes();

    @Query("SELECT * FROM tbl_note ORDER BY priority")
    LiveData<List<Note>> getAllNotes();
}