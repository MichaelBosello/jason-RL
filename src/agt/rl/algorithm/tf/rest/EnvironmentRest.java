package rl.algorithm.tf.rest;

import java.util.List;

public class EnvironmentRest<T> {
    private List<T> a_shape;
    private String a_type;
    private List<T> a_min;
    private List<T> a_max;
    private List<T> o_shape;
    private String o_type;
    private List<T> o_min;
    private List<T> o_max;
    private List<T> init_state;

    public List<T> getA_shape() {
        return a_shape;
    }

    public void setA_shape(List<T> a_shape) {
        this.a_shape = a_shape;
    }

    public String getA_type() {
        return a_type;
    }

    public void setA_type(String a_type) {
        this.a_type = a_type;
    }

    public List<T> getA_min() {
        return a_min;
    }

    public void setA_min(List<T> a_min) {
        this.a_min = a_min;
    }

    public List<T> getA_max() {
        return a_max;
    }

    public void setA_max(List<T> a_max) {
        this.a_max = a_max;
    }

    public List<T> getO_shape() {
        return o_shape;
    }

    public void setO_shape(List<T> o_shape) {
        this.o_shape = o_shape;
    }

    public String getO_type() {
        return o_type;
    }

    public void setO_type(String o_type) {
        this.o_type = o_type;
    }

    public List<T> getO_min() {
        return o_min;
    }

    public void setO_min(List<T> o_min) {
        this.o_min = o_min;
    }

    public List<T> getO_max() {
        return o_max;
    }

    public void setO_max(List<T> o_max) {
        this.o_max = o_max;
    }

    public List<T> getInit_state() {
        return init_state;
    }

    public void setInit_state(List<T> init_state) {
        this.init_state = init_state;
    }
}
