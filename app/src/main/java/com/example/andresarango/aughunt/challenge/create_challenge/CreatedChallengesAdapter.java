package com.example.andresarango.aughunt.challenge.create_challenge;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andresarango.aughunt.R;
import com.example.andresarango.aughunt.challenge.Challenge;
import com.example.andresarango.aughunt.ChallengeReviewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Millochka on 3/5/17.
 */

public class CreatedChallengesAdapter extends RecyclerView.Adapter {

    private List<Challenge<Bitmap>> mChallengeList = new ArrayList<>();
    private ChallengeReviewHelper<Bitmap> mListener;

    public CreatedChallengesAdapter(ChallengeReviewHelper<Bitmap> listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.created_challenge, parent, false);

        return new CreatedChallengesViewHolder(view);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CreatedChallengesViewHolder createdChallengesViewHolder = (CreatedChallengesViewHolder) holder;
        createdChallengesViewHolder.bind(mChallengeList.get(position));
        createdChallengesViewHolder.getmCreatedChallenge().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.passingChallange(mChallengeList.get(position));


            }
        });

    }

    @Override
    public int getItemCount() {
        return mChallengeList.size();
    }

    public void setChallengeList(List<Challenge<Bitmap>> challengeList) {
        this.mChallengeList.clear();
        this.mChallengeList.addAll(challengeList);
        notifyDataSetChanged();
    }
}
