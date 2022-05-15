package ma.razzoukichaimae.ensiasapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import model.Etudiant;

public class EtudiantsAdapterForProf extends RecyclerView.Adapter<EtudiantsAdapterForProf.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView fullNameEtudiant, emailEtudiant, studentItemOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameEtudiant = itemView.findViewById(R.id.fullnameetudiant);
            emailEtudiant = itemView.findViewById(R.id.emailetudiant);
            studentItemOption = itemView.findViewById(R.id.studentitemoption);
        }
    }

    private Context context;
    private List<Etudiant> listEtudiants;

    public EtudiantsAdapterForProf(Context context, List<Etudiant> listEtudiants) {
        this.context = context;
        this.listEtudiants = listEtudiants;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<Etudiant> filterllist) {
        // below line is to add our filtered
        // list in our students list.
        listEtudiants = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EtudiantsAdapterForProf.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.etudiant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantsAdapterForProf.ViewHolder holder, int position) {
        // getting our instance from Firebase Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Etudiant etudiant = listEtudiants.get(position);
        holder.fullNameEtudiant.setText(etudiant.getFullName());
        holder.emailEtudiant.setText(etudiant.getEmail());
        holder.studentItemOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.studentItemOption);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.option_menu_for_etud_in_prof, popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.studentAbsent:
                        if(menuItem.getTitle().equals("Marquer comme absent")){
                            // This is where I want to change the menu. Can be anywhere in your activity.
                            Toast.makeText(context, etudiant.getFullName()+" est absent", Toast.LENGTH_LONG);
                            menuItem.setTitle("Marquer comme present");
                        } else {
                            Toast.makeText(context, etudiant.getFullName()+" est present", Toast.LENGTH_LONG);
                            menuItem.setTitle("Marquer comme absent");
                        }
                    break;

                    case R.id.emailitem:
                        // define Intent object
                        // with action attribute as ACTION_SEND
                        Intent intent = new Intent(Intent.ACTION_SEND);

                        // add receiver to intent using putExtra function
                        intent.putExtra(Intent.EXTRA_EMAIL,
                                new String[] { etudiant.getEmail() });

                        // set type of intent
                        intent.setType("message/rfc822");

                        // startActivity with intent with chooser
                        // as Email client using createChooser function
                        context.startActivity(
                                Intent
                                        .createChooser(intent,
                                                "Choisir une app d'emailing :"));
                        break;
                }
                return false;
            });

        });
    }

    @Override
    public int getItemCount() {
        return listEtudiants.size();
    }

}
