package com.juju.amazeingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class GameFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private int mDeathCount = 0;

    private String generateMessage() {
        switch (mDeathCount) {
            case 0:
                return getResources().getString(R.string.gameFragmentText0);
            case 1:
                return getResources().getString(R.string.gameFragmentText1);
            default:
                return String.format(getResources().getString(R.string.gameFragmentTextMore), mDeathCount);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.gameFragmentTitle));
        builder.setMessage(generateMessage());
        builder.setPositiveButton(getResources().getString(R.string.gameFragmentPositiveButton), this);
        builder.setNeutralButton(getResources().getString(R.string.gameFragmentNeutralButton), this);
        builder.setNegativeButton(getResources().getString(R.string.gameFragmentNegativeButton), this);
        setCancelable(false);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                ((GameActivity)getActivity()).restart(true);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                ((GameActivity)getActivity()).restart(false);
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                getActivity().finish();
                break;
        }
    }

    public void updateDeathCount(int deathCount) {
        mDeathCount = deathCount;
    }

}
