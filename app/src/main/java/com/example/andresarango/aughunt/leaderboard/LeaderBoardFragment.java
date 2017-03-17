package com.example.andresarango.aughunt.leaderboard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andresarango.aughunt.HomeScreenActivity;
import com.example.andresarango.aughunt.R;
import com.example.andresarango.aughunt._models.ChallengePhoto;
import com.example.andresarango.aughunt._models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danny on 3/17/2017.
 */

public class LeaderBoardFragment extends Fragment {
    @BindView(R.id.tv_user_points)
    TextView mUserPointsTv;
    @BindView(R.id.review_number)
    TextView mPendingReview;
    @BindView(R.id.pending_review)
    TextView mPending;
    @BindView(R.id.list_of_users)
    RecyclerView mRecyclerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private Map<String, User> mUserMap = new HashMap<>();
    private List<User> mUserList = new ArrayList<>();
    private LeaderBoardAdapter mLeaderBoardAdapter;
    private int mPendingReviewIndicator = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpRecyclerView();
        retrieveUserFromFirebaseAndSetProfile();
        initializePoints();
        initialize();
    }

    private void initialize() {
        rootRef.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addUserToRecyclerView(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateRecyclerView(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //removeChallengeFromRecyclerView(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addUserToRecyclerView(DataSnapshot dataSnapshot) {
        // Key - value
        String userKey = dataSnapshot.getKey();
        User user = dataSnapshot.getValue(User.class);


        mUserMap.put(userKey, user);

        LeaderBoardAdapter adapter = (LeaderBoardAdapter) mRecyclerView.getAdapter();
        adapter.addUserToList(user);
    }

    private void updateRecyclerView(DataSnapshot dataSnapshot) {
        String userKey = dataSnapshot.getKey();

        Set<String> userKeys = mUserMap.keySet();

        mUserMap.put(userKey, dataSnapshot.getValue(User.class));


        mUserList.clear();
        for (String key : userKeys) {
            mUserList.add(mUserMap.get(key));
        }

        mLeaderBoardAdapter.setUserList(mUserList);
    }

    private void initializePoints() {
        rootRef.child("challenges").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addChallengeToRecyclerView(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //updateRecyclerView(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //removeChallengeFromRecyclerView(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addChallengeToRecyclerView(DataSnapshot dataSnapshot) {
        ChallengePhoto challenge = dataSnapshot.getValue(ChallengePhoto.class);

        if (challenge.getOwnerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            mPendingReviewIndicator += challenge.getPendingReviews();
            mPendingReview.setText(String.valueOf(mPendingReviewIndicator));
            if (mPendingReviewIndicator > 0) {
                mPendingReview.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                mPending.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                mPendingReview.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
                mPending.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            }
        }
    }

    private void retrieveUserFromFirebaseAndSetProfile() {
        rootRef.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println("USER: " + user.getProfileName());
                mUserPointsTv.setText(user.getUserPoints() + " PTS");
                mPendingReview.setTypeface(mPendingReview.getTypeface(), Typeface.BOLD);
                mPendingReview.setText(String.valueOf(mPendingReviewIndicator));
                mPending.setTypeface(mPending.getTypeface(), Typeface.BOLD);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeaderBoardAdapter = new LeaderBoardAdapter();
        mRecyclerView.setAdapter(mLeaderBoardAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("CALLED ON START LEADER");
        mPendingReviewIndicator = 0;
        ((HomeScreenActivity) getActivity()).setBottomNavFocusLeaderBoard();
    }
}
