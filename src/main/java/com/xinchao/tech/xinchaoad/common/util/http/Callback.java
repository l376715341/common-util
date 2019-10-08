package com.xinchao.tech.xinchaoad.common.util.http;

public interface Callback<T, P> {

    T invoke(P p);

}
