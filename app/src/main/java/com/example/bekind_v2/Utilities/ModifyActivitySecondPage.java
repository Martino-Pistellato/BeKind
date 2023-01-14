package com.example.bekind_v2.Utilities;

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

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;

public class ModifyActivitySecondPage extends Fragment {

    private View view;
    private MapViewModel mapViewModel;
    private ModifyActivityDialog modifyActivityDialog;
    private ProposalRepository.Proposal proposal;
    private String documentId;

    public ModifyActivitySecondPage(ModifyActivityDialog modifyActivityDialog, MapViewModel mapViewModel, ProposalRepository.Proposal proposal, String documentID){
        this.modifyActivityDialog = modifyActivityDialog;
        this.mapViewModel = mapViewModel;
        this.proposal = proposal;
        this.documentId = documentID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.add_proposal_popup2, container, false);


        TextInputEditText city = view.findViewById(R.id.user_city),
                street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number),
                maxPartecipants = view.findViewById(R.id.activity_maxparticipants);;

        Button publishButton = view.findViewById(R.id.publish_btn), backButton = view.findViewById(R.id.back_btn);
        CheckBox groupCheckbox = view.findViewById(R.id.group_checkbox), periodicCheckbox = view.findViewById(R.id.periodic_checkbox);
        ListView periodicChoices = view.findViewById(R.id.periodic_choices);

        if(proposal.getMaxParticipants() != 1){
            groupCheckbox.setChecked(true);
            maxPartecipants.setVisibility(View.VISIBLE);
            maxPartecipants.setText(Integer.toString(proposal.getMaxParticipants()));
        }

        ArrayList<String> types = new ArrayList<>(Arrays.asList(RepublishTypes.DAILY.getNameToDisplay(), RepublishTypes.WEEKLY.getNameToDisplay(), RepublishTypes.MONTHLY.getNameToDisplay(), RepublishTypes.ANNUALLY.getNameToDisplay()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, types);
        periodicChoices.setAdapter(adapter);
        RepublishTypes[] choice = new RepublishTypes[1];
        choice[0] = RepublishTypes.NEVER;

        if(proposal.getRepublishTypes() != RepublishTypes.NEVER){
            periodicCheckbox.setChecked(true);
            periodicChoices.setVisibility(View.VISIBLE);
            switch(proposal.getRepublishTypes()){
                case DAILY: periodicChoices.setItemChecked(0, true); break;
                case WEEKLY: periodicChoices.setItemChecked(1, true); break;
                case MONTHLY: periodicChoices.setItemChecked(2, true); break;
                case ANNUALLY: periodicChoices.setItemChecked(3, true); break;
            }
        }

        periodicChoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice[0] = RepublishTypes.getValue((String) periodicChoices.getItemAtPosition(i));
            }
        });

        groupCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    maxPartecipants.setVisibility(View.VISIBLE);
                    maxPartecipants.setText(Integer.toString(proposal.getMaxParticipants()));

                }else{
                    maxPartecipants.setVisibility(View.GONE);
                    maxPartecipants.setText("");
                }
            }
        });

        periodicCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    periodicChoices.setVisibility(View.VISIBLE);
                else {
                    periodicChoices.setVisibility(View.GONE);
                    choice[0] = RepublishTypes.NEVER;
                    for(int i = 0; i< types.size(); ++i)
                        periodicChoices.setItemChecked(i ,false);
                }
            }
        });

        LatLng coord = new LatLng(proposal.getLatitude(), proposal.getLongitude());

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_prop);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_prop);

        mapViewModel.initializeMap(getActivity(), getContext(), autocompleteFragment, mapFragment, city, street, streetNumber, coord, null);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityText = city.getText().toString().trim(),
                        streetText = street.getText().toString().trim(), streetNumb = streetNumber.getText().toString().trim();

                modifyActivityDialog.saveProposalLocationData(cityText, streetText, streetNumb);
                modifyActivityDialog.changeFragment(R.layout.add_proposal_popup2);
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int proposalMaxParticipants= 1;
                boolean publish = true;
                String cityText = city.getText().toString();
                String streetNumb = streetNumber.getText().toString();
                String streetText = street.getText().toString();

                if(groupCheckbox.isChecked()) {
                    if (!modifyActivityDialog.checkGroupProposalConstraints(maxPartecipants, maxPartecipants.getText().toString().trim(), getContext())) {
                        Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                    else{
                        proposalMaxParticipants = Integer.parseInt(maxPartecipants.getText().toString().trim());
                    }
                }

                if(periodicCheckbox.isChecked()){
                    if(choice[0] == RepublishTypes.NEVER){
                        Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                }

                if(!modifyActivityDialog.checkAddress(city, cityText, street,streetText, streetNumber, streetNumb, getContext())){
                    Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                    publish = false;
                }

                LatLng coord = mapViewModel.getCoordinatesFromAddress(getContext(), city.getText().toString().trim(), street.getText().toString().trim(), streetNumber.getText().toString().trim());
                if (coord == null) {
                    Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                    publish = false;
                }

                if (publish){
                    ProposalRepository.editProposal(documentId, modifyActivityDialog.getActivityTitle(), modifyActivityDialog.getActivityBody(), modifyActivityDialog.getActivityExpd(), proposal.getPublishingDate(), modifyActivityDialog.getFilters(), proposalMaxParticipants, choice[0], coord.latitude, coord.longitude, new MyCallback<Boolean>() {
                        @Override
                        public void onCallback(Boolean result) {
                            if(result){
                                Toast.makeText(getContext(), R.string.updated_activity, Toast.LENGTH_SHORT).show();
                                Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                modifyActivityDialog.dismiss();
                            }
                            else
                                Toast.makeText(getContext(), R.string.error_updated_activity, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        return view;
    }

}
