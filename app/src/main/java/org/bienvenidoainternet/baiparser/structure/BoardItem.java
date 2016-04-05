package org.bienvenidoainternet.baiparser.structure;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renard on 17-03-2016.
 */
public class BoardItem implements Parcelable {
    private String name = "";
    private String timestamp_formatted = "";
    private String thumb = "";
    private String tripcode = "";
    private String email = "";
    private String file = "";
    private String message = "";
    private String subject = "";
    private String posterId = "";
    private int parentid, id, idcolor, totalreplies = 0, totalfiles, thumb_height, thumb_weight, filesize, deleted_code, bbs_id = 1, parentPostCount;
    private long timestamp = 0;

    private Bitmap thumbBitmap = null;
    public boolean downloadingThumb = false;
    public boolean isReply = false;
    private Board parentBoard = null;

    protected BoardItem(Parcel in) {
        name = in.readString();
        timestamp_formatted = in.readString();
        thumb = in.readString();
        tripcode = in.readString();
        email = in.readString();
        file = in.readString();
        message = in.readString();
        subject = in.readString();
        posterId = in.readString();
        parentid = in.readInt();
        id = in.readInt();
        idcolor = in.readInt();
        totalreplies = in.readInt();
        totalfiles = in.readInt();
        thumb_height = in.readInt();
        thumb_weight = in.readInt();
        filesize = in.readInt();
        deleted_code = in.readInt();
        bbs_id = in.readInt();
        parentPostCount = in.readInt();
        timestamp = in.readLong();
        thumbBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        downloadingThumb = in.readByte() != 0;
        isReply = in.readByte() != 0;
        parentBoard = in.readParcelable(Board.class.getClassLoader());
    }

    public static final Creator<BoardItem> CREATOR = new Creator<BoardItem>() {
        @Override
        public BoardItem createFromParcel(Parcel in) {
            return new BoardItem(in);
        }

        @Override
        public BoardItem[] newArray(int size) {
            return new BoardItem[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getFileSize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public int getIdColor() {
        return idcolor;
    }

    public void setIdColor(int idcolor) {
        this.idcolor = idcolor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        // TODO: Dar formato a los mensajes de otra forma
        this.message = message.replace("<span class=\"unkfunc\">", "<font color=#85c749><i>");
        this.message = this.message.replace("</span>", "</i></font>");
        if (this.message.contains("<hr")){
            this.message = this.message.replace("<hr />", "<br><br><small><font color=#FF8800><i>") + "</i></small></font>";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Board getParentBoard() {
        return parentBoard;
    }

    public void setParentBoard(Board parentBoard) {
        this.parentBoard = parentBoard;
    }

    public int getParentId() {
        return parentid;
    }

    public void setParentId(int parentid) {
        this.parentid = parentid;
    }

    public int getParentPostCount() {
        return parentPostCount;
    }

    public void setParentPostCount(int parentPostCount) {
        this.parentPostCount = parentPostCount;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getThumbHeight() {
        return thumb_height;
    }

    public void setThumbHeight(int thumb_height) {
        this.thumb_height = thumb_height;
    }

    public int getThumbWidth() {
        return thumb_weight;
    }

    public void setThumbWidth(int thumb_weight) {
        this.thumb_weight = thumb_weight;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeStampFormatted() {
        return timestamp_formatted;
    }

    public void setTimeStampFormatted(String timestamp_formatted) {
        this.timestamp_formatted = timestamp_formatted;
    }

    public int getTotalFiles() {
        return totalfiles;
    }

    public void setTotalFiles(int totalfiles) {
        this.totalfiles = totalfiles;
    }

    public int getTotalReplies() {
        return totalreplies;
    }

    public void setTotalReplies(int totalreplies) {
        this.totalreplies = totalreplies;
    }

    public String getTripcode() {
        return tripcode;
    }

    public void setTripcode(String tripcode) {
        this.tripcode = tripcode;
    }

    public int getDeletedCode() {
        return deleted_code;
    }

    public void setDeletedCode(int deleted_code) {
        this.deleted_code = deleted_code;
        if (deleted_code == 1){
            this.message = "Eliminado por el usuario.";
        }else if (deleted_code == 2){
            this.message = "Eliminado por el Staff.";
        }
    }

    public int getBbsId() {
        return bbs_id;
    }

    public void setBbsId(int bbs_id) {
        this.bbs_id = bbs_id;
    }

    public int realParentId(){
        if (parentid == 0){
            return id;
        }
        return parentid;
    }

    public BoardItem() {

    }

    public boolean isSage(){
        return this.email.equals("sage");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(timestamp_formatted);
        dest.writeString(thumb);
        dest.writeString(tripcode);
        dest.writeString(email);
        dest.writeString(file);
        dest.writeString(message);
        dest.writeString(subject);
        dest.writeString(posterId);
        dest.writeInt(parentid);
        dest.writeInt(id);
        dest.writeInt(idcolor);
        dest.writeInt(totalreplies);
        dest.writeInt(totalfiles);
        dest.writeInt(thumb_height);
        dest.writeInt(thumb_weight);
        dest.writeInt(filesize);
        dest.writeInt(deleted_code);
        dest.writeInt(bbs_id);
        dest.writeInt(parentPostCount);
        dest.writeLong(timestamp);
        dest.writeParcelable(thumbBitmap, flags);
        dest.writeByte((byte) (downloadingThumb ? 1 : 0));
        dest.writeByte((byte) (isReply ? 1 : 0));
        dest.writeParcelable(parentBoard, flags);
    }
}
