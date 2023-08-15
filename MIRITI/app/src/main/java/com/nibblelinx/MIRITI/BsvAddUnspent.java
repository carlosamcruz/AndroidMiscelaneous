package com.nibblelinx.MIRITI;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class BsvAddUnspent {

    private static int a;

    String urlBaseTXID = "";
    //Timer timer;
    //int dalyWhatsOnChain = 0;
    String unsPentInputs;
    //Boolean threadEndReadBsvAddsUnspent = false;


    public  String totalUnspent (String BSVADD)
    {
        /////////////////////////////////////////////////////////////////////
        //Unspent TX treatment do Endereço da Chave privada Indicada pelo Usruário
        /////////////////////////////////////////////////////////////////////

        unsPentInputs = null;
        //unsPentInputs = BSVADD;
        //Variables.unsPentInputs = null;
//        timer = new Timer();
        //tickerThis = new Timer();
        readBsvAddsUnspent(BSVADD);
        String unspentTX = "";


        //Primeiro
        //while (!threadEndReadBsvAddsUnspent)
        //while (unsPentInputs == null)
        //while (unsPentInputs.compareTo(BSVADD) == 0)
        //while (Variables.unsPentInputs == null)
        //while (loopMonitor == 0)
        while (thread.isAlive())
        {
            unspentTX = "";

        }

        //if(bsvTX.unsPentInputs == null)
        //    return ("erro");

        //bsvTX.unsPentInputs = Variables.unsPentInputs;
        unspentTX = unsPentInputs;

        //tickerThis.cancel();
        //tickerThis.purge();

//        timer.cancel();
//        timer.purge();

        if(unsPentInputs == null)
            //return  "Error: Time out reading Unspent TX inputs" + " " + BSVADD;
            return  "Error: reading Unspent TX inputs fail";

        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////

        int nInp = unspentUTXO(unspentTX);
        long totalValue = 0;


        /////////////////////////////////////////////////////////////////////
        //Verificação de quanto tem para gastar
        /////////////////////////////////////////////////////////////////////
        for (int i =0; i < nInp; i++)
        {
            //unspentParts += bsvTX.unspentIndex[i] + "\n" + bsvTX.unspentTXID[i] + "\n" + bsvTX.unspentValue[i] + "\n";
            //totalValue +=  Integer.decode(bsvTX.unspentValue[i]);
            totalValue +=  Long.valueOf(unspentValue[i]);

        }

        //totalValueHex = Long.toHexString(totalValue);
        return Long.toString(totalValue);

        //return "Error: " + unsPentInputs;
        //return "Error: " + nInp + " " + unspentValue[0];
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {

                unsPentInputs = JsonTaskTXIDNew(urlBaseTXID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    public void readBsvAddsUnspent(String BSVADD){

        urlBaseTXID = "https://api.whatsonchain.com/v1/bsv/main/address/" + BSVADD +  "/unspent";

//        threadEndReadBsvAddsUnspent = false;

        //timer.schedule(new TimeCheckURL(), 0, 5000);

        //O Processo a seguir foi testado e não pode ser executado fora de uma thread
        //timer.schedule(new TimeCheckURL2(), dalyWhatsOnChain, 5000);

        thread.start();
    }

    private String JsonTaskTXIDNew(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch(Exception e) {
            //e.printStackTrace();
            //unsPentInputs = content.toString();
            return null;
        }

        //threadEndReadBsvAddsUnspent = true;
        //unsPentInputs = content.toString();
        return content.toString();
    }


    String[] unspentIndex = new String[1000];
    String[] unspentValue = new String[1000];
    String[] unspentTXID = new String[1000];

    public int unspentUTXO (String unspentUTXOstring)
    {
        String[] parts = new String[1000];

        String search1 = "\"tx_pos\":";
        String search2 = "\"tx_hash\":\"";
        String search3 = "\"value\":";

        //jsonStrTXID = params[0];
        int firstIndiceOf;
        int nextIndex;

        //https://www.geeksforgeeks.org/searching-for-character-and-substring-in-a-string/

        firstIndiceOf = unspentUTXOstring.indexOf(search1);

        int i = 0;

        while (firstIndiceOf != -1) {

            //nextIndex = jsonStrTXID.indexOf("\"", firstIndiceOf + searchStrOPRETURN.length());
            nextIndex = unspentUTXOstring.indexOf(",", firstIndiceOf + search1.length());
            unspentIndex[i] = unspentUTXOstring.substring(firstIndiceOf + search1.length(), nextIndex);

            firstIndiceOf = unspentUTXOstring.indexOf(search2, nextIndex);
            nextIndex = unspentUTXOstring.indexOf("\"", firstIndiceOf + search2.length());
            unspentTXID[i] = unspentUTXOstring.substring(firstIndiceOf + search2.length(), nextIndex);

            firstIndiceOf = unspentUTXOstring.indexOf(search3, nextIndex);
            nextIndex = unspentUTXOstring.indexOf("}", firstIndiceOf + search3.length());
            unspentValue[i] = unspentUTXOstring.substring(firstIndiceOf + search3.length(), nextIndex);

            firstIndiceOf = unspentUTXOstring.indexOf(search1, nextIndex);
            i++;
        }

        return i;
    }
}
