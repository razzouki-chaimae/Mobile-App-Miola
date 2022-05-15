package ma.razzoukichaimae.ensiasapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import model.Module;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView intituleModule, profEnseignant, descriptionModule, moduleItemOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            intituleModule = itemView.findViewById(R.id.intitulemodule);
            profEnseignant = itemView.findViewById(R.id.profenseignant);
            moduleItemOption = itemView.findViewById(R.id.moduleitemoption);
            descriptionModule = itemView.findViewById(R.id.descriptionmodule);
        }
    }

    private Context context;
    private List<Module> listModules;

    public ModulesAdapter(Context context, List<Module> listModules) {
        this.context = context;
        this.listModules = listModules;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<Module> filterllist) {
        // below line is to add our filtered
        // list in our students list.
        listModules = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_item, parent, false);
        return new ModulesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulesAdapter.ViewHolder holder, int position) {
        // getting our instance from Firebase Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Module module = listModules.get(position);
        holder.intituleModule.setText(module.getIntitule());
        holder.profEnseignant.setText("Pr. "+module.getProfesseur());
        holder.descriptionModule.setText(module.getDescription());
        holder.moduleItemOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.moduleItemOption);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.module_option_menu, popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.editmodule:
                        final Dialog dialog = new Dialog(context);
                        //We have added a title in the custom layout. So let's disable the default title.
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
                        dialog.setCancelable(true);
                        //Mention the name of the layout of your custom dialog.
                        dialog.setContentView(R.layout.edit_module);

                        //Initializing the views of the dialog.
                        final EditText intitule = dialog.findViewById(R.id.moduleName);
                        final EditText prof = dialog.findViewById(R.id.enseignantmodule);
                        final EditText desc = dialog.findViewById(R.id.descmodule);
                        final ConstraintLayout modifier = dialog.findViewById(R.id.modifyModule);

                        //set edit texts with the old data
                        intitule.setText(module.getIntitule());
                        prof.setText(module.getProfesseur());
                        desc.setText(module.getDescription());

                        modifier.setOnClickListener(view1 -> {
                            //get the new data
                            String moduleName = intitule.getText().toString();
                            String enseignant = prof.getText().toString();
                            String description = desc.getText().toString();

                            //update professor object data
                            module.setIntitule(moduleName);
                            module.setProfesseur(enseignant);
                            module.setDescription(description);

                            // we send data to firebase with specific document id.
                            // below line is use to get the collection of our Firebase Firestore.
                            db.collection("Module").
                                    // below line is use toset the id of
                                    // document where we have to perform
                                    // update operation.
                                            document(module.getId()).

                                    // after setting our document id we are
                                    // passing our whole object class to it.
                                            set(module).

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
                    case R.id.deletemodule:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setMessage("Est-ce que vous voulez vraiment supprimer le module : "+module.getIntitule()+" ?")
                                .setCancelable(false)
                                .setPositiveButton("Oui", (dialogInterface, i) -> {
                                    dialogInterface.cancel();

                                    // below line is for getting the collection
                                    // where we are storing our courses.
                                    db.collection("Module").
                                            // after that we are getting the document
                                            // which we have to delete.
                                                    document(module.getId()).

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
                                                    Toast.makeText(context, "le module a été bien supprimé.", Toast.LENGTH_SHORT).show();

                                                    //update recyclerview
                                                    listModules.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position,listModules.size());
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
        return listModules.size();
    }

}
