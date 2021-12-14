package com.ut.login.Activities.Adapters;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.login.Activities.Classes.Coordinates;
import com.ut.login.Activities.ShowList;
import com.ut.login.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CoordinatesListAdapter implements ListAdapter {

    ArrayList<Coordinates> arrayList;
    ShowList showList;
    ListView listView;
    FirebaseFirestore db;
    String TAG = "FirebaseList";

    public CoordinatesListAdapter(ArrayList<Coordinates> arrayList, ShowList showList,ListView listView) {
        this.arrayList = arrayList;
        this.showList = showList;
        this.listView = listView;

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(showList);

            view = layoutInflater.inflate(R.layout.coordinate_list_item , null);

            TextView latitudeAndLongitude = view.findViewById(R.id.txtLatitudeAndLongitude);
            TextView date = view.findViewById(R.id.txtDate);


            latitudeAndLongitude.setText("" + arrayList.get(i).getLatitude() + ", " +
                    arrayList.get(i).getLongitude() );

            DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(showList);
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(showList);

            date.setText("" + dateFormat.format(arrayList.get(i).getDate()) + " " +
                    timeFormat.format(arrayList.get(i).getDate()));

            ImageButton delete = view.findViewById(R.id.btnDelete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("Coordinates").document(arrayList.get(i).getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(showList, "Se ha eliminado correctamente"
                                            , Toast.LENGTH_SHORT).show();
                                    arrayList.remove(i);
                                    if (arrayList.size() > 0){
                                        CoordinatesListAdapter coordinatesListAdapter =
                                                new CoordinatesListAdapter(
                                                        arrayList, showList, listView );
                                        listView.setAdapter(coordinatesListAdapter);
                                    }else {
                                        listView.invalidateViews();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(showList, "No se ha podido eliminar"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("google.navigation:q=" + arrayList.get(i).getLatitude()
                                        + ", " + arrayList.get(i).getLongitude()));
                                showList.startActivity(intent);
                            }catch (Exception e){
                                Toast.makeText(showList, "Ocurrio un error al obtener las coordenadas",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });

        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
