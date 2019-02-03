package amirz.dngprocessor.scheduler;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import amirz.dngprocessor.NotifHandler;
import amirz.dngprocessor.Path;
import amirz.dngprocessor.parser.DngParser;

import static amirz.dngprocessor.Utilities.ATLEAST_OREO;

public class DngParseService extends IntentService {
    private static final String TAG = "DngParseService";

    public static void runForUri(Context context, Uri uri) {
        context = context.getApplicationContext();

        Intent intent = new Intent(context, DngParseService.class);
        intent.setData(uri);

        if (ATLEAST_OREO) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public DngParseService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Uri uri = intent.getData();
        String file = Path.getFileFromUri(this, uri);
        Log.e(TAG, "onHandleIntent " + file);

        NotifHandler.create(this, file);
        try {
            new DngParser(this, uri).run();
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(getMainLooper()).post(() ->
                    Toast.makeText(this, "DNG Processor could not load " + file,
                            Toast.LENGTH_SHORT).show());
        }
        NotifHandler.done(this);
    }
}