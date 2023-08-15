package com.nibblelinx.MIRITI;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainBCAPP extends AppCompatActivity {

    //public static Activity fa;

    int inicio = 0;


    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homesceen);
       // Button buttonMS1 = (Button) findViewById(R.id.buttonMS1);
        Button buttonMS2 = (Button) findViewById(R.id.btReadRegister);
       // Button buttonMS4 = (Button) findViewById(R.id.buttonMS4);
        Button btnReadKey = (Button) findViewById(R.id.btReadKey);
        Button buttonMS7 = (Button) findViewById(R.id.btTransfer);
        //Button buttonMS8 = (Button) findViewById(R.id.buttonMS8);

        //fa = this;


        //O verficador de pausa deve ser modificado em OnResume e OnCreate de cada Activity
        Variables.activityPause = false;
        //O contador de pausa deve estar presente em OnResume e OnCreate de cada Activity
        //Atenção especial para o uso de MAXSPECIALPAUSETIME
        Variables.timeCounter = Variables.MAXPAUSETIME;
        //O contador de Interação com Usuário deve também estar presente em OnResume e em OnCreate de cada activity
        //O contador deve ser resetado em OnResume, OnCreate, onUserInteraction de cada Activity
        Variables.userInteractionAct = Variables.MAXNOINTERACTIONTIME;

        Variables.SatBalance = "";
        Variables.MainPaymail = "";


        buttonMS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Variables.MainPaymail.length() != 64)
                {
                    Toast.makeText(MainBCAPP.this, "Informe uma Chave Válida!!!", Toast.LENGTH_LONG).show();
                }

                else {

                    Keygen pubKey = new Keygen();
                    //Boolean CompPKey = true;
                    Variables.CompKey = true;

                    String PUBKEY = pubKey.publicKeyHEX(Variables.MainPaymail);//Variables.MainPaymail hex 64 elementos
                    String BSV160 = pubKey.bsvWalletRMD160(PUBKEY, Variables.CompKey);
                    String BSVADD = pubKey.bsvWalletFull(PUBKEY, Variables.CompKey);


                    Intent it = new Intent(MainBCAPP.this, TxidList.class);
                    Variables.MyNFTs = false;
                    //Este é o endereço para os dados serão enviados
                    //Deste endereço os dados também serão lidos
                    //É importante você ter o controle sobre este endereço
                    //Ou seja, que faça parte de uma das carteiras das quais você tem acesso
                    it.putExtra("NFTIndex", BSVADD);
                    startActivity(it);
                }
            }
        });


        buttonMS7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String senha = MyPass

                if(Variables.MainPaymail.length() != 64)
                {
                    Toast.makeText(MainBCAPP.this, "Informe uma Chave Válida!!!", Toast.LENGTH_LONG).show();
                }
                else if(Variables.SatBalance.compareTo("0")==0)
                {
                    Toast.makeText(MainBCAPP.this, "Informe uma Carteira com Fundos!!!", Toast.LENGTH_LONG).show();
                }
                else {

                    Intent it = new Intent(MainBCAPP.this, Token.class);
                    startActivity(it);

                }



            }
        });


        btnReadKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Id 1: AlertDialog Choose Key Input
                createAlertDialog(1);
            }
        });

    }
    //Boolean CompPKey = true;

    public void createAlertDialog(int id) {
        LayoutInflater inflater = getLayoutInflater();
        View view = null;

        if (id == 1) {
            // Id 1: Choose Key Input
            view = inflater.inflate(R.layout.alertdialog_choosekeyinput, null);
        }

        if (view == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainBCAPP.this);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btnTypeKey = alertDialog.findViewById(R.id.BtnTypeInput);
        Button btnQrcode = alertDialog.findViewById(R.id.BtnQrCode);

        btnTypeKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //createAlertDialog2(2);
                createAlertDialogDIG(1);
                alertDialog.dismiss();
            }
        });

        btnQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScannerQrCode();
                alertDialog.dismiss();
            }
        });
    }


    public void createAlertDialogDIG(int id) {
        LayoutInflater inflater = getLayoutInflater();
        View view = null;

        if (id == 1) {
            // Id 1: Choose Key Input
            view = inflater.inflate(R.layout.alertdialog_senha_chave, null);
        }

        if (view == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainBCAPP.this);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btnSenha = alertDialog.findViewById(R.id.btnSenha);
        Button btnChave = alertDialog.findViewById(R.id.btnChave);

        btnSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAlertDialogSENHA(2);
                alertDialog.dismiss();
            }
        });

        btnChave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ScannerQrCode();
                createAlertDialog2(2);
                alertDialog.dismiss();
            }
        });
    }

    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(MainBCAPP.this, "Ação Cancelada", Toast.LENGTH_LONG).show();
                } else {
                    Variables.MainPaymail = result.getContents();
                    //if(Variables.MainPaymail.length() != 64)
                    //    return;
                    if (Variables.MainPaymail.length() == 64) {
                        Variables.CompKey = true;
                        QRCODE = true;
                        setAddValue();
                    }
                }
            });

    private void ScannerQrCode() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Escanear Qr Code");
        //options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);

        //return;

        qrCodeLauncher.launch(options);
    }

    public void createAlertDialog2(int id) {
        LayoutInflater inflater = getLayoutInflater();
        View view = null;

        if (id == 2) {
            // Id 2: Insert Key By Type
            view = inflater.inflate(R.layout.alertdialog_insertkeybytype, null);
        }

        if (view == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainBCAPP.this);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btnAccess = alertDialog.findViewById(R.id.btnAcess);
        TextInputEditText inputKey = alertDialog.findViewById(R.id.InputKey);

        //Mostra a chave atual
        inputKey.setText(Variables.MainPaymail);

        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.MainPaymail = inputKey.getText().toString();

                if (Variables.MainPaymail.length() == 64) {
                    Variables.CompKey = true;
                    QRCODE = false;
                    setAddValue();
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void createAlertDialogSENHA(int id) {
        LayoutInflater inflater = getLayoutInflater();
        View view = null;

        if (id == 2) {
            // Id 2: Insert Key By Type
            view = inflater.inflate(R.layout.alertdialog_senha, null);
        }

        if (view == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainBCAPP.this);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btnAccess = alertDialog.findViewById(R.id.btnAcess);
        TextInputEditText inputKey = alertDialog.findViewById(R.id.InputKey);

        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.MainPaymail = inputKey.getText().toString();

                if (Variables.MainPaymail.length() >= 8) {
                    Variables.CompKey = true;

                    QRCODE = false;
                    ChaveDaSenha();
                    //setAddValue();
                    alertDialog.dismiss();
                }
            }
        });
    }
    private void ChaveDaSenha()
    {
        Variables.MainPaymail = SHA256G.SHA256STR(Variables.MainPaymail);

        if(Variables.MainPaymail == null)
        {
            Toast.makeText(MainBCAPP.this, "Senha Invalida!!", Toast.LENGTH_LONG).show();
            return;
        }

        Variables.MainPaymail = SHA256G.LEformat(Variables.MainPaymail);
        Variables.MainPaymail = SHA256G.SHA256bytes( SHA256G.HashStrToByte2(Variables.MainPaymail));

        //Toast.makeText(MainBCAPP.this, Variables.MainPaymail, Toast.LENGTH_LONG).show();

        QRCODE = false;
        setAddValue();
    }

    private Bitmap GenerateQrCode(String data) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.rgb(0xF4, 0xF4, 0xF4));
                }
            }

            return bitmap;
        } catch (WriterException e) {
            // throw new RuntimeException(e);
            e.printStackTrace();
        }

        return null;
    }

    Thread thread;
    private void renewThread()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    setAddValueTH();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setAddValueTH() {
        Variables.CompKey = true;
        Keygen pubKey = new Keygen();




        String PUBKEY = pubKey.publicKeyHEX(Variables.MainPaymail);
        String BSVADD = pubKey.bsvWalletFull(PUBKEY, Variables.CompKey);
        Variables.BSVWallet = BSVADD;

        BsvTxCreation txCreate = new BsvTxCreation();
        //BsvAddUnspent txCreate = new BsvAddUnspent();

        //Variables.STREAMTT ++;
        Variables.SatBalance = txCreate.totalUnspent(BSVADD);



        //Variables.SatBalance = "Error: XXX";
        //Variables.SatBalance = "Error: XXX";


    }

    Boolean QRCODE = false;

    public void setAddValue()
    {

        //Variables.SatBalance = Variables.MainPaymail;
        renewThread();
        thread.start();


        Toast.makeText(MainBCAPP.this, "Espere...", Toast.LENGTH_LONG).show();
        while (thread.isAlive())
        {

            //Toast.makeText(MainBCAPP.this, "Espere...", Toast.LENGTH_LONG).show();

        }

        //Só libera a consulta em caso positivo
        //inicio = 0;
        ImageView imageQrCode = findViewById(R.id.ImageQrCode);
        //imageQrCode.clearAnimation();

        if(Variables.SatBalance.length() > 5) {
            if (Variables.SatBalance.substring(0, 5).compareTo("Error") == 0) {

                //Toast.makeText(MainBCAPP.this, "Falha ao Ler o Valor", Toast.LENGTH_LONG).show();
                //Toast.makeText(MainBCAPP.this, "Falha na Comunicação!!!", Toast.LENGTH_LONG).show();

                //startPrint = false;
                //inicio = -1;
                //Variables.SatBalance = "";

                if(timer != null)
                {

                    timer.cancel();
                    timer.purge();
                    //           itest = 0;
                }

                Variables.SatBalance = Long.toString(0);

                ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);

                Bitmap bitmap = GenerateQrCode(Variables.BSVWallet);

                if(bitmap==null)
                    return;

                //ImageView imageQrCode = findViewById(R.id.ImageQrCode);
                imageQrCode.setImageBitmap(bitmap);
                //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance)/1000 + " Miritis");
                String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
                //String valueOfBalance = Variables.SatBalance + " MRT";
                ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);


                Toast.makeText(MainBCAPP.this, "Falha na Comunicação!!!", Toast.LENGTH_LONG).show();

                if(!QRCODE) {

                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
//            itest = 0;
                    }

                    timer = new Timer();
                    timer.schedule(new TimeCheck(), 3500, 5000);
                }

                return;
            }
        }

        //if(Variables.SatBalance.substring(0,5).compareTo("Error") == 0) {
        if(Variables.SatBalance != null) {

            Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance)/1000);

            ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);

            Bitmap bitmap = GenerateQrCode(Variables.BSVWallet);

            if(bitmap==null)
                return;

            //ImageView imageQrCode = findViewById(R.id.ImageQrCode);
            imageQrCode.setImageBitmap(bitmap);

            //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance)/1000 + " Miritis");

            String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
            //String valueOfBalance = Variables.SatBalance + " MRT";


            ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);

            //Toast.makeText(MainBCAPP.this, "Falha ao Ler o Valor", Toast.LENGTH_LONG).show();
            //Toast.makeText(MainBCAPP.this, Variables.SatBalance, Toast.LENGTH_LONG).show();

            startPrint = true;
            return;
        }
        //Revisar este if


        ///////////////////////////////////////////////////////
        //MIRITIS
        ///////////////////////////////////////////////////////

        Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance)/1000);


        //Variables.SatBalance = "7";
        //txCreate.totalUnspent(BSVADD);
        //((TextView) findViewById(R.id.TV_TEXT2)).setText("Balance (Satoshis): " + Variables.SatBalance + " sats");
        ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);

        Bitmap bitmap = GenerateQrCode(Variables.BSVWallet);

        if(bitmap==null)
            return;

        //ImageView imageQrCode = findViewById(R.id.ImageQrCode);
        imageQrCode.setImageBitmap(bitmap);

        //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance)/1000 + " Miritis");

        String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
        //String valueOfBalance = Variables.SatBalance + " MRT";


        ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);

        if(timer != null)
        {

            timer.cancel();
            timer.purge();
//            itest = 0;
        }

        timer = new Timer();
        timer.schedule(new TimeCheck(), 3500, 5000);
        //printValue ();

    }



    public void setAddValueOLD()
    {
        Variables.CompKey = true;
        Keygen pubKey = new Keygen();


        String PUBKEY = pubKey.publicKeyHEX(Variables.MainPaymail);
        String BSVADD = pubKey.bsvWalletFull(PUBKEY, Variables.CompKey);
        Variables.BSVWallet = BSVADD;

        BsvTxCreation txCreate = new BsvTxCreation();
        //BsvAddUnspent txCreate = new BsvAddUnspent();

        //Variables.STREAMTT ++;
        Variables.SatBalance = txCreate.totalUnspent(BSVADD);



        //if(Variables.SatBalance.substring(0,5).compareTo("Error") == 0) {
        if(Variables.SatBalance != null) {

            Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance)/1000);

            ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);

            Bitmap bitmap = GenerateQrCode(Variables.BSVWallet);

            if(bitmap==null)
                return;

            ImageView imageQrCode = findViewById(R.id.ImageQrCode);
            imageQrCode.setImageBitmap(bitmap);

            //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance)/1000 + " Miritis");

            String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
            //String valueOfBalance = Variables.SatBalance + " MRT";


            ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);

            //Toast.makeText(MainBCAPP.this, "Falha ao Ler o Valor", Toast.LENGTH_LONG).show();
            //Toast.makeText(MainBCAPP.this, Variables.SatBalance, Toast.LENGTH_LONG).show();
            return;
        }
        //Revisar este if
        else if(Variables.SatBalance.length() > 5) {
            if (Variables.SatBalance.substring(0, 5).compareTo("Error") == 0) {
                //Toast.makeText(MainBCAPP.this, "Falha ao Ler o Valor", Toast.LENGTH_LONG).show();
                Toast.makeText(MainBCAPP.this, Variables.SatBalance, Toast.LENGTH_LONG).show();
                return;
            }
        }


        ///////////////////////////////////////////////////////
        //MIRITIS
        ///////////////////////////////////////////////////////

        Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance)/1000);


        //Variables.SatBalance = "7";
        //txCreate.totalUnspent(BSVADD);
        //((TextView) findViewById(R.id.TV_TEXT2)).setText("Balance (Satoshis): " + Variables.SatBalance + " sats");
        ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);

        Bitmap bitmap = GenerateQrCode(Variables.BSVWallet);

        if(bitmap==null)
            return;

        ImageView imageQrCode = findViewById(R.id.ImageQrCode);
        imageQrCode.setImageBitmap(bitmap);

        //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance)/1000 + " Miritis");

        String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
        //String valueOfBalance = Variables.SatBalance + " MRT";


        ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);

        if(timer != null)
        {

            timer.cancel();
            timer.purge();
//            itest = 0;
        }



        timer = new Timer();
        timer.schedule(new TimeCheck(), 3500, 5000);
        //printValue ();


    }

    //BsvTxCreation txCreate = new BsvTxCreation();
    class TimeCheck extends TimerTask
    {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void run()
        {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //Variables.SatBalance = txCreate.totalUnspent(Variables.BSVWallet);
                    //Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance) / 1000);
                    //printValue ();


                    //  timer.cancel();
                    //  timer.purge();
                    //result = new JsonTaskTXID().execute(urlBaseTXID2);;
                    //Looper.loop();
                    //Para a execução na thread pricipal
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // do onPostExecute stuff
                            printValue ();
                        }
                    });


                }
            }).start();


        }
    }
    //int itest = 0;
    boolean startPrint = false;
    private void printValue ()
    {
        BsvTxCreation txCreate = new BsvTxCreation();
        //BsvAddUnspent txCreate = new BsvAddUnspent();

        Variables.SatBalance = txCreate.totalUnspent(Variables.BSVWallet);


        //Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance) / 1000) + " " + itest;

        if(Variables.SatBalance.length() > 5) {
            if (Variables.SatBalance.substring(0, 5).compareTo("Error") == 0) {

                //Toast.makeText(MainBCAPP.this, "Falha ao Ler o Valor " + inicio, Toast.LENGTH_LONG).show();
                //Toast.makeText(MainBCAPP.this, "Falha ao Ler o Valor ", Toast.LENGTH_LONG).show();
                Toast.makeText(MainBCAPP.this, "Falha na Comunicação!!!", Toast.LENGTH_LONG).show();
                //startPrint = false;
                //inicio = 0;
                Variables.SatBalance = Long.toString(0);
                return;
            }
        }


        Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance) / 1000);
        //Variables.SatBalance = Variables.SatBalance + " " + itest;


                //((TextView) findViewById(R.id.TV_TEXT2)).setText("Balance (Satoshis): " + Variables.SatBalance + " sats");
        //((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet + " " + itest);
        ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);
        //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance) / 1000 + " Miritis");
        String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
        //String valueOfBalance = Variables.SatBalance + " MRT";

        ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);
        //startPrint = true;

//        itest++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_txidlist, menu);
        return true;
    }

    //https://stackoverflow.com/questions/4783960/call-method-when-home-button-pressed
    @Override
    public void onPause(){
        super.onPause();

//        timer.cancel();
//        timer.purge();

        startPrint = false;

        if(timer != null)
        {

            timer.cancel();
            timer.purge();
 //           itest = 0;
        }

        Variables.activityPause = true;
    }
    int flag = 0;
    //https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/activity-lifecycle/pausing.html
    @Override
    public void onResume(){
        super.onResume();
        //Variables.activityPause = false;

        //O verficador de pausa deve ser modificado em OnResume e OnCreate de cada Activity
        Variables.activityPause = false;
        //O contador de pausa deve estar presente em OnResume e OnCreate de cada Activity
        //Atenção especial para o uso de MAXSPECIALPAUSETIME
        Variables.timeCounter = Variables.MAXPAUSETIME;
        //O contador de Interação com Usuário deve também estar presente em OnResume e em OnCreate de cada activity
        //O contador deve ser resetado em OnResume, OnCreate, onUserInteraction de cada Activity
        Variables.userInteractionAct = Variables.MAXNOINTERACTIONTIME;

        if(timer != null)
        {

            timer.cancel();
            timer.purge();
            //           itest = 0;
        }

        if(startPrint) {
            timer = new Timer();
            timer.schedule(new TimeCheck(), 0, 5000);
        }


        if(inicio == 0) {
            inicio = 1;
        }
        else
        {
           //if(Variables.SatBalance.length() > 0 && startPrint) {
            if(Variables.SatBalance.length() > 0) {

               startPrint = true;

                if(timer != null)
                {

                    timer.cancel();
                    timer.purge();
                    //           itest = 0;
                }

               timer = new Timer();
               timer.schedule(new TimeCheck(), 0, 5000);

               //Não tem porque executar esta ação abaixo:
/*
               BsvTxCreation txCreate = new BsvTxCreation();
               //Variables.STREAMTT ++;
               Variables.SatBalance = txCreate.totalUnspent(Variables.BSVWallet);
               Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance) / 1000);

               //((TextView) findViewById(R.id.TV_TEXT2)).setText("Balance (Satoshis): " + Variables.SatBalance + " sats");
               ((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);
               //((TextView) findViewById(R.id.TV_TEXT4)).setText("Saldo (Miritis): " + Long.valueOf(Variables.SatBalance) / 1000 + " Miritis");
               String valueOfBalance = String.format(Locale.getDefault(), "%.2f", Float.valueOf(Variables.SatBalance)) + " MRT";
               ((TextView) findViewById(R.id.TV_TEXT4)).setText(valueOfBalance);
*/
           }
        }

        //((TextView) findViewById(R.id.TV_TEXT2)).setText("Balance: " + Variables.SatBalance + " sats");
        //((TextView) findViewById(R.id.TV_TEXT3)).setText(Variables.BSVWallet);

    }

    //Monitora intecação do usuário com a aplicação
    //O contador deve também estar presente em OnResume e em OnCreate de cada activity
    //Se o contador atingir 0 a aplicação encerra
    //https://stackoverflow.com/questions/4208730/how-to-detect-user-inactivity-in-android
    @Override
    public void onUserInteraction(){
        super.onUserInteraction();
        //O contador de Interação com Usuário deve também estar presente em OnResume e em OnCreate de cada activity
        //O contador deve ser resetado em OnResume, OnCreate, onUserInteraction de cada Activity
        Variables.userInteractionAct = Variables.MAXNOINTERACTIONTIME;


    }
}