package com.example.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.MainActivity;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(this.getFragmentLayout(),container,false);
    }

    protected abstract int getFragmentLayout();

    @Override
    public void onCreateOptionsMenu(@NonNull Menu item, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(item, inflater);
        MapFragment mapFragment = ((MainActivity) requireActivity()).getMapFragment();

        inflater.inflate(R.menu.menu, item);

        SearchView searchView = (SearchView) item.findItem(R.id.restaurant_action_search).getActionView();
        searchView.setQueryHint("Search a restaurant");
        searchView.setImeOptions(EditorInfo.IME_ACTION_NONE);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // remove suggestions keyboard
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 3 && mapFragment.getDeviceLocation() != null) {
                    mapFragment.executeHttpRequestAutoCompleteWithRetrofit(newText);
                }
                return false;
            }
        });

    }
}
