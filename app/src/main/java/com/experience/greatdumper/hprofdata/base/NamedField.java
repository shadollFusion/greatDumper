package com.experience.greatdumper.hprofdata.base;

/**
 * Base class for named fields (instance and static fields)
 *
 */
public interface NamedField {

    /**
     * Returns the field name string string id.
     *
     * @return The field name string id
     */
    ID getFieldNameId();

    /**
     * Set the field name string id.
     *
     * @param fieldNameId The field name string id
     */
    void setFieldNameId(ID fieldNameId);
}
