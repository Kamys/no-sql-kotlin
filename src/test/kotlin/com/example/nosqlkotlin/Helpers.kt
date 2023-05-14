package com.example.nosqlkotlin

import io.kotest.matchers.collections.shouldHaveSize

fun <T> Iterable<T>.ensureFirst(): T = shouldHaveSize(1).first()