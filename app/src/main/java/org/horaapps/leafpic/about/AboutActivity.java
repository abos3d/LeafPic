package org.horaapps.leafpic.about;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.horaapps.leafpic.BuildConfig;
import org.horaapps.leafpic.R;
import org.horaapps.leafpic.util.ApplicationUtils;
import org.horaapps.leafpic.util.ChromeCustomTabs;
import org.horaapps.leafpic.util.preferences.Prefs;
import org.horaapps.liz.ThemedActivity;
import org.horaapps.liz.ui.ThemedTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The Activity to show About application
 * <p>
 * Includes the following data:
 * - Developers
 * - Translators
 * - Relevant app links
 */
public class AboutActivity extends ThemedActivity implements ContactListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.about_version_item_sub) ThemedTextView appVersion;
    @BindView(R.id.aboutAct_scrollView) ScrollView aboutScrollView;

    private ChromeCustomTabs chromeTabs;
    private int emojiEasterEggCount = 0;

    public static void startActivity(@NonNull Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        chromeTabs = new ChromeCustomTabs(AboutActivity.this);

        initUi();
    }

    @Override
    public void onDestroy() {
        chromeTabs.destroy();
        super.onDestroy();
    }

    @OnClick(R.id.about_link_rate)
    public void onRate() {
        Uri uri = Uri.parse(String.format("market://details?id=%s", BuildConfig.APPLICATION_ID));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        /** To count with Play market backstack, After pressing back button,
         * to taken back to our application, we need to add following flags to intent. */

        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;

        goToMarket.addFlags(flags);

        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format("http://play.google.com/store/apps/details?id=%s", BuildConfig.APPLICATION_ID))));
        }
    }

    private void emojiEasterEgg() {
        emojiEasterEggCount++;
        if (emojiEasterEggCount > 3) {
            boolean showEasterEgg = Prefs.showEasterEgg();
            Toast.makeText(this,
                    (!showEasterEgg ? this.getString(R.string.easter_egg_enable) : this.getString(R.string.easter_egg_disable))
                            + " " + this.getString(R.string.emoji_easter_egg), Toast.LENGTH_SHORT).show();
            Prefs.setShowEasterEgg(!showEasterEgg);
            emojiEasterEggCount = 0;
        }
    }

    private void mail(String mail) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + mail));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.send_mail_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void initUi() {
        setSupportActionBar(toolbar);
        appVersion.setText(ApplicationUtils.getAppVersion());
    }

    @CallSuper
    @Override
    public void updateUiElements() {
        super.updateUiElements();
        toolbar.setBackgroundColor(getPrimaryColor());
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setScrollViewColor(aboutScrollView);
        setStatusBarColor();
        setNavBarColor();

    }

    @Override
    public void onContactClicked(Contact contact) {
        chromeTabs.launchUrl(contact.getValue());
    }

    @Override
    public void onMailClicked(String mail) {
        mail(mail);
    }
}
