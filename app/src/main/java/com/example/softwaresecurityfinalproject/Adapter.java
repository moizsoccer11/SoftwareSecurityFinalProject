package com.example.softwaresecurityfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
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
        // Set the AuctionItem for the ViewHolder
        holder.setNote(note);
        //When Click the Note itself
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //intent to go to EditNote page
                Intent intent = new Intent(context,EditNote.class);
                intent.putExtra("note_id", note.getId());
                intent.putExtra("note_title", note.getTitle());
                intent.putExtra("note_description", note.getDescription());
                intent.putExtra("note_color", note.getNoteColor());
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

        private Note note;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleOutput = itemView.findViewById(R.id.noteTitle);
            descriptionOutput = itemView.findViewById(R.id.noteDescription);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(v -> openDeleteModal());
        }
        public void setNote(Note note) {
            this.note = note;
        }
        public void openDeleteModal(){
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
    }
}
