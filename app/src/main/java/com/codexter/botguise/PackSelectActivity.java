package com.codexter.botguise;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PackSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PackAdaptor adapter;
    private List<Pack> packList;

    private String name, email, uid, photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_select);

        Intent fromIntent = getIntent();
        name = fromIntent.getStringExtra("name");
        email = fromIntent.getStringExtra("email");
        uid = fromIntent.getStringExtra("uid");
        photoUrl = fromIntent.getStringExtra("photoUrl");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        packList = new ArrayList<>();
        adapter = new PackAdaptor(this, packList);

        ((TextView) findViewById(R.id.pack_select_cover_detail)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextView) findViewById(R.id.pack_select_cover_title)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        preparePacks();

        try {
            Glide.with(this).load(R.drawable.pack_select_cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pack_select_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You have been logged out successully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PackSelectActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
                return true;
            case R.id.how_to_play_menu:
                showHelp();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Battle Arena");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void preparePacks() {
        int[] covers = new int[]{
                R.drawable.harry_potter,
                R.drawable.marvel_vs_dc,
                R.drawable.ronaldo_messi,
                R.drawable.friends};


        Pack pack = new Pack("Harry Potter", covers[0]);
        packList.add(pack);

        pack = new Pack("Marvel vs DC", covers[1]);
        packList.add(pack);

        pack = new Pack("Soccer", covers[2]);
        packList.add(pack);

        pack = new Pack("Friends", covers[3]);
        packList.add(pack);

        adapter.notifyDataSetChanged();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void showHelp() {

        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.

        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                .replace(R.id.container, new BackFragment())

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();
    }

    public static class BackFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.help_fragment, container, false);

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf");
            final TextView help_satis_text = (TextView) view.findViewById(R.id.help_satis_text);
            final TextView help_guess_text = (TextView) view.findViewById(R.id.help_guess_text);

            ((TextView) view.findViewById(R.id.help_title)).setTypeface(typeface);
            ((TextView) view.findViewById(R.id.help_train_sub)).setTypeface(typeface);
            ((TextView) view.findViewById(R.id.help_train)).setTypeface(typeface);
            ((TextView) view.findViewById(R.id.help_battle_sub)).setTypeface(typeface);
            ((TextView) view.findViewById(R.id.help_battle_1)).setTypeface(typeface);
            ((TextView) view.findViewById(R.id.help_battle_2)).setTypeface(typeface);

            help_guess_text.setTypeface(typeface);
            help_satis_text.setTypeface(typeface);

            RadioButton help_user = (RadioButton) view.findViewById(R.id.help_user);
            final RadioButton help_satis = (RadioButton) view.findViewById(R.id.help_satis);
            help_satis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        help_satis_text.setText("Satisfied");
                    } else {
                        help_satis_text.setText("Not Satisfied");
                    }
                }
            });
            help_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        help_guess_text.setText("User");
                    } else {
                        help_guess_text.setText("Bot");
                    }
                }
            });

            return view;
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}