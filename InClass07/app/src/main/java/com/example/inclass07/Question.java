package com.example.inclass07;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

public class Question implements Parcelable {
    String text,imageURL;
    int answer;
    String choices[];

    protected Question(Parcel in) {
        text = in.readString();
        imageURL = in.readString();
        answer = in.readInt();
        choices = in.createStringArray();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public Question() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", answer=" + answer +
                ", choices=" + Arrays.toString(choices) +
                '}';
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(imageURL);
        dest.writeInt(answer);
        dest.writeStringArray(choices);

    }
}
