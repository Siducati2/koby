package com.innohawk.dan.media;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.R;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AddMediaActivity extends ActivityBase {
    private static final String TAG = "AddMediaActivity";
    protected static final int CAMERA_ACTIVITY = 0;
    protected static final int PICKER_ACTIVITY = 1;

    protected String sGalleryFilesType;
    protected Button m_buttonFromCamera;
    protected Button m_buttonFromGallery;
    protected EditText m_editTitle;
    protected EditText m_editTags;
    protected EditText m_editDesc;
    protected ImageView m_viewFileThumb;
    protected String m_sAlbumName;
    protected AddMediaActivity m_actAddMedia;
    protected TextView file, title, tag, desc;

    //Permission
    private static final int MY_PERMISSION_VAL = 0; //Tiene implicito el READ (que es el que necesitamos
    View layoutHomePermission;
    RelativeLayout rl_dialog;

    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_submit;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false);

        m_actAddMedia = this;

        setContentView(R.layout.media_add);


        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_media_add);
        textTitle.setTypeface(tt);

        file = (TextView) findViewById(R.id.text_file);
        file.setTypeface(tt);
        title = (TextView) findViewById(R.id.text_title);
        title.setTypeface(tt);
        tag = (TextView) findViewById(R.id.text_tag);
        tag.setTypeface(tt);
        desc = (TextView) findViewById(R.id.text_desc);
        desc.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Submit
        btn_action_submit = (Button) findViewById(R.id.buttonsubmit);
        btn_action_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSubmitFile();
            }
        });

        m_buttonFromCamera = (Button) findViewById(R.id.media_btn_from_camera);
        m_buttonFromGallery = (Button) findViewById(R.id.media_btn_from_gallery);
        m_editTitle = (EditText) findViewById(R.id.media_title);
        m_editTags = (EditText) findViewById(R.id.media_tags);
        m_editDesc = (EditText) findViewById(R.id.media_desc);
        m_viewFileThumb = (ImageView) findViewById(R.id.media_file_preview);

        Intent i = getIntent();
        m_sAlbumName = i.getStringExtra("album_name");

        /*Add code innoHawk*/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            m_buttonFromCamera.setVisibility(View.INVISIBLE);
            m_buttonFromGallery.setVisibility(View.INVISIBLE);
        }else {
            m_buttonFromGallery.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent filePickerIntent = new Intent(Intent.ACTION_PICK);
                    filePickerIntent.setType(sGalleryFilesType);
                    startActivityForResult(filePickerIntent, PICKER_ACTIVITY);
                }
            });
        }
        verifyPermissionRequest();
        /*End Code*/
    }

    protected void actionSubmitFile() {
        // overridded in child classes
    }

    protected void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    protected void showToast(int i) {
        showToast(getString(i));
    }

    protected String getRealPathFromURI(Uri contentUri) {
        if (contentUri.toString().startsWith("file://"))
            return contentUri.toString().replace("file://", "");
        return getDataFromURI(MediaStore.Images.Media.DATA, contentUri);
    }

    protected String getDataFromURI(String sData, Uri contentUri) {
        String[] proj = {sData};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(sData);
        if (!cursor.moveToFirst())
            return null;
        return cursor.getString(column_index);
    }

    protected static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    protected static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    protected void showErrorDialog(int iErrorMsg, boolean isFinishOnClose) {
        showErrorDialog(getString(iErrorMsg), isFinishOnClose);
    }

    protected void showErrorDialog(String sErrorMsg, boolean isFinishOnClose) {
        AlertDialog dialog = new AlertDialog.Builder(m_actAddMedia).create();
        dialog.setTitle(R.string.media_popup_error_title);
        dialog.setMessage(sErrorMsg);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.close), new CustomOnClickListener(isFinishOnClose));
        dialog.show();
    }

    protected class CustomOnClickListener implements DialogInterface.OnClickListener {
        protected boolean m_isFinishOnClose;

        public CustomOnClickListener(boolean isFinishOnClose) {
            m_isFinishOnClose = isFinishOnClose;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
            if (m_isFinishOnClose)
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.media_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.media_submit:
                actionSubmitFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isFileTooBig(double fileSize, boolean isDisplayError) {
        double max = Runtime.getRuntime().maxMemory(); //the maximum memory the app can use
        double heapSize = Runtime.getRuntime().totalMemory(); //current heap size
        double heapRemaining = Runtime.getRuntime().freeMemory(); //amount available in heap
        double nativeUsage = Debug.getNativeHeapAllocatedSize(); //is this right? I only want to account for native memory that my app is being "charged" for.  Is this the proper way to account for that?
        double remaining = max - (heapSize - heapRemaining + nativeUsage); //heapSize - heapRemaining = heapUsed + nativeUsage = totalUsage

        double potentialMemoryRequired = 2.5 * 1024 * 1024 + fileSize + (fileSize * 1.37) * 4; // potential required memory is 2.5Mb(service methids usage) + binary data + two strings of 64base encoded data (1 char is 2 bytes in java)

        Log.i(TAG, "----------------------------------------");
        Log.i(TAG, String.format("File size: %.2f Mb", fileSize / 1024 / 1024));
        Log.i(TAG, String.format("Memory - max:%.2fMb   heapSize:%.2fMb   heapRemaining:%.2fMb   nativeUsage:%.2fMb", max / 1024 / 1024, heapSize / 1024 / 1024, heapRemaining / 1024 / 1024, nativeUsage / 1024 / 1024));
        Log.i(TAG, String.format("Memory remaining: %.2f Mb", remaining / 1024 / 1024));
        Log.i(TAG, String.format("Memory requred: %.2f Mb", potentialMemoryRequired / 1024 / 1024));
        Log.i(TAG, "----------------------------------------");

        if (remaining > potentialMemoryRequired)
            return false;

        if (isDisplayError)
            showErrorDialog(R.string.media_error_file_too_big, false);

        return true;
    }

    /* Start code innoHawk permission Request */

    //Paso 1. Verificar permiso
    @SuppressLint("WrongConstant")
    private void verifyPermissionRequest() {
        //WRITE_EXTERNAL_STORAGE tiene implícito READ_EXTERNAL_STORAGE porque pertenecen al mismo grupo de permisos
        int camara = 0;
        int write = 0;
        int micro = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            camara = checkSelfPermission(android.Manifest.permission.CAMERA);
            write = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            micro = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO);
        }
        if ((camara != PackageManager.PERMISSION_GRANTED)||(write != PackageManager.PERMISSION_GRANTED)||(micro != PackageManager.PERMISSION_GRANTED)) {
            requestPermission();
        } else {
            doAction();
        }
    }

    //Paso 2: Solicitar permiso
    private void requestPermission() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if( (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            showSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_VAL);
            }
        }
    }

    //Paso 3: Procesar respuesta de usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_PERMISSION_VAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doAction();
            } else {
                showSnackBar();
            }
        }
    }


    /**
     * Método para mostrar el snackbar de la aplicación.
     * Snackbar es un componente de la librería de diseño 'com.android.support:design:23.1.0'
     * y puede ser personalizado para realizar una acción, como por ejemplo abrir la actividad de
     * configuración de nuestra aplicación.
     */
    private void showSnackBar() {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
            layoutHomePermission = getLayoutInflater().inflate(R.layout.activity_permission, rl_dialog, false);
            rl_dialog.addView(layoutHomePermission);
            TextView txt_name_permission=(TextView)findViewById(R.id.txt_dia);
            txt_name_permission.setText(R.string.permission_request_media);
            Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
            Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialog.setVisibility(View.GONE);
                    doAction();
                }
            });
            btn_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    openSettings();
                    Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
                    Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
                    btn_yes.setText("OK");
                    btn_settings.setVisibility(View.GONE);
                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            rl_dialog.setVisibility(View.GONE);
                            doAction();
                        }
                    });
                }
            });
        }
    }

    /**
     * Abre el intento de detalles de configuración de nuestra aplicación
     */
    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Hace la Accion
     */
    private void doAction() {
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        m_buttonFromCamera = (Button) findViewById(R.id.media_btn_from_camera);
        m_buttonFromGallery = (Button) findViewById(R.id.media_btn_from_gallery);
        m_editTitle = (EditText) findViewById(R.id.media_title);
        m_editTags = (EditText) findViewById(R.id.media_tags);
        m_editDesc = (EditText) findViewById(R.id.media_desc);
        m_viewFileThumb = (ImageView) findViewById(R.id.media_file_preview);
        m_buttonFromCamera.setVisibility(View.VISIBLE);
        m_buttonFromGallery.setVisibility(View.VISIBLE);
        m_buttonFromCamera.setTypeface(tt);
        m_buttonFromGallery.setTypeface(tt);
        Intent i = getIntent();
        m_sAlbumName = i.getStringExtra("album_name");

        m_buttonFromGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent filePickerIntent = new Intent(Intent.ACTION_PICK);
                filePickerIntent.setType(sGalleryFilesType);
                startActivityForResult(filePickerIntent, PICKER_ACTIVITY);
            }
        });

    }

    /* End Code */
}
