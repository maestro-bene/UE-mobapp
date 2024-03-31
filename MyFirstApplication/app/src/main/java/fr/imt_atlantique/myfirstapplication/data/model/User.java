package fr.imt_atlantique.myfirstapplication.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User implements Parcelable {
    private final String lastName;
    private final String firstName;
    private final String birthDate;
    private final String city;
    private final String department;
    private final ArrayList<String> phoneNumbers;

    private String photoURI;

    // Constructor
    public User(String lastName, String firstName, String birthDate, String city, String department, ArrayList<String> phoneNumbers, String photoURI) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.city = city;
        this.department = department;
        this.phoneNumbers = phoneNumbers;
        this.photoURI = photoURI;
    }

    // Overloaded constructor without photoURI
    public User(String lastName, String firstName, String birthDate, String city, String department, ArrayList<String> phoneNumbers) {
        this(lastName, firstName, birthDate, city, department, phoneNumbers, null);
    }

    // Parcelable implementation
    protected User(Parcel in) {
        lastName = in.readString();
        firstName = in.readString();
        birthDate = in.readString();
        city = in.readString();
        department = in.readString();
        phoneNumbers = in.createStringArrayList();
        photoURI = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeString(birthDate);
        dest.writeString(city);
        dest.writeString(department);
        dest.writeStringList(phoneNumbers);
        dest.writeString(photoURI);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters
    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getCity() {
        return city;
    }

    public String getDepartment() {
        return department;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    @NonNull
    public String toString() {
        return ("Full name: " + this.firstName + " " + this.lastName + "\nLiving in: " + this.department + "\nBorn on: " + this.birthDate + "\nPhone numbers: " + this.phoneNumbers + "\nAssociated with photo: " + (this.photoURI != null ? this.photoURI : "No Photo") + '\'');
    }
}
