package custom.picker;

import android.os.Parcel;

import com.marvinlabs.widget.floatinglabel.instantpicker.Instant;

import java.io.Serializable;

/**
 * Created by Daniel on 05.01.2015.
 */
public class GenericInstant<Value extends Serializable> implements Instant {

    /** The Constant CREATOR. */
    public static final Creator<GenericInstant> CREATOR = new Creator<GenericInstant>() {
        /**
         * Generic Instant Parcel to simplify the whole parceling process
         * @param source
         * @return
         */
        public GenericInstant createFromParcel(Parcel source) {
            return new GenericInstant(source);
        }

        /**
         * Creates a new array of given size
         * @param size
         * @return
         */
        public GenericInstant[] newArray(int size) {
            return new GenericInstant[size];
        }
    };

    /**
     *  the amount
     */
    private Value mAmount;

    /**
     * Instantiates a new generic instant.
     *
     * @param amount the amount
     */
    public GenericInstant(Value amount) {
        mAmount = amount;
    }

    /**
     * Instantiates a new generic instant.
     *
     * @param in the in
     */
    private GenericInstant(Parcel in) {
        mAmount = (Value) in.readSerializable();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mAmount);
    }
    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public Value getAmount(){
        return mAmount;
    }
    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(Value amount){
        mAmount = amount;
    }
}
