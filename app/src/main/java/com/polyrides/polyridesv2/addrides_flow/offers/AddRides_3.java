package com.polyrides.polyridesv2.addrides_flow.offers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.polyrides.polyridesv2.AddRideOfferActivity;
import com.polyrides.polyridesv2.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddRides_3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddRides_3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRides_3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText numSeats;
    private EditText descText;
    private EditText costText;
    private Button nextButton;
    private EditText dateText;
    private EditText timeText;

    private OnFragmentInteractionListener mListener;

    public AddRides_3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddRides_3.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRides_3 newInstance(String param1, String param2) {
        AddRides_3 fragment = new AddRides_3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_rides_3, container, false);

        numSeats = (EditText) v.findViewById(R.id.numSeats);
        descText = (EditText) v.findViewById(R.id.descText);
        costText = (EditText) v.findViewById(R.id.costText);
        nextButton = (Button) v.findViewById(R.id.nextButton);
        dateText = v.findViewById(R.id.dateText);
        timeText = v.findViewById(R.id.timeText);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = timeText.getText().toString();
                String date = dateText.getText().toString();

                String res = date + " " + time;
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mmaa");
                Date d = null;
                try {
                    d = formatter.parse(res);
                }
                catch (Exception e ) {

                }

                AddRideOfferActivity a = (AddRideOfferActivity) getActivity();

                a.setSeats(Integer.valueOf(numSeats.getText().toString()));
                a.setDescription(descText.getText().toString());
                a.setCost(Double.valueOf(costText.getText().toString()));

                Fragment f = AddRides_4.newInstance("", "");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, f);
                ft.commit();
            }
        });


        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}