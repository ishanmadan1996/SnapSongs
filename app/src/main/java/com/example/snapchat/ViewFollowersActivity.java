package com.example.snapchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.snapchat.RecyclerViewFollow.FollowAdapter;
import com.example.snapchat.RecyclerViewFollow.FollowObject;
import com.example.snapchat.RecyclerViewFollow.FollowerAdapter;
import com.example.snapchat.RecyclerViewFollow.FollowerObject;
import com.example.snapchat.RecyclerViewStory.StoryObject;
import com.example.snapchat.fragment.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ViewFollowersActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText mInput;
    private FirebaseAuth auth;
    private static final String TAG = "FindUserActivity";
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_followers);



        mInput = findViewById(R.id.input1);
        ImageView mSearch = findViewById(R.id.search1);
        auth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView1);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FollowerAdapter(getDataSet(), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                getData();
            }
        });
        }

    private void getData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference emailDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("followers");
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
                    FollowerObject obj = new FollowerObject(user,uid);
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


    //This method is used to search emails based on the entered text
        private void listenForData() {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            for (int i = 0; i < UserInformation.listFollowers.size(); i++){

                DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowers.get(i));

                followingStoryDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Getting Information from database and checking if the timestamp lies in between the 24 hours timestamp for the story
                        Log.d(TAG, "listenForData: "+dataSnapshot);
                        String name = dataSnapshot.child("name").getValue().toString();
                        String uid = dataSnapshot.getRef().getKey();

                                FollowerObject obj = new FollowerObject(name, uid);
                                Log.d(TAG, "onDataChange: "+obj);
                                //If more than one story is present for the story
                                if(!results.contains(obj)){
                                    Log.d(TAG, "onDataAdd: "+obj);
                                    results.add(obj);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

        }

        //This method clears the search result on each search done
        private void clear() {
            int size = this.results.size();
            this.results.clear();
            mAdapter.notifyItemRangeChanged(0,size);
        }


        //Will return all the users in the recycler view
        private ArrayList<FollowerObject> results = new ArrayList<>();

        //In the beginning of the search load up every single data
        private ArrayList<FollowerObject> getDataSet() {
            if(results.size()!=0){
                clear();
            }
            listenForData();
            return results;
        }

}
