package ma.razzoukichaimae.ensiasapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.Etudiant;
import model.Professeur;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{//represents professeur_item

        TextView fullNameProf, departemetProf, professorItemOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameProf = itemView.findViewById(R.id.fullnameprof);
            departemetProf = itemView.findViewById(R.id.departementprof);
            professorItemOption = itemView.findViewById(R.id.professoritemoption);
        }
    }

    private Context context;
    private List<Professeur> profs;

    public RecyclerViewAdapter(Context context, List<Professeur> profs){
        this.context = context;
        this.profs = profs;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<Professeur> filterllist) {
        // below line is to add our filtered
        // list in our students list.
        profs = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//define professeur_item
        View view = LayoutInflater.from(context).inflate(R.layout.professeur_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {// get data for professeur_item
        // getting our instance from Firebase Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Professeur professeur = profs.get(position);

        holder.fullNameProf.setText(professeur.getFullName());
        holder.departemetProf.setText(professeur.getDepartement());

        holder.professorItemOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.professorItemOption);
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
                                new String[] { professeur.getEmail() });

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
                        dialog.setContentView(R.layout.edit_prof);

                        //Initializing the views of the dialog.
                        final EditText nom = dialog.findViewById(R.id.fullName);
                        final EditText departementList = dialog.findViewById(R.id.departementprof);
                        final EditText email = dialog.findViewById(R.id.email);
                        final EditText pwd = dialog.findViewById(R.id.password);
                        final ConstraintLayout modifier = dialog.findViewById(R.id.modifierprof);

                        //set edit texts with the old data
                        nom.setText(professeur.getFullName());
                        departementList.setText(professeur.getDepartement());
                        email.setText(professeur.getEmail());
                        pwd.setText(professeur.getPassword());

                        modifier.setOnClickListener(view1 -> {
                            //get the new data
                            String name = nom.getText().toString();
                            String departement = departementList.getText().toString();
                            String emailText = email.getText().toString();
                            String password = pwd.getText().toString();

                            //update professor object data
                            professeur.setFullName(name);
                            professeur.setDepartement(departement);
                            professeur.setEmail(emailText);
                            professeur.setPassword(password);

                            // we send data to firebase with specific document id.
                            // below line is use to get the collection of our Firebase Firestore.
                            db.collection("Professeur").
                                    // below line is use toset the id of
                                    // document where we have to perform
                                    // update operation.
                                            document(professeur.getId()).

                                    // after setting our document id we are
                                    // passing our whole object class to it.
                                            set(professeur).

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

                        builder.setMessage("Est-ce que vous voulez vraiment supprimer le professeur : "+professeur.getFullName()+" ?")
                                .setCancelable(false)
                                .setPositiveButton("Oui", (dialogInterface, i) -> {
                                    dialogInterface.cancel();

                                    // below line is for getting the collection
                                    // where we are storing our courses.
                                    db.collection("Professeur").
                                            // after that we are getting the document
                                            // which we have to delete.
                                                    document(professeur.getId()).

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
                                                    Toast.makeText(context, "le professeur a été bien supprimé.", Toast.LENGTH_SHORT).show();

                                                    //update recyclerview
                                                    profs.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position,profs.size());
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
        return profs.size();
    }
}
