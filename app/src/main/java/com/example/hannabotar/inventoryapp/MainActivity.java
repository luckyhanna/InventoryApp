package com.example.hannabotar.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hannabotar.inventoryapp.adapter.ItemCursorAdapter;
import com.example.hannabotar.inventoryapp.data.ItemContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DB_ITEM_LOADER_ID = 1;

    private static final String[] PROJECTION = {
            ItemContract.ItemEntry._ID,
            ItemContract.ItemEntry.COLUMN_PRODUCT_NAME,
            ItemContract.ItemEntry.COLUMN_PRICE,
            ItemContract.ItemEntry.COLUMN_QUANTITY,
    };

    private ItemCursorAdapter mAdapter;

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Setup cursor adapter
        mAdapter = new ItemCursorAdapter(this, null);
        // Attach cursor adapter to the ListView
        listView.setAdapter(mAdapter);

        // Set empty view on the ListView, so that it only shows when the list has 0 items.
        listView.setEmptyView(emptyView);

        // Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mAdapter.getItem(position);
                Intent edit = new Intent(MainActivity.this, EditActivity.class);
                edit.setData(ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id));
                startActivity(edit);
            }
        });

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(DB_ITEM_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(this, ItemContract.ItemEntry.CONTENT_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        progressBar.setVisibility(View.GONE);
        if (data.getCount() == 0) {
            emptyView.setText(getString(R.string.empty_text));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
