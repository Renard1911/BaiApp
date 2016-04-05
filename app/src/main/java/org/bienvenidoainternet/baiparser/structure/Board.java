package org.bienvenidoainternet.baiparser.structure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renard on 17-03-2016.
 */

public class Board implements Parcelable{
    private String boardName, boardDir;
    private int boardType;
    public Board(String boardName,String boardDir,int boardType){
        this.boardName = boardName;
        this.boardDir = boardDir;
        this.boardType = boardType;
    }

    public Board(Parcel in){
        this.boardName = in.readString();
        this.boardDir = in.readString();
        this.boardType = in.readInt();
    }

    public String getBoardDir() {
        return boardDir;
    }

    public String getBoardName() {
        return boardName;
    }

    public int getBoardType() {
        return boardType;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(boardName);
        dest.writeString(boardDir);
        dest.writeInt(boardType);
    }
}
