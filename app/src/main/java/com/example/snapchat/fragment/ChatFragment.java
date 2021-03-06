package com.example.snapchat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.snapchat.RecyclerViewFollow.FollowAdapter;
import com.example.snapchat.RecyclerViewFollow.FollowObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.example.snapchat.*;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText mInput;
    private FirebaseAuth auth;
    private static final String TAG = "FindUserActivity";
    private Set<FollowObject> finalResults= new HashSet<>();;
    private ArrayList<FollowObject> finalResultslist= new ArrayList<>();
    String userName;

    public static ChatFragment newInstance(){
        ChatFragment fragment=new ChatFragment();
        return fragment;
    }


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_chat,container,false);
        mInput = view.findViewById(R.id.input);
        ImageView mSearch = view.findViewById(R.id.search);
        auth = FirebaseAuth.getInstance();

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FollowAdapter(getDataSet(), getContext());
        mRecyclerView.setAdapter(mAdapter);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                listenForData();
            }
        });
        return view;
    }

    private void findUser() {


    }

    //This method is used to search emails based on the entered text
    private void listenForData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference("users").child(userId).child("name");

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Name: " + dataSnapshot.getValue().toString());
                userName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference emailDb = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = emailDb.orderByChild("name").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String user ="";
                //Gives uid of each users returned to us

                String uid = dataSnapshot.getRef().getKey();
                System.out.println("Uid is:"+uid);

                // A precautionary check as query will always return childs with email
                if(dataSnapshot.child("name").getValue() !=null){
                    user = dataSnapshot.child("name").getValue().toString();
                    Log.d(TAG, "onChildAdded name: "+user);
                }

                if(!user.equals(userName)){
                    FollowObject obj = new FollowObject(user,uid);
                    results.add(obj);
                    mAdapter.notifyDataSetChanged();

                }
            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This method clears the search result on each search done
    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0,size);
    }


    //Will return all the users in the recycler view
    private ArrayList<FollowObject> results = new ArrayList<>();

    //In the beginning of the search load up every single data
    private ArrayList<FollowObject> getDataSet() {
        if(results.size()!=0){
            clear();
        }
        listenForData();
        return results;
    }
}
