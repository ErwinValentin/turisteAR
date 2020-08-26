package com.valentingonzalez.turistear.modal_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.valentingonzalez.turistear.R;
import com.valentingonzalez.turistear.providers.UserProvider;

import org.w3c.dom.Text;

public class LocationInfoModalSheet extends BottomSheetDialogFragment {

    public LocationInfoModalSheet(){}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.info_modal_sheet, container, false);
        Bundle b = getArguments();
        TextView tv = layout.findViewById(R.id.modal_tv);
        TextView nv = layout.findViewById(R.id.name_tv);
        if(b != null){
            tv.setText(b.getString("TITLE"));
        }
        UserProvider userProvider = new UserProvider();
        userProvider.getUser(nv);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
