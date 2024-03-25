package com.example.softwaresecurityfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class SharedNotesAdapter extends RecyclerView.Adapter<SharedNotesAdapter.SharedNoteViewHolder>{

    private Context context;
    private List<Note> noteList;

    private User user;

    public SharedNotesAdapter(Context context, List<Note> noteList, User user){
        this.context=context;
        this.noteList=noteList;
        this.user=user;
    }

    @NonNull
    @Override
    public SharedNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_shared_view,parent,false);
        return new SharedNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedNoteViewHolder holder, int position) {
        Note note =noteList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteDescription.setText("");
        holder.itemView.setBackgroundColor(Color.parseColor(note.getNoteColor().toString()));
        // Set the note for the ViewHolder
        holder.setNote(note);
    }
    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class SharedNoteViewHolder  extends RecyclerView.ViewHolder {

        TextView noteTitle;
        TextView noteDescription;

        ImageView viewBtn;
        private Note note;

        public SharedNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteDescription = itemView.findViewById(R.id.noteDescription);
            viewBtn = itemView.findViewById(R.id.viewBtn);

            //On Click Functions:
            viewBtn.setOnClickListener(v -> {
                try {
                    openViewNoteModal();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        public void setNote(Note note) {
            this.note = note;
        }

        private void openViewNoteModal() throws Exception {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            View sharenoteModalView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.sharednoteview, null);
            // Find views in ModalView
            Button closeBtn = sharenoteModalView.findViewById(R.id.btnClose);
            TextView title = sharenoteModalView.findViewById(R.id.textViewTitle);
            TextView desc = sharenoteModalView.findViewById(R.id.textViewDescription);
            ImageView image = sharenoteModalView.findViewById(R.id.image);
            //Create Dialog
            builder.setView(sharenoteModalView);
            AlertDialog dialog = builder.create(); // Create the dialog instance
            dialog.show();
            //Display note data
            title.setText(note.getTitle());
            desc.setText(note.getDescription());
            if(note.getImage() != null){
                byte[] imageBytes = note.getImage();
                Bitmap imageBitmap = bytesToBitmap(imageBytes);
                image.setImageBitmap(imageBitmap);
            }
            //onClick Functions
            closeBtn.setOnClickListener(v -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
        }
        public Bitmap bytesToBitmap(byte[] imageBytes) {
            if (imageBytes == null || imageBytes.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

    }


}
