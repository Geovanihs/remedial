package com.ut.login.Activities;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ut.login.Activities.Adapters.CoordinatesListAdapter;
import com.ut.login.Activities.Classes.Coordinates;
import com.ut.login.R;

import java.util.ArrayList;

public class ShowList extends AppCompatActivity {

    ListView listView;

    FirebaseFirestore db;

    String TAG = "FirebaseList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listviewCoordinates);

        loadCoordinates();
    }

    private void loadCoordinates() {
        db.collection("Coordinates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList <Coordinates> arrayList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " = " + String.valueOf(document.get("Latitud")));
                                //document.getDate("date");
                                arrayList.add(new Coordinates(
                                        document.getId(),
                                        document.getDate("date"),
                                        Float.parseFloat(String.valueOf(document.get("Latitud"))),
                                        Float.parseFloat(String.valueOf(document.get("Longitud")))
                                ));
                            }

                            if (arrayList.size() > 0){
                                CoordinatesListAdapter coordinatesListAdapter =
                                        new CoordinatesListAdapter(
                                                arrayList, ShowList.this, listView );
                                listView.setAdapter(coordinatesListAdapter);
                            }



                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}