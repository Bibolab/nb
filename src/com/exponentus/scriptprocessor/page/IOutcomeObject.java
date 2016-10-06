package com.exponentus.scriptprocessor.page;

public interface IOutcomeObject {
    default String toXML() {
        return toString();
    }

    default Object toJSON() {
        return this;
    }
}
