package com.example.softwaresecurityfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SharedNotesAdapter extends RecyclerView.Adapter<SharedNotesAdapter.SharedNoteViewHolder>{

    private Context context;
    private List<Note> noteList;

    public SharedNotesAdapter(Context context, List<Note> noteList){
        this.context=context;
        this.noteList=noteList;
    }

    @NonNull
    @Override
    public SharedNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_shared_view,parent,false);
        return new SharedNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedNoteViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class SharedNoteViewHolder  extends RecyclerView.ViewHolder {

        public SharedNoteViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }


}
