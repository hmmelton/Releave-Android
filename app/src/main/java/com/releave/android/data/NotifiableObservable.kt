package com.releave.android.data.models

import androidx.databinding.Observable

interface NotifiableObservable : Observable {

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange()

    /**
     * Notifies listeners that a specific property has changed. The getter for the property that
     * changes should be marked with {@link Bindable} to generate a field in <code>BR</code> to be
     * used as <code>fieldId</code>.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int)
}