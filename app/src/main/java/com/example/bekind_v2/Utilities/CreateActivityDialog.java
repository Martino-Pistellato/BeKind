package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;

public class CreateActivityDialog extends DialogFragment {

    private View view;
    private BottomBarViewModel bottomBarViewModel;
    private Dialog choose_dialog;
    private CreateActivityFirstPage createActivity1;
    private CreateActivitySecondPage createActivity2;

    public CreateActivityDialog(BottomBarViewModel bottomBarViewModel, Dialog choose_dialog, MapViewModel mapViewModel){
        this.bottomBarViewModel = bottomBarViewModel;
        this.choose_dialog = choose_dialog;
        this.createActivity2 = new CreateActivitySecondPage(bottomBarViewModel, choose_dialog, this, mapViewModel);
        this.createActivity1 = new CreateActivityFirstPage(bottomBarViewModel, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.add_proposal_popup_container,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.add_proposal_container, createActivity1);
        transaction.addToBackStack("childFragment1");
        transaction.commit();
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void changeFragment(FragmentActivity activity, int fragmentId){
        //we start a fragment transaction and we replace current view with the new fragment
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if(fragmentId == R.layout.add_proposal_popup) {
            fragmentTransaction.replace(R.id.add_proposal_container, createActivity2);
            //You can add here as well your fragment in and out animation how you like.
            fragmentTransaction.addToBackStack("childFragment2");
            fragmentTransaction.commit();
        }else{
            fragmentTransaction.replace(R.id.add_proposal_container, createActivity1);
            //You can add here as well your fragment in and out animation how you like.
            fragmentTransaction.addToBackStack("childFragment1");
            fragmentTransaction.commit();
        }
    }
}
