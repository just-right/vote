package com.example.vote.service;

import java.util.function.Supplier;

public interface IDistributedLockerService {
    <T> T lock(String lockName, Supplier<T> supplier);
    <T> T lock(String lockName, Supplier<T> supplier,long lockTime);
}
