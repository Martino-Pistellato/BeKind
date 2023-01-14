package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateActivitySecondPage extends Fragment {
    private View view;
    private BottomBarViewModel bottomBarViewModel;
    private MapViewModel mapViewModel;
    private Dialog choose_dialog;
    private CreateActivityDialog createActivityDialog;


    public CreateActivitySecondPage(BottomBarViewModel bottomBarViewModel, Dialog choose_dialog, CreateActivityDialog createActivityDialog, MapViewModel mapViewModel){
        this.bottomBarViewModel = bottomBarViewModel;
        this.choose_dialog = choose_dialog;
        this.createActivityDialog = createActivityDialog;
        this.mapViewModel = mapViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.add_proposal_popup2,container,false);

        CheckBox groupProposal, periodicProposal;
        TextInputEditText maxParticipants;
        ListView listView;
        Button backBtn, publishBtn;
        
        TextInputEditText city = view.findViewById(R.id.user_city),
                street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number);

        city.setText(bottomBarViewModel.getProposalCity());
        street.setText(bottomBarViewModel.getProposalStreet());
        streetNumber.setText(bottomBarViewModel.getProposalStreetNumber());

        groupProposal = view.findViewById(R.id.group_checkbox);
        maxParticipants = view.findViewById(R.id.activity_maxparticipants);
        groupProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    maxParticipants.setVisibility(View.VISIBLE);
                else {
                    maxParticipants.setText("");
                    maxParticipants.setVisibility(View.GONE);
                }
            }
        });

        periodicProposal = view.findViewById(R.id.periodic_checkbox);
        listView = view.findViewById(R.id.periodic_choices);

        ArrayList<String> types = new ArrayList<>(Arrays.asList(RepublishTypes.DAILY.getNameToDisplay(), RepublishTypes.WEEKLY.getNameToDisplay(), RepublishTypes.MONTHLY.getNameToDisplay(), RepublishTypes.ANNUALLY.getNameToDisplay()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, types);
        listView.setAdapter(adapter);
        RepublishTypes[] choice = new RepublishTypes[1];
        choice[0] = RepublishTypes.NEVER;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice[0] = RepublishTypes.getValue((String) listView.getItemAtPosition(i));
            }
        });

        periodicProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    listView.setVisibility(View.VISIBLE);
                else
                    listView.setVisibility(View.GONE);
            }
        });

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_prop);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_prop);

        mapViewModel.initializeMap(getActivity(), getContext(), autocompleteFragment, mapFragment, city, street, streetNumber, null, null);


        backBtn = view.findViewById(R.id.back_btn);
        publishBtn = view.findViewById(R.id.publish_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityText = city.getText().toString().trim(),
                        streetText = street.getText().toString().trim(), streetNumb = streetNumber.getText().toString().trim();

                bottomBarViewModel.saveProposalLocationData(cityText, streetText, streetNumb);
                createActivityDialog.changeFragment(R.layout.add_proposal_popup2);

            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int proposalMaxParticipants= 1;
                boolean publish = true;
                String cityText = city.getText().toString();
                String streetNumb = streetNumber.getText().toString();
                String streetText = street.getText().toString();

                if(groupProposal.isChecked()) {
                    if (!bottomBarViewModel.checkGroupProposalConstraints(maxParticipants, maxParticipants.getText().toString().trim(), getContext())) {
                        Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                    else{
                        proposalMaxParticipants = Integer.parseInt(maxParticipants.getText().toString().trim());
                    }
                }

                if(periodicProposal.isChecked()){
                    if(choice[0] == RepublishTypes.NEVER){
                        Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                }

                if(!bottomBarViewModel.checkAddress(city, cityText, street,streetText, streetNumber, streetNumb, getContext())){
                    Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                    publish = false;
                }

                LatLng coord = mapViewModel.getCoordinatesFromAddress(getContext(), city.getText().toString().trim(), street.getText().toString().trim(), streetNumber.getText().toString().trim());
                if (coord == null) {
                    Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                    publish = false;
                }

                if (publish) {
                    bottomBarViewModel.createProposal(bottomBarViewModel.getProposalTitle(), bottomBarViewModel.getProposalBody(), proposalMaxParticipants, bottomBarViewModel.getProposalExpd(), coord.latitude, coord.longitude, choice[0], (result -> {
                        if (result) {
                            Toast.makeText(getActivity(), R.string.publish_activity, Toast.LENGTH_SHORT).show();
                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);

                            bottomBarViewModel.setProposalBody("");
                            bottomBarViewModel.setProposalTitle("");
                            bottomBarViewModel.setProposalCity("");
                            bottomBarViewModel.setProposalStreet("");
                            bottomBarViewModel.setProposalStreetNumber("");
                            bottomBarViewModel.setProposalExpd(null);

                            createActivityDialog.dismiss();
                            choose_dialog.dismiss();

                        } else
                            Toast.makeText(getActivity(), R.string.activity_publication_error, Toast.LENGTH_SHORT).show();
                    }));

                }
            }

        });

        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }
}
