package com.cmax.bodysheild.activity.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.SharedPreferencesUtil;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TemperatureInfoImgFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TemperatureInfoImgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemperatureInfoImgFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * 每1摄氏度对应的角度
     */
    private static final float SCALE           = 11.25f;
    private static final float TEMPERATURE_MIN = 34.0f;
    private static final float TEMPERATURE_MAX = 42.00f;

    /**
     * 开始箭头指向的角度
     */
    private static final float STRAT_ANGLE = 90F;

    @Bind(R.id.temperature_info_tempvalue)
    TextView tempvalueTextView;

    @Bind(R.id.pointerCircle)
    ImageView pointerCircle;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //默认的温度
    private Temperature preTemperature = new Temperature(System.currentTimeMillis(), 34,new byte[]{});

    private OnFragmentInteractionListener mListener;
    DecimalFormat df = new DecimalFormat("#.00");

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TemperatureInfoImgFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TemperatureInfoImgFragment newInstance(String param1, String param2) {
        TemperatureInfoImgFragment fragment = new TemperatureInfoImgFragment();
        Bundle                     args     = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureInfoImgFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_temperature_info_img, container, false);
        ButterKnife.bind(this, view);

        refreshTemperature(preTemperature);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 在动态温度显示区域设值
     * @param tempValue
     */
    public void setTempValue(String tempValue){
        if(tempvalueTextView != null){
            tempvalueTextView.setText(tempValue);
        }
    }



    public void refreshTemperature(Temperature value){

        if (isNormalTemperature(value)){

            final float preValue = preTemperature.getValue();
            final float currentValue = value.getValue();

            AnimationSet animationSet = new AnimationSet(true);

            RotateAnimation rotateAnimation = new RotateAnimation(
                    getAngle(preValue), getAngle(currentValue), Animation.RELATIVE_TO_SELF,0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            animationSet.addAnimation(rotateAnimation);
            animationSet.setDuration(1000);
            animationSet.setFillAfter(true);

            pointerCircle.startAnimation(animationSet);

            //缓存这次的度数
            preTemperature = value;
        }

    }

    private boolean isNormalTemperature(Temperature value){
        return (value != null && value.getValue() != 0 && value.getValue() != -1);
    }

    private float getAngle(float value){
        if (value >= TEMPERATURE_MIN && value <= TEMPERATURE_MAX){
            float temp = value - TEMPERATURE_MIN;

            return (STRAT_ANGLE + temp*SCALE);
        }
        return  STRAT_ANGLE;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
