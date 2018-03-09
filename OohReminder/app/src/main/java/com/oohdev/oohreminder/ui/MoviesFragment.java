package com.oohdev.oohreminder.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.oohdev.oohreminder.R;
import com.oohdev.oohreminder.core.api.MovieApiHelper;
import com.oohdev.oohreminder.core.db.MoviesTable;
import com.oohdev.oohreminder.core.model.MovieModelComplete;
import com.squareup.picasso.Picasso;

import junit.framework.Assert;

import java.lang.ref.WeakReference;

public class MoviesFragment extends ContentFragment {
    private RecyclerView mRecyclerView;
    private MoviesRecyclerAdapter mRecyclerAdapter;
    private MoviesTable mMoviesTable;
    private MovieItemClickListener mMovieItemClickListener;

    public static MoviesFragment newInstance() {
        // Bundle logic might be useful in future
        Bundle args = new Bundle();
        MoviesFragment fragment = new MoviesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Assert.assertNotNull(getContext());
        mMovieItemClickListener = new MovieItemClickListener();
        mMoviesTable = new MoviesTable(getContext());
        mRecyclerView = view.findViewById(R.id.movies_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new MoviesRecyclerAdapter(mMoviesTable.getMoviesOrderedByDate(), mMovieItemClickListener);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecycler();
    }

    @Override
    public void addElement() {
        final MoviesFragment currentFragment = this;
        if (currentFragment.getContext() == null) {
            return;
        }
        new MaterialDialog.Builder(currentFragment.getContext())
                .title(R.string.add_movie)
                .inputRange(2, 30)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.add_title_hint, R.string.empty_string, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        new GetMovieInfoTask(currentFragment, input.toString(), "unknown", "no description").execute();
                    }
                }).show();
    }

    private void updateRecycler() {
        mRecyclerAdapter.replaceItems(mMoviesTable.getMoviesOrderedByDate());
    }

    private void updateRecycler(@NonNull MovieModelComplete movie) {
        mRecyclerAdapter.addItem(movie);
        mRecyclerView.scrollToPosition(0);
    }

    private class MovieItemClickListener implements ContentItemClickResolver {
        @Override
        public boolean onLongClick(final int item) {
            final String itemTitle = mRecyclerAdapter.getItems().get(item).getTitle();
            Assert.assertNotNull(getContext());
            new MaterialDialog.Builder(getContext())
                    .title(R.string.delete_movie)
                    .content(R.string.sure_to_delete_movie)
                    .positiveText(R.string.delete)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mRecyclerAdapter.removeItem(item);
                            mMoviesTable.removeMovie(itemTitle);
                        }
                    }).show();
            return true;
        }

        @Override
        public void onClick(int item) {
            final MovieModelComplete movie = mRecyclerAdapter.getItems().get(item);
            Assert.assertNotNull(getContext());
            MaterialDialog completeInfoDialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.movie_complete_desc)
                    .customView(R.layout.movie_card_complete, true)
                    .positiveText(R.string.ok)
                    .build();
            View movieCardComplete = completeInfoDialog.getCustomView();
            TextView title = movieCardComplete.findViewById(R.id.movie_title_complete);
            title.setText(movie.getTitle());
            TextView desc = movieCardComplete.findViewById(R.id.movie_desc_complete);
            desc.setText(movie.getDescription());
            TextView director = movieCardComplete.findViewById(R.id.movie_director_complete);
            director.setText(movie.getDirector());
            AppCompatImageView poster = movieCardComplete.findViewById(R.id.movie_poster);
            if (!movie.getPosterUrl().isEmpty()) {
                Picasso.with(getContext())
                        .load(movie.getPosterUrl())
                        .error(R.drawable.unknown_movie)
                        .into(poster);
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.unknown_movie)
                        .into(poster);
            }
            completeInfoDialog.show();
        }
    }

    // static modifier and WeakReference logic are required to avoid memory leak: goo.gl/hy74u2
    private static class GetMovieInfoTask extends AsyncTask<Void, Void, MovieModelComplete> {
        private final WeakReference<MoviesFragment> moviesFragmentRef;
        private final String mTitle;
        private final String mDirector;
        private final String mDescription;

        GetMovieInfoTask(MoviesFragment moviesFragment, String title, String defDirector, String defDesc) {
            moviesFragmentRef = new WeakReference<>(moviesFragment);
            mTitle = title;
            mDirector = defDirector;
            mDescription = defDesc;
        }

        @Override
        @NonNull
        protected MovieModelComplete doInBackground(Void... voids) {
            return MovieApiHelper.getMovieModel(mTitle, mDirector, mDescription);
        }

        @Override
        protected void onPostExecute(@NonNull MovieModelComplete movie) {
            MoviesFragment fragment = moviesFragmentRef.get();
            if (fragment == null || fragment.getContext() == null) {
                return;
            }
            new MoviesTable(fragment.getContext()).insertMovie(movie);
            if (fragment.isResumed()) {
                fragment.updateRecycler(movie);
            }
        }
    }
}