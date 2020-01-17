import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;




public abstract class PersistentGlobalObjectManager<T extends Serializable> {

    public abstract String getSharedPreferencesFile();
    public abstract String getObjectName();

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    protected PersistentGlobalObjectManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(getSharedPreferencesFile(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private T object;

    public T get() {
        if (object == null) {
            String serializedObject = sharedPreferences.getString(getObjectName(), null);
            try {
                object = (T) deserialize(serializedObject);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public void set(T object) {
        this.object = object;
        try {
            String serializedObject = serialize(object);
            editor.putString(getObjectName(), serializedObject);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object deserialize(String s) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.decode(s.getBytes(), Base64.DEFAULT);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return objectInputStream.readObject();
    }

    private static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(o);
        objectOutputStream.close();
        return new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
    }
}
