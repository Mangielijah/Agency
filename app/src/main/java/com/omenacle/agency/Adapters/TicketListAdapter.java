package com.omenacle.agency.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.omenacle.agency.DataClasses.Route;
import com.omenacle.agency.DataClasses.Ticket;
import com.omenacle.agency.R;

import java.util.ArrayList;

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.ViewHolder> {

    private ArrayList<Ticket> tickets;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private InfoAdapterInterface adapterInterface;

    public TicketListAdapter(RecyclerView recyclerView, ArrayList<Ticket> tickets, DatabaseReference mDatabase, InfoAdapterInterface adapterInterface) {
        this.tickets = tickets;
        this.recyclerView = recyclerView;
        this.mDatabase = mDatabase;
        this.adapterInterface = adapterInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.validation_row_design, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.codeView.setText("Code: " + String.valueOf(tickets.get(i).getCode()));
        viewHolder.nameView.setText(tickets.get(i).getName());
        viewHolder.idView.setText("ID: " + tickets.get(i).getId());
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView codeView, nameView, idView;
        ImageButton validateButton;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            codeView = itemView.findViewById(R.id.code);
            nameView = itemView.findViewById(R.id.name);
            idView = itemView.findViewById(R.id.id);
            validateButton = itemView.findViewById(R.id.btnValidate);
            validateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = recyclerView.getChildLayoutPosition(itemView);
                    String key = String.valueOf(tickets.get(pos).getCode());
                    Ticket newTicket = tickets.get(pos);
                    newTicket.setStatus("u");
                    mDatabase.child("t").child(key).setValue(newTicket).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                adapterInterface.OnItemClicked(pos);
                            }
                        }
                    });
                }
            });
        }
    }

    // Your interface to send data to your fragment
    public interface InfoAdapterInterface{
        void OnItemClicked(int pos);
    }
}
