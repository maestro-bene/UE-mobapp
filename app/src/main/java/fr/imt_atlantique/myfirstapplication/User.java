package fr.imt_atlantique.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private final String surname;
    private final String name;
    private final String birthDate;
    private final String city;
    private final String department;
    private final String[] phoneNumbers;

    // Constructor
    public User(String surname, String name, String birthDate, String city, String department, String[] phoneNumbers) {
        this.surname = surname;
        this.name = name;
        this.birthDate = birthDate;
        this.city = city;
        this.department = department;
        this.phoneNumbers = phoneNumbers;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        surname = in.readString();
        name = in.readString();
        birthDate = in.readString();
        city = in.readString();
        department = in.readString();
        phoneNumbers = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(surname);
        dest.writeString(name);
        dest.writeString(birthDate);
        dest.writeString(city);
        dest.writeString(department);
        dest.writeStringArray(phoneNumbers);
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
    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
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

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }
}
