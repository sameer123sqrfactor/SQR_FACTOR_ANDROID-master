package com.user.sqrfactor.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Utils.FileDownloader;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValentineEventFragment extends DialogFragment {


    public ValentineEventFragment() {
        // Required empty public constructor
    }

    TextView ok;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_valentine_event, null);

        builder.setView(view);

        ok = view.findViewById(R.id.ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sqrfactor.sfo2.digitaloceanspaces.com/feb_brief.pdf"));
                Intent chooser = Intent.createChooser(intent, "Select Browser");
                getActivity().startActivity(chooser);

//                String extStorageDirectory = Environment.getExternalStorageDirectory()
//                        .toString();
//                File folder = new File(extStorageDirectory, "SqrfactorImages");
//                folder.mkdir();
//                File file = new File(folder, "pdf_open_parameters.pdf");
//                try {
//                    file.createNewFile();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                  FileDownloader.DownloadFile("https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/pdf_open_parameters.pdf", file);

            }
        });

        return builder.create();
    }

}
