package ru.integrationmonitoring.monetasdkapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class SdkMainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_main);
    }

    @Override
    public void onClick(View view) {
        MonetaSdk monetasdk = new MonetaSdk();

        // payment form
        Double mntAmount = 12.00;
        String mntPaymentSystem = "plastic";
        String mntOrderId = monetasdk.getOrderId();
        String mntCurrency = "RUB";

        WebView myWebView = (WebView) findViewById(R.id.webView);
        monetasdk.showPaymentFrom(mntOrderId, mntAmount, mntCurrency, mntPaymentSystem, myWebView, this);

    }
}
