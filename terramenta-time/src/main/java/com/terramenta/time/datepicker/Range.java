/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.datepicker;

/**
 *
 * @author Chris.Heidt
 * @param <T>
 */
public class Range<T extends Number> {

    private T start, end;

    /**
     *
     * @param start
     * @param end
     */
    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @return
     */
    public T getStart() {
        return start;
    }

    /**
     *
     * @param start
     */
    public void setStart(T start) {
        this.start = start;
    }

    /**
     *
     * @return
     */
    public T getEnd() {
        return end;
    }

    /**
     *
     * @param end
     */
    public void setEnd(T end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}
