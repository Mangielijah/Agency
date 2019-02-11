package com.omenacle.agency.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omenacle.agency.DataClasses.Ticket;
import com.omenacle.agency.R;

import java.util.ArrayList;

public class ValidatedListAdapter extends RecyclerView.Adapter<ValidatedListAdapter.ViewHolder> {

    private ArrayList<Ticket> tickets;
    private RecyclerView recyclerView;
    private InfoAdapterInterface adapterInterface;
    Context ctx;

    public ValidatedListAdapter(RecyclerView recyclerView, ArrayList<Ticket> tickets, InfoAdapterInterface adapterInterface) {

        this.tickets = tickets;
        this.recyclerView = recyclerView;
        this.adapterInterface = adapterInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ctx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.validated_row_design, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.codeView.setText("Code: " + String.valueOf(tickets.get(i).getCode()));
        viewHolder.nameView.setText(tickets.get(i).getName());
        if(tickets.get(i).getId() == 1){
            viewHolder.idView.setText(ctx.getResources().getText(R.string.no_idcard));
        }else{
            viewHolder.idView.setText("ID: " + tickets.get(i).getId());
        }
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView codeView, nameView, idView;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            codeView = itemView.findViewById(R.id.code);
            nameView = itemView.findViewById(R.id.name);
            idView = itemView.findViewById(R.id.id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = recyclerView.getChildLayoutPosition(itemView);
                    adapterInterface.OnItemClicked(pos);;
                }
            });
        }
    }

    // Your interface to send data to your fragment
    public interface InfoAdapterInterface{
        void OnItemClicked(int pos);
    }
}
