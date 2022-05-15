package ma.razzoukichaimae.ensiasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import model.Message;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView fullNameProfessor, dateMessage, corpsMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameProfessor = itemView.findViewById(R.id.fullnameprof);
            dateMessage = itemView.findViewById(R.id.datemessage);
            corpsMessage = itemView.findViewById(R.id.corpsmessage);
        }
    }

    private Context context;
    private LinkedList<Message> listMessages;

    public MessagesAdapter(Context context, LinkedList<Message> listMessages) {
        this.context = context;
        this.listMessages = listMessages;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = listMessages.get(position);
        holder.fullNameProfessor.setText(message.getProfesseur());
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        //String strDate = dateFormat.format(date);
        //holder.dateMessage.setText(dateFormat.format(message.getDate()));
        holder.dateMessage.setText(message.getDateMessage());
        holder.corpsMessage.setText(message.getCorps());
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }
}
