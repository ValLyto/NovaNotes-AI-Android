package uws.ac.uk.noteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView textUserName;
    private Button buttonLogout;
    private FirebaseAuth mAuth;

    private EditText editText;
    private Button buttonSave;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList = new ArrayList<>();

    private static final String BASE_URL = "http://10.0.2.2/notes_api2/";
    private int currentEditId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        imageProfile = findViewById(R.id.imageProfile);
        textUserName = findViewById(R.id.textUserName);
        buttonLogout = findViewById(R.id.buttonLogout);

        String userName = currentUser.getDisplayName();
        if (userName == null || userName.isEmpty()) {
            userName = currentUser.getEmail();
        }
        textUserName.setText(userName);

        Uri photoUrl = currentUser.getPhotoUrl();
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).circleCrop().into(imageProfile);
        }

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        editText = findViewById(R.id.editTextNote);
        buttonSave = findViewById(R.id.buttonSave);
        recyclerView = findViewById(R.id.recyclerViewNotes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteAdapter(noteList, new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onEditClick(Note note) {
                editText.setText(note.getContent());
                currentEditId = note.getId();
                buttonSave.setText("Update Note");
            }

            @Override
            public void onDeleteClick(Note note) {
                deleteNote(note.getId());
            }
        });
        recyclerView.setAdapter(adapter);

        loadNotes();

        buttonSave.setOnClickListener(v -> {
            String noteText = editText.getText().toString();
            if (!noteText.trim().isEmpty()) {
                if (currentEditId == -1) {
                    saveNote(noteText);
                } else {
                    updateNote(currentEditId, noteText);
                }
            } else {
                Toast.makeText(MainActivity.this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });


        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });


        ImageButton buttonVoice = findViewById(R.id.buttonVoice);
        buttonVoice.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
            try {
                startActivityForResult(intent, 100);
            } catch (Exception e) {
                Toast.makeText(this, "Voice input not supported", Toast.LENGTH_SHORT).show();
            }
        });

        com.google.android.material.floatingactionbutton.FloatingActionButton fabAiAssistant = findViewById(R.id.fabAiAssistant);
        fabAiAssistant.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }


    private void filter(String text) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : noteList) {
            if (note.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(note);
            }
        }
        adapter.filterList(filteredList);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                editText.setText(result.get(0));
            }
        }
    }


    private void loadNotes() {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "get_notes.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();
                JSONArray array = new JSONArray(result.toString());
                noteList.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    noteList.add(new Note(obj.getInt("id"), obj.getString("content"), obj.getString("created_at")));
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveNote(String note) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "add_note.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String postData = "content=" + java.net.URLEncoder.encode(note, "UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> { editText.setText(""); loadNotes(); });
                }
                conn.disconnect();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void updateNote(int id, String noteContent) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "edit_note.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String postData = "id=" + id + "&content=" + java.net.URLEncoder.encode(noteContent, "UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        editText.setText("");
                        currentEditId = -1;
                        buttonSave.setText("Save Note");
                        loadNotes();
                    });
                }
                conn.disconnect();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void deleteNote(int id) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "delete_note.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String postData = "id=" + id;
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(this::loadNotes);
                }
                conn.disconnect();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}