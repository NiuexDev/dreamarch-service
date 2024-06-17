package dev.niuex.dreamarch.Util;

public interface Callback<T, U> {
    T call(U arg);
}
