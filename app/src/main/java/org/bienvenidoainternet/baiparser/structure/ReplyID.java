package org.bienvenidoainternet.baiparser.structure;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

/**
 * Created by Renard on 18-03-2016.
 */
public class ReplyID implements Parcelable{
    public String id;
    public int color;
    public ReplyID(String id){
        this.id = id;
        Random r = new Random();
        this.color = Color.rgb(r.nextInt(125) + 127, r.nextInt(127) + 127, r.nextInt(127) + 127);
    }

    protected ReplyID(Parcel in) {
        id = in.readString();
        color = in.readInt();
    }

    public static final Creator<ReplyID> CREATOR = new Creator<ReplyID>() {
        @Override
        public ReplyID createFromParcel(Parcel in) {
            return new ReplyID(in);
        }

        @Override
        public ReplyID[] newArray(int size) {
            return new ReplyID[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(color);
    }
}
