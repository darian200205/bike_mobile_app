package com.example.myapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {

    int k;

    {
        k = 0;
    }

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private FirebaseFirestore fStore;
    private DatabaseReference GroupRef;
    private FirebaseAuth fAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);






        fAuth = FirebaseAuth.getInstance();

        GroupRef = FirebaseDatabase.getInstance().getReference().child("GROUPIES");

        if(fAuth.getCurrentUser() != null){
            String userid = fAuth.getCurrentUser().getUid();
            GroupRef = FirebaseDatabase.getInstance().getReference().child("GROUPIES").child(userid);

        }

        InitializeFields();

        displayGroups();


        return groupFragmentView;


    }
    private void displayGroups() {

        GroupRef
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
               Iterator iterator = dataSnapshot.getChildren().iterator();

               while(iterator.hasNext()){
                   set.add(((DataSnapshot)iterator.next()).getKey());
               }
                Log.i("sssssssssss", set.toString());
                arrayAdapter.clear();
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.addAll(list_of_groups);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void InitializeFields(){

        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        list_view.setAdapter(arrayAdapter);

    }



}