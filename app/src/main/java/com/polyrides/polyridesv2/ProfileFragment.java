package com.polyrides.polyridesv2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polyrides.polyridesv2.models.AppUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView username;
    private ImageView profileImageView;
    private Button viewPastRidesBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button signOutButton;
    private String uid;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference usersReference;
    private AppUser thisUser;
    private boolean isThisMe;
    private CardView pastRidesCard;
    private LinearLayout pastRidesLayout;
    private CardView signOutCard;
    private LinearLayout signOutLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String uid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_PARAM1);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isThisMe = uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (isThisMe) {
            getActivity().setTitle("Your Profile");
        }
        else {
            getActivity().setTitle("User Profile");
        }

        // Inflate the layout for this fragment
        CoordinatorLayout cl = (CoordinatorLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = (ImageView) cl.findViewById(R.id.profileimage);
        username = (TextView) cl.findViewById(R.id.userName);

        signOutButton = (Button) cl.findViewById(R.id.signOutButton);
        viewPastRidesBtn = (Button) cl.findViewById(R.id.viewPastRidesBtn);
        signOutCard = cl.findViewById(R.id.SignOutCard);
        signOutLayout = cl.findViewById(R.id.signOutLayout);
        pastRidesCard = cl.findViewById(R.id.PastRidesCard);
        pastRidesLayout = cl.findViewById(R.id.PastRidesLayout);

        if (isThisMe) {
            pastRidesCard.setVisibility(View.VISIBLE);
            signOutCard.setVisibility(View.VISIBLE);
        }
        else {
            pastRidesLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            signOutLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finishAffinity();
            }
        });

        viewPastRidesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        usersReference = FirebaseDatabase.getInstance().getReference("Profile/" + uid);

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(AppUser.class);

                String userProfileImg = thisUser.getPhoto();

                username.setText(thisUser.getFirstName() + " " + thisUser.getLastName());

                if (userProfileImg != null) {
                    new ImageDownloaderTask(profileImageView).doInBackground(userProfileImg);
                }
                else {
                    profileImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_user_image));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return cl;
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

