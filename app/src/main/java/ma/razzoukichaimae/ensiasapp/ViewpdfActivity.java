package ma.razzoukichaimae.ensiasapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ViewpdfActivity extends AppCompatActivity {

    String urls;
    WebView pdfWebView;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        getSupportActionBar().hide();

        pdfWebView = findViewById(R.id.pdfWebView);
        pdfWebView.setWebViewClient(new WebViewClient());
        pdfWebView.getSettings().setSupportZoom(true);
        pdfWebView.getSettings().setJavaScriptEnabled(true);

        // Firstly we are showing the progress
        // dialog when we are loading the pdf
        //dialog = new ProgressDialog(this);
        //dialog.setMessage("Loading..");
        //dialog.show();

        // getting url of pdf using getItentExtra
        //urls = getIntent().getStringExtra("url");
        //new RetrivePdfStream().execute(urls);
        pdfWebView.loadUrl("https://firebasestorage.googleapis.com/v0/b/miola-application.appspot.com/o/schedules%2F1652559757206.pdf");
        //dialog.dismiss();
    }


/*    private class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {

                // adding url
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // if url connection response code is 200 means ok the execute
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            // if error return null
            catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        // Here load the pdf and dismiss the dialog box
        protected void onPostExecute(InputStream inputStream) {
            pdfWebView.fromStream(inputStream).load();
            dialog.dismiss();
        }
    }*/
}