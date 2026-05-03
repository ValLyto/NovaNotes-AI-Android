package uws.ac.uk.noteapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onEditClick(Note note);
        void onDeleteClick(Note note);
    }

    public NoteAdapter(List<Note> noteList, OnNoteClickListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }


    public void filterList(List<Note> filteredList) {
        this.noteList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        holder.textContent.setText(note.getContent());


        holder.textDateTime.setText("Created: " + note.getCreatedAt());

        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(note));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(note));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        TextView textDateTime;
        Button buttonEdit;
        Button buttonDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            textContent = itemView.findViewById(R.id.textNoteContent);
            textDateTime = itemView.findViewById(R.id.textNoteDateTime);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}