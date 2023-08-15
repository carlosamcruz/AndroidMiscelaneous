package com.nibblelinx.MIRITI;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
//import android.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//import com.google.android.material.button.MaterialButton;


public class Token extends AppCompatActivity {

    //Notes banco;
    String myPassword;
    String myUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        Button buttonSEND = (Button) findViewById(R.id.ButtonTranfer);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //O verficador de pausa deve ser modificado em OnResume e OnCreate de cada Activity
        Variables.activityPause = false;
        //O contador de pausa deve estar presente em OnResume e OnCreate de cada Activity
        //Atenção especial para o uso de MAXSPECIALPAUSETIME
        Variables.timeCounter = Variables.MAXPAUSETIME;
        //O contador de Interação com Usuário deve também estar presente em OnResume e em OnCreate de cada activity
        //O contador deve ser resetado em OnResume, OnCreate, onUserInteraction de cada Activity
        Variables.userInteractionAct = Variables.MAXNOINTERACTIONTIME;

        //EditText PVTKEY = (EditText) findViewById(R.id.ET_LobbyAct_PVTKEY);
        TextInputEditText SendTo = findViewById(R.id.InputReceiverWallet);
        TextInputEditText Satoshis = findViewById(R.id.InputValue);
        TextInputEditText Data = findViewById(R.id.InputDescription);

        TextInputLayout SendToLayout = findViewById(R.id.InputReceiverWalletLayout);
        SendToLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScannerQrCode();
            }
        });

        //String pvtkey = PVTKEY.getText().toString();
        String sendTo = SendTo.getText().toString();
        String sats = Satoshis.getText().toString();
        String data = Data.getText().toString();

        ((TextView) findViewById(R.id.DisplayCurrentBalance)).setText("Saldo (Miritis): " + Variables.SatBalance + " mrts");
        ((TextView) findViewById(R.id.DisplayUserWallet)).setText(Variables.BSVWallet);


        buttonSEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String senha = MyPass

                //sendTX(pvtkey,sendTo, sats, data);

                //DialogWait(0);
                sendTX();
                //createAlertDialog(2, "ABC");

                //DialogWait(1);
                //Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance)/1000);

                //((TextView) findViewById(R.id.DisplayCurrentBalance)).setText("Saldo (Miritis): " + Variables.SatBalance + " mrts");
                //((TextView) findViewById(R.id.DisplayUserWallet)).setText(Variables.BSVWallet);
            }
        });
    }

    //final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Token.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
    private AlertDialog.Builder alertDialogBuilder;// = new AlertDialog.Builder(Token.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
    public void createAlertDialog(int id, String res) {
        LayoutInflater inflater = getLayoutInflater();
        View view = null;

        if (id == 1) {
            view = inflater.inflate(R.layout.alertdialog_error, null);
        } else if (id == 2) {
            view = inflater.inflate(R.layout.alertdialog_success, null);
            //view = inflater.inflate(R.layout.alertdialog_wait, null);
            //((TextView) findViewById(R.id.DisplayTXID)).setText("ABC");
        }

        if (view == null) {
            return;
        }

        if(alertDialogBuilder != null)
            alertDialogBuilder = null;

        //final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Token.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        alertDialogBuilder = new AlertDialog.Builder(Token.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        //((TextView) findViewById(R.id.DisplayTXID)).setText("TXID: "+res);

        alertDialog.show();

        switch (id) {
            case 1:
                Button buttonTryAgain = alertDialog.findViewById(R.id.ButtonTryAgain);
                buttonTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        ScannerQrCode();
                    }
                });
                break;

            case 2:

                //setContentView(R.layout.alertdialog_success);
                Button buttonConfirm = alertDialog.findViewById(R.id.ButtonConfirm);

                //((TextView) findViewById(R.id.DisplayTXID2)).setText("ABC");
                //TextInputEditText inputTXID2 = alertDialog.findViewById(R.id.DisplayTXID);
                TextInputEditText inputTXID = alertDialog.findViewById(R.id.InputTXID);
                inputTXID.setText(res);
                //inputTXID2.setText("abcd");

                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                break;

            default:
                break;
        }
    }


    private AlertDialog alertDialogWait;
    public void DialogWait(int STATE) {
        LayoutInflater inflater = getLayoutInflater();
        View view = null;

        view = inflater.inflate(R.layout.alertdialog_wait, null);

        if (view == null) {
            return;
        }

        if(STATE == 0) {
            //final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Token.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
            alertDialogBuilder = new AlertDialog.Builder(Token.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
            alertDialogBuilder.setView(view);

            alertDialogWait = alertDialogBuilder.create();
            alertDialogWait.show();

            TextInputEditText inputTXID = alertDialogWait.findViewById(R.id.InputPhase);

            switch (Variables.TxPhases) {
                case 1:
                    inputTXID.setText("Iniciando a Criação da Transação ... ");
                    break;

                case 2:
                    inputTXID.setText("Verificando Entradas da Transação ... ");
                    break;

                case 3:
                    inputTXID.setText("Criando as Saídas da Transação ... ");
                    break;

                case 4:
                    inputTXID.setText("Criando Pre-Imagens da Transação ... ");
                    break;

                case 5:
                    inputTXID.setText("Assinando Input " + Variables.TxPhasesNinp
                            + " de " + Variables.TxPhasesNinpTotal + " (Aguarde!!!)");
                    break;

                case 6:
                    inputTXID.setText("Enviando a Transação ..." );
                    break;

                default:
                    break;
            }




        }

        if(STATE == 1) {
            alertDialogWait.dismiss();
            alertDialogBuilder = null;
        }

    }



    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(Token.this, "Ação Cancelada", Toast.LENGTH_LONG).show();
                } else {
                    String contents = result.getContents();

                    if (contents.trim().length() == 34) {
                        TextInputEditText SendTo = findViewById(R.id.InputReceiverWallet);
                        SendTo.setText(contents);
                    } else {
                        createAlertDialog(1, null);
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

        qrCodeLauncher.launch(options);
    }


    Thread threadBuild;
    private void renewThreadBuild()
    {
        threadBuild = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    buildTXTH();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Para a execução na thread pricipal
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do onPostExecute stuff
                        //new NoConnection().onPostExecute(result);
                        sendTXPhase2();
                    }
                });
            }
        });
    }

    String newTX = "";

    public void buildTXTH()
    {
        //EditText PVTKEY = (EditText) findViewById(R.id.ET_LobbyAct_PVTKEY);
        TextInputEditText SendTo = findViewById(R.id.InputReceiverWallet);
        TextInputEditText Satoshis = findViewById(R.id.InputValue);
        TextInputEditText Data = findViewById(R.id.InputDescription);

        //String pvtkey = PVTKEY.getText().toString();

        String pvtkey = Variables.MainPaymail;

        String sendTo = SendTo.getText().toString();
        //String sendTo = "1B69q3ZY6VsuKwCinvbB5tkKWLjHWfGz1J";

        ///////////////////////////////////////////
        //Conversão para Miritis
        ///////////////////////////////////////////
        String sats = Satoshis.getText().toString();

        sats = sats + "000";

        ///////////////////////////////////////////
        ///////////////////////////////////////////


        //String sats = "1000";
        String data = Data.getText().toString();
        //String data = "Teste de mensagem mais longa.";
        //String data = "Teste N";
        //String data = "Teste N TTT t";//bad request



        //////////////////////////////////////////////////////////////////////////////////////////////////
        //Preparação das Chaves
        //////////////////////////////////////////////////////////////////////////////////////////////////
        Keygen pubKey = new Keygen();
        //Boolean CompPKey = true;

        String PUBKEY = pubKey.publicKeyHEX(pvtkey);

        String BSV160 = pubKey.bsvWalletRMD160(PUBKEY, Variables.CompKey);
        String BSVADD = pubKey.bsvWalletFull(PUBKEY, Variables.CompKey);


        /////////////////////////////////////////////////////////////////////
        //User Data Input
        /////////////////////////////////////////////////////////////////////

        String [] PayWallets = new String[10];
        String [] PayValues = new String[10];
        String [] OP_RETURNs = new String[10];

        //PayWallets[0] = "1B69q3ZY6VsuKwCinvbB5tkKWLjHWfGz1J"; //MoneyButton
        PayWallets[0] = sendTo; //Carteira para onde esta sendo enviado
        PayWallets[1] = BSVADD;
        //PayValues[0] = "1000";
        PayValues[0] = sats;
        //...at the name of Jesus every knee should bow, of things in heaven, and things in earth, and things under the earth;
        //OP_RETURNs[0] = "2e2e2e617420746865206e616d65206f66204a65737573206576657279206b6e65652073686f756c6420626f772c206f66207468696e677320696e2068656176656e2c20616e64207468696e677320696e2065617274682c20616e64207468696e677320756e646572207468652065617274683b";

        int nOR = 0;
        if(data.length() > 0) {
            OP_RETURNs[0] = StrToHex(data);
            nOR = 1;
        }

        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////

        BsvTxCreation txCreate = new BsvTxCreation();

        newTX = txCreate.txBuilder(pvtkey, Variables.CompKey,2 + nOR, PayWallets,PayValues,OP_RETURNs, nOR);

    }

    Thread threadSend;
    private void renewThreadSend()
    {
        threadSend = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    sendTXTH();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Para a execução na thread pricipal
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do onPostExecute stuff
                        //new NoConnection().onPostExecute(result);
                        sendTXPhase3();
                    }
                });
            }
        });
    }

    String result = "";
    public void sendTXTH() {

        BsvTxCreation txCreate = new BsvTxCreation();


        Variables.LastTxHexData = newTX;

        BsvTxOperations bsvTxOp = new BsvTxOperations();
        bsvTxOp.txID(newTX);
        Variables.LastTXID = bsvTxOp.TXID;

        result = txCreate.txBroadCast(newTX);
    }

    int phaseTx = 0;
    public void sendTX() {

        phaseTx = 0;
        Variables.TxPhases = 1;
        if (timer != null) {
            timer.cancel();
            timer.purge();
//            itest = 0;
        }

        timer = new Timer();
        timer.schedule(new Token.TimeCheck(), 0, 4000);

        //if(Variables.SatBalance!=null) {
        //    Toast.makeText(Token.this, "Construindo TX...", Toast.LENGTH_LONG).show();
        //    return;
        //}
        //Toast.makeText(Token.this, "Construindo TX...", Toast.LENGTH_LONG).show();
        renewThreadBuild();
        threadBuild.start();

//        phaseTx = 1;
//        DialogWait(0);

    }
    public void sendTXPhase2() {
        //DialogWait(0);

//        DialogWait(1);
//        phaseTx = 2;
//        DialogWait(0);

        if (newTX.length() > 5) {
            if (newTX.substring(0, 5).compareTo("Error") == 0) {
                Toast.makeText(Token.this, newTX
                        , Toast.LENGTH_LONG).show();
                return;
            }
        }

        Toast.makeText(Token.this, "Enviando Transação ...", Toast.LENGTH_LONG).show();
        renewThreadSend();
        threadSend.start();
    }
    public void sendTXPhase3()
    {
        if (timer != null) {
            timer.cancel();
            timer.purge();

            if(alertDialogBuilder != null)
                DialogWait(1);
//            itest = 0;
        }
//        DialogWait(1);
//        phaseTx = 3;
//        DialogWait(0);

//        DialogWait(1);
        Toast.makeText(Token.this, "Result: " + result
                , Toast.LENGTH_LONG).show();

//        DialogWait(1);
        createAlertDialog(2, result);

        //Valor final da carteira
        Variables.SatBalance = Long.toString(Long.valueOf(Variables.SatBalance)/1000);

        ((TextView) findViewById(R.id.DisplayCurrentBalance)).setText("Saldo (Miritis): " + Variables.SatBalance + " mrts");
        ((TextView) findViewById(R.id.DisplayUserWallet)).setText(Variables.BSVWallet);

    }

    Timer timer;

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
                            //new NoConnection().onPostExecute(result);
                            //sendTXPhase3();
                            if(alertDialogBuilder != null)
                                DialogWait(1);
                            phaseTx ++;
                            DialogWait(0);
                        }
                    });

                }
            }).start();


        }
    }



    public void sendTXOld2()
    {

        //if(Variables.SatBalance!=null) {
        //    Toast.makeText(Token.this, "Construindo TX...", Toast.LENGTH_LONG).show();
        //    return;
        //}
        //Toast.makeText(Token.this, "Construindo TX...", Toast.LENGTH_LONG).show();
        renewThreadBuild();
        threadBuild.start();

        //DialogWait(0);

        alertDialogWait = null;

        int cont = 0;


        Toast.makeText(Token.this, "Construindo TX...", Toast.LENGTH_LONG).show();
        while (threadBuild.isAlive())
        {

            //Toast.makeText(MainBCAPP.this, "Espere...", Toast.LENGTH_LONG).show();
            if(alertDialogWait==null) {
                DialogWait(0);
                String hashToHashs = "ABCDEF";
                hashToHashs = SHA256G.SHA256STR(hashToHashs);
                if(hashToHashs==null)
                    hashToHashs = SHA256G.SHA256STR("ABC");
                Toast.makeText(Token.this, "Espere...", Toast.LENGTH_LONG).show();
                cont ++;

            }

        }
        if(newTX.length()>5) {
            if (newTX.substring(0, 5).compareTo("Error") == 0) {
                Toast.makeText(Token.this, newTX
                        , Toast.LENGTH_LONG).show();
                return;
            }
        }

        Toast.makeText(Token.this, "Enviando TX...", Toast.LENGTH_LONG).show();
        renewThreadSend();
        threadSend.start();

        Toast.makeText(Token.this, "Enviando TX...", Toast.LENGTH_LONG).show();
        while (threadSend.isAlive())
        {

            //Toast.makeText(MainBCAPP.this, "Espere...", Toast.LENGTH_LONG).show();
        }

        Toast.makeText(Token.this, "Result: " + result + " " + cont
                , Toast.LENGTH_LONG).show();

        DialogWait(1);
        createAlertDialog(2, result);

    }


    //public void sendTX(String PVTKEY, String Wallet, String Value, String DATA)
    public void sendTXOLD()
    {

        //EditText PVTKEY = (EditText) findViewById(R.id.ET_LobbyAct_PVTKEY);
        TextInputEditText SendTo = findViewById(R.id.InputReceiverWallet);
        TextInputEditText Satoshis = findViewById(R.id.InputValue);
        TextInputEditText Data = findViewById(R.id.InputDescription);

        //String pvtkey = PVTKEY.getText().toString();

        String pvtkey = Variables.MainPaymail;

        String sendTo = SendTo.getText().toString();
        //String sendTo = "1B69q3ZY6VsuKwCinvbB5tkKWLjHWfGz1J";

        ///////////////////////////////////////////
        //Conversão para Miritis
        ///////////////////////////////////////////
        String sats = Satoshis.getText().toString();

        sats = sats + "000";

        ///////////////////////////////////////////
        ///////////////////////////////////////////


        //String sats = "1000";
        String data = Data.getText().toString();
        //String data = "Teste de mensagem mais longa.";
        //String data = "Teste N";
        //String data = "Teste N TTT t";//bad request



        //////////////////////////////////////////////////////////////////////////////////////////////////
        //Preparação das Chaves
        //////////////////////////////////////////////////////////////////////////////////////////////////
        Keygen pubKey = new Keygen();
        //Boolean CompPKey = true;

        String PUBKEY = pubKey.publicKeyHEX(pvtkey);

        String BSV160 = pubKey.bsvWalletRMD160(PUBKEY, Variables.CompKey);
        String BSVADD = pubKey.bsvWalletFull(PUBKEY, Variables.CompKey);


        /////////////////////////////////////////////////////////////////////
        //User Data Input
        /////////////////////////////////////////////////////////////////////

        String [] PayWallets = new String[10];
        String [] PayValues = new String[10];
        String [] OP_RETURNs = new String[10];

        //PayWallets[0] = "1B69q3ZY6VsuKwCinvbB5tkKWLjHWfGz1J"; //MoneyButton
        PayWallets[0] = sendTo; //Carteira para onde esta sendo enviado
        PayWallets[1] = BSVADD;
        //PayValues[0] = "1000";
        PayValues[0] = sats;
        //...at the name of Jesus every knee should bow, of things in heaven, and things in earth, and things under the earth;
        //OP_RETURNs[0] = "2e2e2e617420746865206e616d65206f66204a65737573206576657279206b6e65652073686f756c6420626f772c206f66207468696e677320696e2068656176656e2c20616e64207468696e677320696e2065617274682c20616e64207468696e677320756e646572207468652065617274683b";

        int nOR = 0;
        if(data.length() > 0) {
            OP_RETURNs[0] = StrToHex(data);
            nOR = 1;
        }

        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////

        BsvTxCreation txCreate = new BsvTxCreation();

        String newTX = txCreate.txBuilder(pvtkey, Variables.CompKey,2 + nOR, PayWallets,PayValues,OP_RETURNs, nOR);

        if(newTX.length()>5) {
            if (newTX.substring(0, 5).compareTo("Error") == 0) {
                Toast.makeText(Token.this, newTX
                        , Toast.LENGTH_LONG).show();
                return;
            }
        }

        String result = "";
        Variables.LastTxHexData = newTX;

        BsvTxOperations bsvTxOp = new BsvTxOperations();
        bsvTxOp.txID(newTX);
        Variables.LastTXID = bsvTxOp.TXID;

        result = txCreate.txBroadCast(newTX);

        //if(result.substring(0,5).compareTo("Error")==0) {
        //    Toast.makeText(Token.this, newTX
        //            , Toast.LENGTH_LONG).show();
        //    return;
        //}

        Toast.makeText(Token.this, "Result: " + result
                , Toast.LENGTH_LONG).show();
        createAlertDialog(2, result);
    }

    static public String StrToHex(String text)
    {
        char base16[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int numberOfNonChar = 0;

        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) > 0xFF) numberOfNonChar++;

        byte[] newTextChar = new byte[text.length() + numberOfNonChar];

        for (int i = 0, j = 0; i < text.length(); i++, j++) {
            if (text.charAt(i) > 0xFF) {
                newTextChar[j] = (byte) ((text.charAt(i) / 0x100) & 0xFF);
                j++;
                newTextChar[j] = (byte) (text.charAt(i) & 0xFF);
            } else {
                newTextChar[j] = (byte) (text.charAt(i) & 0xFF);
            }
        }

        return  SHA256G.ByteToStrHex(newTextChar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //https://stackoverflow.com/questions/4783960/call-method-when-home-button-pressed
    @Override
    public void onPause(){
        super.onPause();
        Variables.activityPause = true;
    }
    //https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/activity-lifecycle/pausing.html
    @Override
    public void onResume(){
        super.onResume();
        //O verficador de pausa deve ser modificado em OnResume e OnCreate de cada Activity
        Variables.activityPause = false;
        //O contador de pausa deve estar presente em OnResume e OnCreate de cada Activity
        //Atenção especial para o uso de MAXSPECIALPAUSETIME
        Variables.timeCounter = Variables.MAXPAUSETIME;
        //O contador de Interação com Usuário deve também estar presente em OnResume e em OnCreate de cada activity
        //O contador deve ser resetado em OnResume, OnCreate, onUserInteraction de cada Activity
        Variables.userInteractionAct = Variables.MAXNOINTERACTIONTIME;
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