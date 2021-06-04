// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.rtstyk.linetextscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GestureDetectorCompat;
import androidx.exifinterface.media.ExifInterface;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.rtstyk.linetextscanner.GraphicOverlay.Graphic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


// icon <div>Icons made by <a href="https://www.flaticon.com/authors/pixel-perfect" title="Pixel perfect">Pixel perfect</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private Button mTextButton;
    private Button mFaceButton;
    private Bitmap mSelectedImage;
    private GraphicOverlay mGraphicOverlay;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    private static final int PICK_IMAGE = 100;
    //Uri imageUri;
    String currentPhotoPath;
    TextView textView;
    //List<Text.TextBlock> mCurrentSortedTextBlocks;
    List<Text.Element> sortedTextElements = new ArrayList<>();
    List<Text.Element> hiddenTextElements = new ArrayList<>();
    List<GraphicOverlay.Graphic> hiddenGraphics = new ArrayList<>();
    private GestureDetectorCompat mDetector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private TextElementComparator textElementComparator = new TextElementComparator();


    /**
     * Number of results to show in the UI.
     */
    private static final int RESULTS_TO_SHOW = 3;
    /**
     * Dimensions of inputs.
     */
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;

    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float>
                                o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);

        mTextButton = findViewById(R.id.button_text);
        mFaceButton = findViewById(R.id.button_face);

        mGraphicOverlay = findViewById(R.id.graphic_overlay);

        textView = findViewById(R.id.text);
        //textView.setText("Ready...");
        textView.setTextColor(Color.WHITE);
        textView.setVisibility(View.INVISIBLE);

        //setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(mImageView.getContext(), new MyGestureListener());

        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                //doLocalTest();

            }
        });
        mFaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openGallery();
                dispatchTakePictureIntent();

                //selectImage(getApplicationContext());

            }
        });

        mSelectedImage = getBitmapFromAsset(this, "receipt1.jpg");
        mImageView.setImageBitmap(mSelectedImage);



        TextView topline = findViewById(R.id.title);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.);

        //ActionBar actionBar = getSupportActionBar();
        //setSupportActionBar(actionBar);
        
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.fin
        actionBar.setDisplayShowHomeEnabled(true);*/
        //getActionBar().setd

        /*actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        CharSequence title1 = actionBar.getTitle();
        actionBar.setTitle("ett");
        TextView title = new TextView(this);
        title.setText(title1);



        //TextView topline = findViewById(R.id.text);
        title.setMovementMethod(LinkMovementMethod.getInstance());
        actionBar.setCustomView(title);*/


        /*Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Camera", "Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.google.com"));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void doLocalTest()
    {
        mSelectedImage = getBitmapFromAsset(this, "receipt4.jpg");
        int height = mImageView.getHeight();
        int width = mImageView.getWidth();



        Matrix matrix = new Matrix();
        //Bitmap mSelectedImage1 = Bitmap.createBitmap(mSelectedImage,0,0, width, height, matrix, true);
        //Bitmap mSelectedImage1 = Bitmap.createScaledBitmap(mSelectedImage, width, height, false);

        Glide.with(this)
                .asBitmap()
                .load(mSelectedImage)
                .into(new CustomTarget<Bitmap>(width,height) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImageView.setImageBitmap(resource);
                        mSelectedImage = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


        //Matrix matrix = new Matrix();
        //if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
        //rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        //mImageView.setImageBitmap(mSelectedImage1);
        if (mSelectedImage.getWidth()== mSelectedImage.getWidth())
        {
            Log.i("tag"," same width " +mSelectedImage.getWidth());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            //Log.d(DEBUG_TAG,"onDown: " + event.toString());

            return true;
        }

        @Override
        public void onLongPress(MotionEvent even){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", textView.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            //Log.i("longpress","long press");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());

            //Toast.makeText(getApplicationContext(), "jjj", Toast.LENGTH_SHORT).show();
            //Log.i("single tup","tap ");
            // check if tap is within a rectangle of recognized text
            //switchTextViews();
            //event.getRawX();



            if (mGraphicOverlay.getVisibility() != View.VISIBLE)
            {
                return false;
            }

            int[] viewCoords = new int[2];
            mImageView.getLocationOnScreen(viewCoords);
            int left = (int)event.getX()-viewCoords[0]-15;
            int top = (int)event.getY()-viewCoords[1]-15;
            int right = left+30;
            int bottom = top+30;

            int l1 = 72;
            int t1 = 573;
            int r1 = 471;
            int b1 = 624;
            //72, 573 - 471, 624
            Rect r = new Rect(l1,t1,r1,b1);
            boolean t = r.intersects(left,top,right,bottom);
            //Log.i("does",t+" "+left+" "+top+" "+right+" "+bottom);
            //Text.Element found  = null;

            /*for (Text.Element te : sortedTextElements)
            {
                if (te.getBoundingBox().contains((int)event.getX()-viewCoords[0],
                        (int)event.getY()-viewCoords[1])){
                    Log.i("clicked text element"," clicked on "+te.getText());
                    found=te;
                    break;
                }
            }*/



            //if (found == null)
              //  return false;

            TextGraphic found = null;

            for (GraphicOverlay.Graphic g : mGraphicOverlay.getGraphics())
            {
                TextGraphic tg = (TextGraphic)g;
                if (tg.getBoundingBox().intersects(left, top, right, bottom)){
                    //Log.i("clicked text element"," clicked on "+tg.getText()+" "+tg.getBoundingBox()+
                      //      " "+left+" "+top+" "+right+" "+bottom);
                    found=tg;
                }
            }
            if (found!=null)
            {
                mGraphicOverlay.remove(found);
                hiddenGraphics.add(found);
                redoText();
                return false;
            }

            for (GraphicOverlay.Graphic g : hiddenGraphics)
            {
                TextGraphic tg = (TextGraphic)g;

                if (tg.getBoundingBox().intersects(left, top, right, bottom)){
                    //Log.i("clicked text element"," clicked on "+tg.getText()+" "+tg.getBoundingBox()+
                      //      " "+left+" "+top+" "+right+" "+bottom);
                    found=tg;
                }
            }

            if (found!=null){
                //GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, found);
                mGraphicOverlay.add(found);
                //Log.i("found text element"," removing "+found.getText());
                redoText();
            }


                //mGraphicOverlay.


            //if (sortedTextElements.contains())



            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + e1.toString() + e2.toString());
            //mGraphicOverlay.clear();
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left
                //return false;
                //Log.i("swipe"," right swipe ");
                switchTextViews();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Left to right
                //Log.i("swipe"," left swipe ");
                switchTextViews();
                return false;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Bottom to top
                //Your code to flip the coin
                Log.println(Log.VERBOSE,"Coin", "Coin flipped");
                //runTextRecognition();
                return false;
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Top to bottom
                return false;
            }
            return false;
        }

    }

    private void switchTextViews()
    {
        if (mGraphicOverlay.getVisibility() == View.VISIBLE)
            mGraphicOverlay.setVisibility(View.INVISIBLE);
        else
            mGraphicOverlay.setVisibility(View.VISIBLE);

        if (textView.getVisibility() == View.VISIBLE)
            textView.setVisibility(View.INVISIBLE);
        else
            textView.setVisibility(View.VISIBLE);
    }

    private void redoText()
    {
        List<Text.Element> elements = new ArrayList<>();
        for (Graphic g : mGraphicOverlay.getGraphics())
        {
            elements.add(((TextGraphic)g).getTextElement());
        }
        regenerateText(elements, textView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int height = mImageView.getHeight();
        int width = mImageView.getWidth();

        int height1 = mImageView.getMeasuredHeight();
        int width1 = mImageView.getMeasuredWidth();

        int width2 = mGraphicOverlay.getWidth();
        int height2 = mGraphicOverlay.getHeight();
        //height=600;
        //width = 500;

        if (resultCode != RESULT_CANCELED) {
            mGraphicOverlay.clear();
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        //data.getExtras().
                        //Uri imgUri = data.getData();

                        mImageView.setImageBitmap(selectedImage);
                        mSelectedImage = selectedImage;

                        /*Glide.with(this)
                                .asBitmap()
                                .load(selectedImage)
                                .into(new CustomTarget<Bitmap>(width,height) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mImageView.setImageBitmap(resource);
                                        mSelectedImage = resource;
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });*/


                    }

                    break;
                case 111: // take a picture
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap selectedImageBitmap = BitmapFactory.decodeFile(picturePath);

                                cursor.close();
                                mImageView.setImageBitmap(selectedImageBitmap);
                                mSelectedImage = selectedImageBitmap;
                                runTextRecognition();
                            }
                        }

                    }
                    break;
                case PICK_IMAGE:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ExifInterface exif = new ExifInterface(imageStream);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);
                        //Matrix matrix = new Matrix();
                        //if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                        //rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


                        //mImageView.setImageBitmap(selectedImage);
                        //mSelectedImage = selectedImage;

                        //Glide.with(this).load(imageUri).into(mImageView);

                        Glide.with(this)
                                .asBitmap()
                                .load(imageUri)
                                .apply(new RequestOptions().override(width, height))
                                .into(new CustomTarget<Bitmap>(width, height) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mImageView.setImageBitmap(resource);
                                        Bitmap mSelectedImage1 = Bitmap.createScaledBitmap(resource,
                                                mImageView.getWidth(), mImageView.getHeight(), false);
                                        mSelectedImage = mSelectedImage1;
                                        runTextRecognition();
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 101:
                    //final Uri imageUri = data.getData();
                    //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    //Bundle extras = data.getExtras();

                    Bitmap temp = getBitmapFromAsset(this, currentPhotoPath);
                    //Bitmap temp2 = Bitmap.createScaledBitmap(temp, width, height, false);
                    //Bitmap temp2 = scaleDown(temp,)

                    Glide.with(this)
                                .asBitmap()
                                .load(currentPhotoPath)
                                .apply(new RequestOptions().override(width, height))
                                .into(new CustomTarget<Bitmap>(width, height) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mImageView.setImageBitmap(resource);
                                        mSelectedImage = resource;
                                        Bitmap mSelectedImage1 = Bitmap.createScaledBitmap(resource,
                                                mImageView.getWidth(), mImageView.getHeight(), false);
                                        mSelectedImage = mSelectedImage1;
                                        //setPic(resource);
                                        runTextRecognition();
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }

                                })
                                ;
                    break;
            }


        }
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

        //Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(takePicture, 0);
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        PackageManager pm = getPackageManager();

        ActivityInfo activityInfo = takePictureIntent.resolveActivityInfo(pm, takePictureIntent.getFlags());
        if (activityInfo.exported) {
            Log.i("intent", " no idea "+activityInfo.name);
        }

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.rtstyk.linetextscanner.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 101);
            }
        }
    }



    /**
     * Gets the Amount of Degress of rotation using the exif integer to determine how much
     * we should rotate the image.
     * @param exifOrientation - the Exif data for Image Orientation
     * @return - how much to rotate in degress
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    /*private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }*/

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mGraphicOverlay.clear();
        switch (position) {
            case 0:
                //mSelectedImage = getBitmapFromAsset(this, "receipt3.jpg");
                //openGallery();
                dispatchTakePictureIntent();
                //selectImage(getApplicationContext());
                break;
            case 1:
                // Whatever you want to happen when the thrid item gets selected
                //mSelectedImage = getBitmapFromAsset(this, "receipt4.jpg");
                //openGallery();
                doLocalTest();



                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            //Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            //int targetWidth = targetedSize.first;
            //int maxHeight = targetedSize.second;

            int targetWidth = 517;
            int maxHeight = 689;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void setPic(Bitmap bitmap) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //bmOptions.inJustDecodeBounds = true;

        //BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bitmap.getWidth();
        int photoH = bitmap.getHeight();

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        //bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        //Bitmap newbitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        //mSelectedImage = bitmap;
        //mImageView.setImageBitmap(bitmap);
    }


    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

///////////////////////////////////

    private void runTextRecognition() {
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        mTextButton.setEnabled(false);

        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                mTextButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mTextButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }


    private void processTextRecognitionResult(Text texts)
    {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();

        List<Text.Element> textElements = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            Rect rect = blocks.get(i).getBoundingBox();
            //Log.i("rect", rect.top + " " + rect.right + " " + rect.bottom);

            List<Text.Line> lines = blocks.get(i).getLines();
            //Log.i("rect", rect.centerX() + " " + rect.centerY() + " " + rect.toString());

            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Text.Element e = elements.get(k);
                    textElements.add(e);
                    //Log.i(i+"-",j+"-"+k+" "+elements.get(k).getText());
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, e);
                    mGraphicOverlay.add(textGraphic);
                }
            }
        }



        /*StringBuilder textBuilder = new StringBuilder();
        for (Text.Element text : textElements) {
            if (text != null && text.getText() != null) {
                textBuilder.append(text.getText() + "\n");
            }
        }
        Log.i("full text", textBuilder.toString());*/
        sortedTextElements = regenerateText(textElements, textView);
    }


    private List<Text.Element> regenerateText(List<Text.Element> textElements, TextView lTextView)
    {
        Log.i("sorting",textElements.size()+" ------------------------------ ");
        Collections.sort(textElements, textElementComparator);
        Log.i("processing",textElements.size()+" ------------------------------ ");

        List<List<Text.Element>> lines = new ArrayList<>();
        List<Text.Element> currentLine = new ArrayList<>();
        lines.add(currentLine);

        // process and save only lines with prices
        for (int i = 0; i < textElements.size(); i++) {

            Rect rect = textElements.get(i).getBoundingBox();
            Text.Element element = textElements.get(i);
            Log.i("rect" + i, rect.top + " " + rect.left + " " + rect.bottom + " c:x" +
                    rect.centerX() + " cy:" + rect.centerY() + " " + element.getText());

            if (currentLine.size() == 0)
                currentLine.add(element);
            else { // need to check if this next element is still part of the line
                Text.Element previousElement = currentLine.get(currentLine.size() - 1);
                if (isSameLine(previousElement, element)) {
                    currentLine.add(element);
                    //Log.i("same line", previousElement.getText() + " " + element.getText() + " ");
                } else { //evaluate if we should keep this line
                    String t = previousElement.getText();
                    //if (t.contains(",") && t.matches("^[0-9].*$"))
                    //if (t.indexOf(',') > 0 && t.matches("^[0-9].*$")) {
                        //keep this line and create new one

                        currentLine = new ArrayList<>();
                        currentLine.add(element);
                        lines.add(currentLine);


                    //Log.i("new line", element.getText() + " ");
                    //} else {
                    //    currentLine.clear();
                    //    currentLine.add(element);
                    //    Log.i("line clear", element.getText() + " ");
                    //}
                }
            }
        }

        String s="";
        for (int i = 0; i < lines.size(); i++) {
            List<Text.Element> line = lines.get(i);
            for (int j = 0; j < line.size(); j++) {
                Text.Element element = line.get(j);
                //Log.i("rect" + rect.top, rect.right + " " + rect.bottom + " c:x" +
                //      rect.centerX() + " cy:" + rect.centerY() + " " + element.getText());


                s+= element.getText()+" ";
                if (j+1 == line.size())
                {
                    s+= " \n";
                }
            }
        }

        lTextView.setText(s);

        /*for (int i = 0; i < textElements.size(); i++) {
            Rect rect = textElements.get(i).getBoundingBox();

            Text.Element element = textElements.get(i);
            Log.i("rect"+rect.top,rect.right+" "+rect.bottom+ " c:x"+
                    rect.centerX()+" cy:"+rect.centerY()+" "+element.getText());

            Graphic textGraphic = new TextGraphic(mGraphicOverlay, element);
            mGraphicOverlay.add(textGraphic);

        }*/

        /*for (int i = 0; i < blocks.size(); i++) {
            Rect rect = blocks.get(i).getBoundingBox();
            Log.i("rect",rect.top+" "+rect.right+" "+rect.bottom);

            List<Text.Line> lines = blocks.get(i).getLines();
            Log.i("rect",rect.centerX()+" "+rect.centerY()+" "+rect.toString());

            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);
                    //Log.i(i+"-",j+"-"+k+" "+elements.get(k).getText());
                }
            }
        }*/

        return textElements;
    }

    private boolean isSameLine(Text.Element t1, Text.Element t2) {
        int diffOfTops = t1.getBoundingBox().top - t2.getBoundingBox().top;
        //int diffOfLefts = t1.getBoundingBox().left - t2.getBoundingBox().left;

        int height = (t1.getBoundingBox().height() + t2.getBoundingBox().height()) / 2;

        //Log.i("compare", t1.getText()+"-"+t2.getText()+"  "+diffOfTops +" "+diffOfLefts+ " height:"+height);

        if (Math.abs(diffOfTops) > height ) {
            return false;
        }
        return true;
    }

    class TextElementComparator implements Comparator<Text.Element> {
        @Override
        public int compare(Text.Element t1, Text.Element t2) {
            int diffOfTops = t1.getBoundingBox().top - t2.getBoundingBox().top;
            int diffOfBottoms = t1.getBoundingBox().bottom - t2.getBoundingBox().bottom;
            int diffOfLefts = t1.getBoundingBox().left - t2.getBoundingBox().left;

            int height = (t1.getBoundingBox().height() + t2.getBoundingBox().height()) / 2;

            int result;
            if (Math.abs(diffOfTops) > (height + height * 0.25)) {
                result =  diffOfTops;
            }else {
                result = diffOfLefts;
            }
            Log.i("compare", t1.getBoundingBox().top+ " "+ t2.getBoundingBox().top+
                    " " + t1.getBoundingBox().left+ " " + t2.getBoundingBox().left+ " "+
                    diffOfTops + " " + diffOfLefts + " height:" + height+"  "+result +"  "+
                    t1.getText() + "-" + t2.getText() + "  " );
            return result;
        }
    }

    class TextElementComparator2 implements Comparator<Text.Element> {
        @Override
        public int compare(Text.Element t1, Text.Element t2) {
            int diffOfTops = t1.getBoundingBox().top - t2.getBoundingBox().top;
            int diffOfLefts = t1.getBoundingBox().left - t2.getBoundingBox().left;

            int height = (t1.getBoundingBox().height() + t2.getBoundingBox().height()) / 2;

            //Log.i("compare", t1.getText() + "-" + t2.getText() + "  " + diffOfTops + " " + diffOfLefts + " height:" + height);

            if (Math.abs(diffOfTops) > height / 2) {
                return diffOfTops;
            }
            return diffOfLefts;
        }
    }



}
