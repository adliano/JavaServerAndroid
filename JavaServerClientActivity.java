package com.adliano.javaserverclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class JavaServerClientActivity extends Activity
{
    // replace the xxx's for your ip address or domain name
    private final static String HOST_NAME = "xxx.xxx.xxx.xxx";
    // place here the port you will use
    private final static int PORT_NUMBER = 54321;
    private static final boolean AUTO_FLUSH = true;
    TextView tv;
    EditText edInput;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_java_server_client_activity);

        tv = (TextView)findViewById(R.id.textView);
        edInput = (EditText)findViewById(R.id.editText);
    }
    /*** buttonSend ***/
    public void buttonSend(View view)
    {
        new GetServerResponse().execute(edInput.getText().toString());
    }

    /*** GetServerResponse ***/
    public class GetServerResponse extends AsyncTask<String, Void, String>
    {
        String strReturn;
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                // create a Socket
                Socket socket= new Socket(HOST_NAME,PORT_NUMBER);
                if(!socket.isConnected())
                {
                    Log.d("TAG_CONNECTION","NOT CONNECTED");
                }
                // get OutputStream
                OutputStream out = socket.getOutputStream();
                // get InputStream to receive data from server
                InputStream input = socket.getInputStream();
                // Scanner to read response from server
                Scanner scanner = new Scanner(input);
                // PrintWriter will send data to server using the OutputStream
                PrintWriter writer = new PrintWriter(out,AUTO_FLUSH);

                writer.println(params[0].trim());
                strReturn = scanner.nextLine();

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return strReturn;
        }

        @Override
        protected void onPostExecute(String s)
        {
            //if(!s.isEmpty())
                tv.setText(s);
        }
    }
}

