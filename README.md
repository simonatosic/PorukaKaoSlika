# PorukaKaoSlika

PorukaKaoSlika _(Message in a Picture)_ is an Android app where you receive notifications containing images with inspirational quotes at any time within 24 hours you choose. Developed in Croatian language.
    
![home](https://user-images.githubusercontent.com/30208087/28428923-2d2675c4-6d7b-11e7-828a-80e277c1c664.jpg)

##### Moja poruka _(My Message)_
- write your own message
- choose time within 24 hours
- receive and open notification
- message is displayed as an image with your text on a background
- there are 30 backgrounds, one gets randomly selected 
- close by pressing close button or system back button
- gallery opens, where image gets saved

##### Poruka iznenađenja _(Surprise Message)_
- choose time within 24 hours
- receive and open notification
- image with inspirational quote is displayed
- there are 30 pre-designed images, one gets randomly selected 
- close by pressing close button or system back button
- gallery opens, where image gets saved

##### Primljene poruke _(Received Messages)_
- scrollable gallery of received images in a form of colorful patchwork
- click an image and swipe for a full screen display

## Built With
- [Firebase development platform for Android](https://firebase.google.com/)
- [Glide library for loading and caching images](https://github.com/bumptech/glide)

## Code Samples

Notification for _Moja poruka_, containing edit text input:
```java
public class AlarmReceiverMojaPoruka extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String editTextPoruka = intent.getStringExtra("editTextPoruka");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setSmallIcon(R.drawable.ic_notify_p)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle("Poruka kao slika")
                .setContentText("Stigla je tvoja poruka")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        Intent i = new Intent(context, NotificationFullscreenActivity.class);
        i.putExtra("mojaPoruka", editTextPoruka);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), i, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify(0, notification);

        TaskStackBuilder tsb = TaskStackBuilder.create(context);
        tsb.addParentStack(NotificationFullscreenActivity.class);
    }
}
```

Getting DataSnapshot from Firebase Database:
```java
if(getIntent().hasExtra("mojaPoruka")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("mojaporuka");
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            collectUrlsMojaPoruka((Map<String, Object>) dataSnapshot.getValue());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
}
```

Pulling urls from Firebase Database and displaying a randomly selected one with Glide:
```java
private void collectUrlsPorukaIznenadjenja (Map<String, Object> porukaIznenadjenja) {
        urls = new ArrayList<>();

        for (Map.Entry<String, Object> entry : porukaIznenadjenja.entrySet()) {
            Map singleData = (Map) entry.getValue();
            urls.add((String) singleData.get("url"));
        }

        Random r = new Random();
        id = r.nextInt(urls.size());
        url = urls.get(id);

        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(), R.string.nema_internet_veze, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        progressBar.setVisibility(View.GONE);
                        imagePreview.setImageBitmap(resource);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                urlDatabaseHelper.addUrl(url, sqLiteDatabase);
                                urlDatabaseHelper.close();
                                handler.sendEmptyMessage(1);
                            }
                        });
                        thread.start();
                        Toast.makeText(NotificationFullscreenActivity.this, "Slika se sprema u primljene poruke", Toast.LENGTH_LONG).show();
                    }
                });
        urls.clear();
        imagePreview.setImageResource(android.R.color.transparent);
}    
```
___
More pics at Wiki!
