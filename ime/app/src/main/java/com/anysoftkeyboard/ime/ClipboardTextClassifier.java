package com.anysoftkeyboard.ime;



import android.content.Context;

import android.os.Handler;

import android.os.Looper;

import android.util.Log;

import android.util.Pair;





import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier;

import org.tensorflow.lite.support.label.Category;



import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;



public class ClipboardTextClassifier {

    private static final String TAG = "ASK_TextClassifier_TFLite";

    private final String modelName;

    private final Context context;

    private final ClassificationListener listener;





    private BertNLClassifier bertClassifier;

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();





    public interface ClassificationListener {

        void onClassificationResult(List<Pair<String, Float>> results);

    }



    public ClipboardTextClassifier(Context context, String modelName, ClassificationListener listener) {

        this.context = context;

        this.modelName = modelName;

        this.listener = listener;

        setupClassifier();

    }



    private void setupClassifier() {

        try {



            if (bertClassifier != null) {

                bertClassifier.close();

            }



            bertClassifier = BertNLClassifier.createFromFile(context, modelName);

            Log.i(TAG, "BertNLClassifier setup successfully.");

        } catch (IOException e) {

            Log.e(TAG, "Error setting up BertNLClassifier: " + e.getMessage(), e);

            bertClassifier = null;

        } catch (Exception e) {

            Log.e(TAG, "Unexpected error setting up BertNLClassifier: " + e.getMessage(), e);

            bertClassifier = null;

        }

    }



    public void classify(String text) {
        if (backgroundExecutor == null || backgroundExecutor.isShutdown()) {
            Log.w(TAG, "Background executor is not running. Cannot classify.");
            postEmptyResults();
            return;
        }

        backgroundExecutor.execute(() -> {

            Log.d(TAG, "Starting classification in background for text: '" + text + "'");

            if (bertClassifier == null) {
                Log.e(TAG, "BertNLClassifier is null. Cannot classify.");
                postEmptyResults();
                return;
            }

            List<Pair<String, Float>> classificationResultsList = new ArrayList<>();
            try {

                List<Category> tfliteResults = bertClassifier.classify(text);


                if (tfliteResults != null && !tfliteResults.isEmpty()) {
                    Log.i(TAG, "Classification successful! Got " + tfliteResults.size() + " results.");
                    StringBuilder resultsLog = new StringBuilder("Model results:\n");

                    for (Category category : tfliteResults) {
                        String name = category.getLabel();
                        float scr = category.getScore();

                        resultsLog.append(String.format("  - Label: %s, Score: %.4f\n", name, scr));
                        classificationResultsList.add(new Pair<>(name, scr));
                    }
                    Log.d(TAG, resultsLog.toString());
                } else {

                    Log.w(TAG, "Classification returned null or empty results from the model.");
                }


            } catch (Exception e) {
                Log.e(TAG, "Error during text classification: " + e.getMessage(), e);

            }


            new Handler(Looper.getMainLooper()).post(() -> {
                Log.d(TAG, "Posting " + classificationResultsList.size() + " results back to the listener.");
                if (listener != null) {
                    listener.onClassificationResult(classificationResultsList);
                }
            });
        });
    }




    private void postEmptyResults() {

        new Handler(Looper.getMainLooper()).post(() -> {

            if (listener != null) {

                listener.onClassificationResult(new ArrayList<>());

            }

        });

    }



    public void close() {

        Log.d(TAG, "Closing ClipboardTextClassifier.");

        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {

            backgroundExecutor.shutdown();



        }

        if (bertClassifier != null) {

            try {

                bertClassifier.close();

                Log.i(TAG, "BertNLClassifier closed successfully.");

            } catch (Exception e) {
                Log.e(TAG, "Error closing BertNLClassifier: " + e.getMessage(), e);

            }

            bertClassifier = null;

        }

    }

}