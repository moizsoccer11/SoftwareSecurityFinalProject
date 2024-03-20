package com.example.softwaresecurityfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{

    private Context context;
    private List<Note> noteList;

    public Adapter(Context context, List<Note> noteList){
        this.context=context;
        this.noteList=noteList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note note =noteList.get(position);
        holder.titleOutput.setText(note.getTitle());
        holder.descriptionOutput.setText(note.getDescription());
        holder.itemView.setBackgroundColor(Color.parseColor(note.getNoteColor().toString()));
        // Set the note for the ViewHolder
        holder.setNote(note);
        //When Click the Note itself
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //intent to go to EditNote page
                Intent intent = new Intent(context,EditNote.class);
                intent.putExtra("note_object", note);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {
        TextView titleOutput;
        TextView descriptionOutput;

        ImageView deleteBtn;

        ImageView shareNoteBtn;

        private Note note;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleOutput = itemView.findViewById(R.id.noteTitle);
            descriptionOutput = itemView.findViewById(R.id.noteDescription);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            shareNoteBtn = itemView.findViewById(R.id.shareBtn);

            //On Click Functions:
            deleteBtn.setOnClickListener(v -> openDeleteModal());
            shareNoteBtn.setOnClickListener(v -> {
                try {
                    openShareModal();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        public void setNote(Note note) {
            this.note = note;
        }
        private void openDeleteModal(){
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            View deleteModalView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.modal, null);
            // Find views in ModalView
            Button btnDelete = deleteModalView.findViewById(R.id.btnConfirmDelete);
            Button btnCancel= deleteModalView.findViewById(R.id.btnCancelDelete);
            //Create Dialog
            builder.setView(deleteModalView);
            AlertDialog dialog = builder.create(); // Create the dialog instance
            dialog.show();
            //onClick Functions
            btnDelete.setOnClickListener(v -> {
                // Get the ID of the note
                try{
                    long noteId = note.getId();
                    //Open Database to access
                    DatabaseServices dataSource = new DatabaseServices(context); // Initialize the data source
                    dataSource.open(); // Open the databaase
                    dataSource.deleteNote(noteId);
                    dataSource.close();
                    //Refresh list
                    // Remove the deleted note from the list
                    int position = noteList.indexOf(note);
                    noteList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    // Dismiss the dialog
                    dialog.dismiss();
                    //Make a toast that Bid Created
                    Toast.makeText(context,"Note has been deleted", Toast.LENGTH_SHORT).show();
                }catch (SQLException e) {
                    Log.e("YourTag", "Error message", e);
                }
            });
            btnCancel.setOnClickListener(v -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
        }
        private void openShareModal() throws Exception {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            View shareModalView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.sharemodal, null);
            // Find views in ModalView
            TextView sharedKey = shareModalView.findViewById(R.id.textViewShareToken);
            Button closeBtn = shareModalView.findViewById(R.id.btnClose);
            //Create Dialog
            builder.setView(shareModalView);
            AlertDialog dialog = builder.create(); // Create the dialog instance
            dialog.show();
            //Display Key of the note to user to share
            sharedKey.setText(note.getKey());
            //onClick Functions
            closeBtn.setOnClickListener(v -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
        }
    }
}
