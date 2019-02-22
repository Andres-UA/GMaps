package com.appmoviles.andres.gmaps;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MarkerDialog extends DialogFragment {

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_marker, null);

        final EditText et_place_name = v.findViewById(R.id.et_place_name);
        final EditText et_place_description = v.findViewById(R.id.et_place_description);

        builder.setView(v)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_place_name.getText().toString();
                        String description = et_place_description.getText().toString();

                        if (name.equals("") || name == null) {
                            Toast.makeText(getContext(), "El nombre del lugar es obligatorio", Toast.LENGTH_LONG).show();
                        } else {
                            mListener.onDialogPositiveClick(MarkerDialog.this, name, description);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MarkerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public interface MarkerDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String place_name, String place_description);
    }

    // Instanciar la interface
    MarkerDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verificar que el host implemento la interface
        try {
            // Cree una instancia de MarkerDialogListener para que podamos enviar eventos al host
            mListener = (MarkerDialogListener) context;
        } catch (ClassCastException e) {
            // La actividad no implementa la interfaz, lanza excepción
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
