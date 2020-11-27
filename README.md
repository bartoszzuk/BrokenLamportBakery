# BrokenLamportBakery

Buggy implementation of Lamport's bakery algorithm with deterministic tests.  
Created to see [vmlens plugin](https://vmlens.com/) in action.

## Requirements

- Java SDK 1.8
- Maven

## Run

```sh
$ cd BrokenLamportBakery
$ mvn clean test
```

Chceck folder *target/interleave* for any interleavings which resulted in the failure of the tests.
