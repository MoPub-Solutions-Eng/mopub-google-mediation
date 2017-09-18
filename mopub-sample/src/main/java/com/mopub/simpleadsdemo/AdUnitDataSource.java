package com.mopub.simpleadsdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mopub.common.logging.MoPubLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.mopub.simpleadsdemo.MoPubSQLiteHelper.COLUMN_AD_TYPE;
import static com.mopub.simpleadsdemo.MoPubSQLiteHelper.COLUMN_AD_UNIT_ID;
import static com.mopub.simpleadsdemo.MoPubSQLiteHelper.COLUMN_DESCRIPTION;
import static com.mopub.simpleadsdemo.MoPubSQLiteHelper.COLUMN_ID;
import static com.mopub.simpleadsdemo.MoPubSQLiteHelper.COLUMN_USER_GENERATED;
import static com.mopub.simpleadsdemo.MoPubSQLiteHelper.TABLE_AD_CONFIGURATIONS;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.BANNER;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.CUSTOM_NATIVE;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.INTERSTITIAL;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.LEADERBOARD;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.LIST_VIEW;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.MRECT;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.RECYCLER_VIEW;
import static com.mopub.simpleadsdemo.MoPubSampleAdUnit.AdType.REWARDED_VIDEO;

class AdUnitDataSource {
    private Context mContext;
    private MoPubSQLiteHelper mDatabaseHelper;
    private String[] mAllColumns = {
            COLUMN_ID,
            COLUMN_AD_UNIT_ID,
            COLUMN_DESCRIPTION,
            COLUMN_USER_GENERATED,
            COLUMN_AD_TYPE
    };

    AdUnitDataSource(final Context context) {
        mContext = context.getApplicationContext();
        mDatabaseHelper = new MoPubSQLiteHelper(context);
        populateDefaultSampleAdUnits();
    }

    MoPubSampleAdUnit createDefaultSampleAdUnit(final MoPubSampleAdUnit sampleAdUnit) {
        return createSampleAdUnit(sampleAdUnit, false);
    }

    MoPubSampleAdUnit createSampleAdUnit(final MoPubSampleAdUnit sampleAdUnit) {
        return createSampleAdUnit(sampleAdUnit, true);
    }

    private MoPubSampleAdUnit createSampleAdUnit(final MoPubSampleAdUnit sampleAdUnit,
                                                 final boolean isUserGenerated) {
        final ContentValues values = new ContentValues();
        final int userGenerated = isUserGenerated ? 1 : 0;
        values.put(COLUMN_AD_UNIT_ID, sampleAdUnit.getAdUnitId());
        values.put(COLUMN_DESCRIPTION, sampleAdUnit.getDescription());
        values.put(COLUMN_USER_GENERATED, userGenerated);
        values.put(COLUMN_AD_TYPE, sampleAdUnit.getFragmentClassName());

        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        final long insertId = database.insert(TABLE_AD_CONFIGURATIONS, null, values);
        final Cursor cursor = database.query(TABLE_AD_CONFIGURATIONS, mAllColumns,
                COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        final MoPubSampleAdUnit newAdConfiguration = cursorToAdConfiguration(cursor);
        cursor.close();
        database.close();

        if (newAdConfiguration != null) {
            MoPubLog.d("Ad configuration added with id: " + newAdConfiguration.getId());
        }
        return newAdConfiguration;
    }

    void deleteSampleAdUnit(final MoPubSampleAdUnit adConfiguration) {
        final long id = adConfiguration.getId();
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        database.delete(TABLE_AD_CONFIGURATIONS, COLUMN_ID + " = " + id, null);
        MoPubLog.d("Ad Configuration deleted with id: " + id);
        database.close();
    }

    List<MoPubSampleAdUnit> getAllAdUnits() {
        final List<MoPubSampleAdUnit> adConfigurations = new ArrayList<>();
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = database.query(TABLE_AD_CONFIGURATIONS,
                mAllColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            final MoPubSampleAdUnit adConfiguration = cursorToAdConfiguration(cursor);
            adConfigurations.add(adConfiguration);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        return adConfigurations;
    }

    List<MoPubSampleAdUnit> getDefaultAdUnits() {
        final List<MoPubSampleAdUnit> adUnitList = new ArrayList<>();
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder("faa64d034b684268926e07fc04d8fdb3", BANNER)
                        .description("MoPub > AdMob Banner Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder(mContext.getString(R.string.ad_unit_id_mrect), MRECT)
                        .description("MoPub Mrect Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder(mContext.getString(R.string.ad_unit_id_leaderboard), LEADERBOARD)
                        .description("MoPub Leaderboard Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder("4a488029dafb423790423370dc5835c0", INTERSTITIAL)
                        .description("MoPub > AdMob Interstitial Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder("49a4a502ed2945fd92bc5798c9421a57",
                        REWARDED_VIDEO)
                        .description("MoPub > AdMob Rewarded Video Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder(mContext.getString(R.string.ad_unit_id_rewarded_rich_media),
                        REWARDED_VIDEO)
                        .description("MoPub Rewarded Rich Media Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder("57fd53ccacbf4e49a91b4f2cde681923", LIST_VIEW)
                        .description("MoPub > AdMob Ad Placer Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder("57fd53ccacbf4e49a91b4f2cde681923", RECYCLER_VIEW)
                        .description("MoPub > AdMob Recycler View Sample")
                        .build());
        adUnitList.add(
                new MoPubSampleAdUnit
                        .Builder("57fd53ccacbf4e49a91b4f2cde681923", CUSTOM_NATIVE)
                        .description("MoPub > AdMob View Pager Sample")
                        .build());
        return adUnitList;
    }

    private void populateDefaultSampleAdUnits() {
        final HashSet<MoPubSampleAdUnit> allAdUnits = new HashSet<>();
        for (final MoPubSampleAdUnit adUnit : getAllAdUnits()) {
            allAdUnits.add(adUnit);
        }

        for (final MoPubSampleAdUnit defaultAdUnit : getDefaultAdUnits()) {
            if (!allAdUnits.contains(defaultAdUnit)) {
                createDefaultSampleAdUnit(defaultAdUnit);
            }
        }
    }

    private MoPubSampleAdUnit cursorToAdConfiguration(final Cursor cursor) {
        final long id = cursor.getLong(0);
        final String adUnitId = cursor.getString(1);
        final String description = cursor.getString(2);
        final int userGenerated = cursor.getInt(3);
        final AdType adType = AdType.fromFragmentClassName(cursor.getString(4));

        if (adType == null) {
            return null;
        }

        return new MoPubSampleAdUnit.Builder(adUnitId, adType)
                .description(description)
                .isUserDefined(userGenerated == 1)
                .id(id)
                .build();
    }
}
