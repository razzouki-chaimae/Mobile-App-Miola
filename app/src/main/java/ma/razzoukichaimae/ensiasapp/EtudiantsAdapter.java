package ma.razzoukichaimae.ensiasapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import model.Etudiant;

public class EtudiantsAdapter extends RecyclerView.Adapter<EtudiantsAdapter.ViewHolder>{

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

    public EtudiantsAdapter(Context context, List<Etudiant> listEtudiants) {
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
    public EtudiantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.etudiant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantsAdapter.ViewHolder holder, int position) {
        // getting our instance from Firebase Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Etudiant etudiant = listEtudiants.get(position);
        holder.fullNameEtudiant.setText(etudiant.getFullName());
        holder.emailEtudiant.setText(etudiant.getEmail());
        holder.studentItemOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.studentItemOption);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.item_option_menu, popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
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

                    case R.id.edititem:
                        final Dialog dialog = new Dialog(context);
                        //We have added a title in the custom layout. So let's disable the default title.
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
                        dialog.setCancelable(true);
                        //Mention the name of the layout of your custom dialog.
                        dialog.setContentView(R.layout.edit_student);

                        //Initializing the views of the dialog.
                        final EditText nom = dialog.findViewById(R.id.studentName);
                        final EditText email = dialog.findViewById(R.id.studentEmail);
                        final EditText pwd = dialog.findViewById(R.id.studentPassword);
                        final ConstraintLayout modifier = dialog.findViewById(R.id.modifyStudent);

                        //set edit texts with the old data
                        nom.setText(etudiant.getFullName());
                        email.setText(etudiant.getEmail());
                        pwd.setText(etudiant.getPassword());

                        modifier.setOnClickListener(view1 -> {
                            //get the new data
                            String name = nom.getText().toString();
                            String emailText = email.getText().toString();
                            String password = pwd.getText().toString();

                            //update professor object data
                            etudiant.setFullName(name);
                            etudiant.setEmail(emailText);
                            etudiant.setPassword(password);

                            // we send data to firebase with specific document id.
                            // below line is use to get the collection of our Firebase Firestore.
                            db.collection("Etudiant").
                                    // below line is use toset the id of
                                    // document where we have to perform
                                    // update operation.
                                            document(etudiant.getID()).

                                    // after setting our document id we are
                                    // passing our whole object class to it.
                                            set(etudiant).

                                    // after passing our object class we are
                                    // calling a method for on success listener.
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // on successful completion of this process
                                            // we are displaying the toast message.
                                            Toast.makeText(context, "Modifications effectuées avec succés.", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                // inside on failure method we are
                                // displaying a failure message.
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Modification échouées.", Toast.LENGTH_LONG).show();
                                }
                            });

                            dialog.dismiss();
                        });

                        dialog.show();
                        break;
                    case R.id.deleteitem:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setMessage("Est-ce que vous voulez vraiment supprimer l'étudiant(e) : "+etudiant.getFullName()+" ?")
                                .setCancelable(false)
                                .setPositiveButton("Oui", (dialogInterface, i) -> {
                                    dialogInterface.cancel();

                                    // below line is for getting the collection
                                    // where we are storing our courses.
                                    db.collection("Etudiant").
                                            // after that we are getting the document
                                            // which we have to delete.
                                                    document(etudiant.getID()).

                                            // after passing the document id we are calling
                                            // delete method to delete this document.
                                                    delete().
                                            // after deleting call on complete listener
                                            // method to delete this data.
                                                    addOnCompleteListener(task -> {
                                                        // inside on complete method we are checking
                                                        // if the task is success or not.
                                                        if (task.isSuccessful()) {
                                                            // this method is called when the task is success
                                                            // after deleting we are starting our MainActivity.
                                                            Toast.makeText(context, "l'étudiant a été bien supprimé.", Toast.LENGTH_SHORT).show();

                                                            //update recyclerview
                                                            listEtudiants.remove(position);
                                                            notifyItemRemoved(position);
                                                            notifyItemRangeChanged(position,listEtudiants.size());
                                                        } else {
                                                            // if the delete operation is failed
                                                            // we are displaying a toast message.
                                                            Toast.makeText(context, "Suppression échouée. ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                })

                                .setNegativeButton("Non", (dialogInterface, i) -> dialogInterface.cancel());
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
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
