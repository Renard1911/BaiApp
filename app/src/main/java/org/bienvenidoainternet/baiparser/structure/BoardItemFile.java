package org.bienvenidoainternet.baiparser.structure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renard on 08-04-2016.
 */
public class BoardItemFile implements Parcelable{
    public String file;
    public String fileURL;
    public String boardDir;

    public BoardItemFile(String fileURL, String file, String boardDir){
        this.fileURL = fileURL;
        this.file = file;
        this.boardDir = boardDir;
    }

    protected BoardItemFile(Parcel in) {
        file = in.readString();
        fileURL = in.readString();
        boardDir = in.readString();
    }

    public static final Creator<BoardItemFile> CREATOR = new Creator<BoardItemFile>() {
        @Override
        public BoardItemFile createFromParcel(Parcel in) {
            return new BoardItemFile(in);
        }

        @Override
        public BoardItemFile[] newArray(int size) {
            return new BoardItemFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(file);
        dest.writeString(fileURL);
        dest.writeString(boardDir);
    }
}
