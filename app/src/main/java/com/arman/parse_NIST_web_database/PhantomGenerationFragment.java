package com.arman.parse_NIST_web_database;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhantomGenerationFragment extends Fragment {

    //private PhantomGenerationTask PGTask;
    private static final String TAG = "CCCP";
    private PhanGenCallbacks PGCallbacks;
    private Runnable PhantomGenerationTask = new Runnable() {

        @Override
        public void run() {

            StringBuilder text = new StringBuilder();
            Matcher DoubleFound;
            MainActivity MainActivity_access=(MainActivity)getActivity();

            URL my_page = null;

            try {
                my_page = new URL(MainActivity_access.url_to_parse);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            InputStream s_tream;
            try {
                s_tream=my_page.openStream();
                in = new BufferedReader(new InputStreamReader(s_tream));

            } catch (IOException e) {
                e.printStackTrace();
            }

            String inputLine = null;
            int element_counter=0;
            while (true) {
                try {
                    if ((inputLine = in.readLine()) == null) break;
                    //Log.v(TAG,inputLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // find first data with E+, E-, <TD> for html code,
                int firstEPlus = inputLine.indexOf("E+");
                int firstEMinus = inputLine.indexOf("E-");
                int start_of_the_next_table = inputLine.indexOf("ASCII format");
                if (start_of_the_next_table>=0)
                        break;
                //Log.v(TAG, String.valueOf(start_of_the_next_table));
                // read first 47 by 3 table from webpage
                if(!(firstEPlus<0)|!(firstEMinus<0)){
                    element_counter++;
                    DoubleFound = Pattern.compile( "[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?" ).matcher( inputLine );
                    while ( DoubleFound.find() )
                    {
                        double element = Double.parseDouble( DoubleFound.group() );
                        text.append(element);
                        text.append('\n');
                        //Log.v(TAG, String.valueOf(element));

                    }
                }

            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



//            File file = new File("/sdcard/Download/Attenuation coefficients/NIST elements/Aluminum.txt");
//            //Read text from file
//            BufferedReader br;
//            int firstPlus;
//            // 08.11.2021 Need some safety for possibly wrong table data
//            try {
//                br = new BufferedReader(new FileReader(file));
//                String line = null;
//                while (true) {
//                    try {
//                        if (!((line = br.readLine()) != null)) break;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    // find first data with +e
//                    firstPlus = line.indexOf('+');
//                    if(!(firstPlus<0)){
//                        DoubleFound = Pattern.compile( "[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?" ).matcher( line );
//                        while ( DoubleFound.find() )
//                        {
//                            double element = Double.parseDouble( DoubleFound.group() );
//                            text.append(element);
//                            text.append('\n');
//                            //PGCallbacks.onProgressUpdate(String.valueOf(element));
//
//                        }
//                    }
//                }
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

            Log.v(TAG, String.valueOf(text));
            PGCallbacks.onProgressUpdate(text.toString());
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PGCallbacks = (PhanGenCallbacks) activity;
        Log.v(TAG, "I'm in onAttach!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OpenCVLoader.initDebug();
        // Retain this fragment across configuration changes.
        setRetainInstance(true); // does not really make a difference if it is even false

        Executor mSingleThreadExecutor = Executors.newSingleThreadExecutor();
        mSingleThreadExecutor.execute(PhantomGenerationTask);

        //PGTask = new PhantomGenerationTask();
        //PGTask.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        PGCallbacks = null;
        Log.v(TAG, "I'm on onDetach");
    }

    interface PhanGenCallbacks {
        void onProgressUpdate(String text);
        void onCancelled();
        void onPostExecute();
    }

}