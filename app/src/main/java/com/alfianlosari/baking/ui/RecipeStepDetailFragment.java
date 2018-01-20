package com.alfianlosari.baking.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.provider.BakingProvider;
import com.alfianlosari.baking.provider.RecipeContract;
import com.alfianlosari.baking.provider.StepContract;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecipeStepDetailFragment extends Fragment {

    private static final String STEP_ID = "STEP_ID";
    private static final String PLAYER_POSITION = "PLAYER_POSITION";
    private String mVideoURL;
    private long mStepId;
    private long mPlayerPosition;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private TextView mInstructionTextView;
    private ImageView mThumbnailImageView;
    private SimpleExoPlayer mExoPlayer;
    private Button nextButton;
    private Button prevButton;

    public RecipeStepDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);
        mInstructionTextView = rootView.findViewById(R.id.textview_instruction);
        mThumbnailImageView = rootView.findViewById(R.id.imageview_thumbnail);
        mSimpleExoPlayerView = rootView.findViewById(R.id.playerView);
        mSimpleExoPlayerView.requestFocus();
        prevButton = rootView.findViewById(R.id.prevButton);
        nextButton = rootView.findViewById(R.id.nextButton);
        if (prevButton != null) {
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mStepId -= 1;
                    setupView(mStepId);
                }
            });
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mStepId += 1;
                    setupView(mStepId);
                }
            });
        }

        if(savedInstanceState != null) {
            mStepId = savedInstanceState.getLong(STEP_ID, -1);
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION, C.TIME_UNSET);
        } else {
            mPlayerPosition = C.TIME_UNSET;
        }

        if (mStepId != -1) {
            setupView(mStepId);
        }
        return rootView;
    }

    public void setupView(long stepId) {
        Cursor stepCursor = getContext().getContentResolver().query(
                BakingProvider.BakingSteps.withId(mStepId),
                null,
                null,
                null,
                null
        );

        if (stepCursor != null && stepCursor.moveToNext()) {
            Long recipeId = stepCursor.getLong(stepCursor.getColumnIndex(StepContract.COLUMN_RECIPE_ID));
            int order = stepCursor.getInt(stepCursor.getColumnIndex(StepContract.COLUMN_STEP_ORDER));
            String instruction = stepCursor.getString(stepCursor.getColumnIndex(StepContract.COLUMN_DESCRIPTION));
            String videoURL = stepCursor.getString(stepCursor.getColumnIndex(StepContract.COLUMN_VIDEO_URL));
            String thumbnailURL = stepCursor.getString(stepCursor.getColumnIndex(StepContract.COLUMN_THUMBNAIL_URL));
            String tag = (String) mInstructionTextView.getTag();

            if (tag.equals(getResources().getString(R.string.landscape))) {
                if (videoURL.isEmpty()) {
                    mSimpleExoPlayerView.setVisibility(View.GONE);
                    mInstructionTextView.setVisibility(View.VISIBLE);
                    mThumbnailImageView.setVisibility(View.VISIBLE);
                } else {
                    mInstructionTextView.setVisibility(View.GONE);
                    mSimpleExoPlayerView.setVisibility(View.VISIBLE);
                    mThumbnailImageView.setVisibility(View.VISIBLE);
                    setScreenAsFullScreen();
                }
            } else if (tag.equals(getResources().getString(R.string.portrait))) {
                if (order == 0) {
                    prevButton.setVisibility(View.INVISIBLE);
                } else {
                    prevButton.setVisibility(View.VISIBLE);
                }

                Cursor stepCountCursor = getContext().getContentResolver().query(
                        BakingProvider.BakingSteps.withRecipeId(recipeId),
                        null,
                        null,
                        null,
                        null
                );

                if (stepCountCursor != null && order < stepCountCursor.getCount() - 1) {
                    nextButton.setVisibility(View.VISIBLE);
                } else {
                    nextButton.setVisibility(View.INVISIBLE);
                }

                stepCountCursor.close();
            }

            mInstructionTextView.setText(instruction);
            if (!thumbnailURL.isEmpty()) {
                Picasso.with(getContext()).load(thumbnailURL).into(mThumbnailImageView);
            }

            Cursor recipeCursor = getContext().getContentResolver().query(
                    BakingProvider.BakingRecipes.withId(recipeId),
                    null,
                    null,
                    null,
                    null);

            String titleText = "";
            if (order > 0) {
                titleText = getResources().getString(R.string.step_header, 0, order) + "";
            } else {
                titleText = getResources().getString(R.string.step_intro);
            }

            if (recipeCursor != null && recipeCursor.moveToNext()) {
                String name = recipeCursor.getString(recipeCursor.getColumnIndex(RecipeContract.COLUMN_NAME));
                titleText += " - " + name;
            }

            recipeCursor.close();
            mVideoURL = videoURL;
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(titleText);
        } else {
            mSimpleExoPlayerView.setVisibility(View.GONE);
        }
    }

    public void setStepId(long stepId) {
        this.mStepId = stepId;
    }

    private void setScreenAsFullScreen() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        activity.getSupportActionBar().hide();
    }


    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mSimpleExoPlayerView.requestFocus();
            mSimpleExoPlayerView.setPlayer(mExoPlayer);



            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(getContext(), getUserAgent(getContext(), "BakingApp")),
                    new DefaultExtractorsFactory(),
                    null,
                    null
            );

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVideoURL != null && !mVideoURL.isEmpty()) {
            initializePlayer(Uri.parse(mVideoURL));
            mSimpleExoPlayerView.setVisibility(View.VISIBLE);
        } else {
            mSimpleExoPlayerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            if (mPlayerPosition != C.TIME_UNSET) {
                mExoPlayer.seekTo(mPlayerPosition);
            }
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public String getUserAgent(Context context, String applicationName) {
        String versionName;
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        return applicationName + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE
                + ") " + "ExoPlayerLib/" + ExoPlayerLibraryInfo.VERSION;
    }


    @Override
    public void onSaveInstanceState(Bundle currentState) {
        if (mStepId != -1) currentState.putLong(STEP_ID, mStepId);
        if (mPlayerPosition != -1) currentState.putLong(PLAYER_POSITION, mPlayerPosition);
    }

}
