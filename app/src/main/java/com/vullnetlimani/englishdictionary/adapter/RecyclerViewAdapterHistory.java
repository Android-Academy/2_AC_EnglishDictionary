package com.vullnetlimani.englishdictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vullnetlimani.englishdictionary.R;
import com.vullnetlimani.englishdictionary.model.History;

import java.util.ArrayList;

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.HistoryViewHolder> {

    private final Context context;
    private final ArrayList<History> histories;
    private ItemClickListener itemClickListener;

    public RecyclerViewAdapterHistory(Context context, ArrayList<History> histories, ItemClickListener itemClickListener) {
        this.context = context;
        this.histories = histories;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        final History history = histories.get(position);

        holder.enWord.setText(history.getEn_word());
        holder.enDef.setText(history.getEn_def());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClickListener(position, history.getEn_word(), holder.enWord);
            }
        });

    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(int pos, String word, View sharedView);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView enWord;
        TextView enDef;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            enWord = itemView.findViewById(R.id.enWord);
            enDef = itemView.findViewById(R.id.enDef);

        }
    }
}
