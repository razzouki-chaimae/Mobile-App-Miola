package ma.razzoukichaimae.ensiasapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;

public class ParcelableLinkedList<E extends Parcelable> implements Parcelable {

    private final LinkedList<E> linkedList;

    public final Creator<ParcelableLinkedList> CREATOR = new Creator<ParcelableLinkedList>() {
        @Override
        public ParcelableLinkedList createFromParcel(Parcel in) {
            return new ParcelableLinkedList(in);
        }

        @Override
        public ParcelableLinkedList[] newArray(int size) {
            return new ParcelableLinkedList[size];
        }
    };

    public ParcelableLinkedList(Parcel in) {
        // Read size of list
        int size = in.readInt();
        // Read the list
        linkedList = new LinkedList<E>();
        for (int i = 0; i < size; i++) {
            linkedList.add((E)in.readParcelable(ParcelableLinkedList.class.getClassLoader()));
        }

    }

    public ParcelableLinkedList(LinkedList<E> linkedList) {
        this.linkedList = linkedList;
    }

    LinkedList<E> getLinkedList() {
        return linkedList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // Write size of the list
        parcel.writeInt(linkedList.size());
        // Write the list
        for (E entry : linkedList) {
            parcel.writeParcelable(entry, i);
        }
    }

}