package com.dmsoft.firefly.sdk.dataframe;

/**
 * enum class for pass policy
 * VARIABLE for only check pass in variable test item
 * ATTRIBUTE for only check pass in attribute test item
 * VARIABLE_AND_ATTRIBUTE for both variable and attribute
 * NONE for no P
 */
public enum PassPolicy {
    VARIABLE, ATTRIBUTE, VARIABLE_AND_ATTRIBUTE, NONE
}
