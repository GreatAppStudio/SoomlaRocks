package greatappstudio.com.soomlarocks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,AdListener{

    private Button mShowBannerButton;
    private ArrayList<AdView> mAdViews = new ArrayList<>();
    private List<String> urls =
            Arrays.asList("https://masterz.app.link/soomla",
                    "https://www.google.com",
                    "https://www.soomla.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowBannerButton = findViewById(R.id.showBannerButton);
        mShowBannerButton.setOnClickListener(this);


        //this is somehow make things load fester...

        AdView adView = new AdView(this, "YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        adView.loadAd();
        mAdViews.add(adView);
        AdSettings.addTestDevice("72999f88-03dc-4a53-b76f-4aa82b4eb1d0");
    }

    @Override
    protected void onDestroy() {
        if (mAdViews.size() != 0) {
            for (AdView ad :mAdViews) {
                ad.destroy();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == mShowBannerButton) {
            showAdPressed();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        // Ad error callback
        Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdLoaded(Ad ad) {
        // Ad loaded callback
        WebView webView = findWebView(ad);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Random rand = new Random();
                int  n = rand.nextInt(urls.size());
                String randomUrl = urls.get(n);
                view.loadUrl(
                        "javascript:(function() { " +
                                "document.getElementsByClassName(\"icon\")[0].src = \"https://picsum.photos/417/?random\";"+
                                "document.getElementsByClassName(\"fbOffsiteAdLink fbAdLinkInline buttonClickableArea\")[0].href = \"soomla:"+randomUrl+"\";"+
                                "})()");

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("soomla:")) {
                    url = url.replace("soomla:","");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }


    private WebView findWebView(Object v) {
        if (v instanceof WebView) {
            return (WebView) v;
        }
        Class viewClass = v.getClass();

        Field[] aFields = viewClass.getDeclaredFields();
        for(Field f : aFields)
        {
            if (f.getType().isAssignableFrom(View.class)) {
                f.setAccessible(true);
                try {
                    return findWebView((View)f.get(v));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void showAdPressed() {

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.banner_item,null);

        LinearLayout itemsView = findViewById(R.id.items_view);

        itemsView.addView(view);

        AdView adView = new AdView(this, "YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

        LinearLayout adContainer = (LinearLayout) view;

        adView.setAdListener(this);

        adContainer.addView(adView);

        adView.loadAd();
    }

}
