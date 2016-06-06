package com.morning.demo.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.morning.demo.R;
import com.morning.demo.activity.download.DownloadActivity;
import com.morning.demo.tools.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements IMainView {

    private final String TAG = "mumu";

    private MainPresenter mPresenter;
    private DrawerLayout mDrawerLayout;
    private FloatingSearchView mSearchView;
    private RecyclerView mSearchResultsList;
    private LinearLayout mTip;
    private ImageView mTipImg;
    private TextView mTipText;
    private SweetAlertDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchResultsList = (RecyclerView) findViewById(R.id.search_results_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTip = (LinearLayout) findViewById(R.id.tip);
        mTipImg = (ImageView) findViewById(R.id.tip_img);
        mTipText = (TextView) findViewById(R.id.tip_text);
        mLoading = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

        mPresenter = new MainPresenter(this);
        mPresenter.setSearchResultsList(mSearchResultsList);
        setupFloatingSearch();
        setupDrawer();
        mPresenter.handleSearch("");
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                mPresenter.handleSearch(searchSuggestion.getBody());
                Log.d(TAG, "onSuggestionClicked()");
            }

            @Override
            public void onSearchAction(String query) {
                mPresenter.handleSearch(query);
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(Utils.getSearchSuggestions());

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                //just print action
                if (item.getItemId() == R.id.action_to_download) {
                    Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHamburger"
        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {

                Log.d(TAG, "onMenuOpened()");
                mDrawerLayout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {
                Log.d(TAG, "onMenuClosed()");
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_history_black_24dp, null));
                leftIcon.setAlpha(.36f);
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });
    }



    private void setupDrawer() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mSearchView.setLeftMenuOpen(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        if (!mSearchView.setSearchFocused(false)) {
            super.onBackPressed();
        }
    }


    @Override
    public void showLoading() {
        mLoading.setTitleText("Loading...").show();
    }

    @Override
    public void dimissLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoading.dismiss();
            }
        },300);
    }

    @Override
    public void showEmptyTips() {
        mTipImg.setBackgroundResource(R.drawable.no_result);
        mTipText.setText("sorry, I had not found anything...");
        mTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void dimissEmptyTips() {
        mTip.setVisibility(View.GONE);
    }
}
