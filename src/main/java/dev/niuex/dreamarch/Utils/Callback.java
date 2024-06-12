package dev.niuex.dreamarch.Utils;

public interface Callback<T, U> {
    T call(U arg);
}
