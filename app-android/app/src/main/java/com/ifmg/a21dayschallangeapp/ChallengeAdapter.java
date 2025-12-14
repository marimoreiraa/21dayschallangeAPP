package com.ifmg.a21dayschallangeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // Import necessário
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private final List<Challenge> challengeList;


    public ChallengeAdapter(List<Challenge> challengeList) {
        this.challengeList = challengeList;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_desafio_diario, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);

        holder.tituloDesafioDiario.setText(challenge.getTitle());

        String iconName = challenge.getIconName();
        int iconId = holder.itemView.getContext().getResources().getIdentifier(
                iconName, "drawable", holder.itemView.getContext().getPackageName()
        );

        if (iconId != 0) {
            holder.iconDesafioDiario.setImageResource(iconId);
        } else {
            holder.iconDesafioDiario.setImageResource(R.drawable.ic_default);
        }

        holder.checkDiario.setEnabled(true);
        updateCheckIcon(holder.checkDiario, challenge.isCheckedToday());

        holder.checkDiario.setOnClickListener(v -> {
            boolean isCurrentlyChecked = challenge.isCheckedToday();
            boolean newState = !isCurrentlyChecked;

            holder.checkDiario.setEnabled(false);
            updateCheckIcon(holder.checkDiario, newState);

            if (holder.itemView.getContext() instanceof MainActivity) {
                ((MainActivity) holder.itemView.getContext()).performCheckin(challenge.getId(), holder.checkDiario, newState);
            }
        });
    }

    private void updateCheckIcon(ImageView checkIcon, boolean isChecked) {
        if (isChecked) {
            checkIcon.setImageResource(R.drawable.ic_checked);
        } else {
            checkIcon.setImageResource(R.drawable.ic_unchecked);
        }
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        public TextView tituloDesafioDiario;
        public ImageView iconDesafioDiario;
        public ImageView checkDiario;

        public ChallengeViewHolder(View itemView) {
            super(itemView);
            tituloDesafioDiario = itemView.findViewById(R.id.tituloDesafioDiario);
            iconDesafioDiario = itemView.findViewById(R.id.iconDesafioDiario);
            checkDiario = itemView.findViewById(R.id.checkDiario);        }
    }


}