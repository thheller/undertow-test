# undertow-test

Just testing how to drive undertow via Clojure.

Rougly based on
https://github.com/TechEmpower/FrameworkBenchmarks/tree/master/frameworks/Java/undertow

## Usage

```
lein uberjar
java -cp target/undertow-test-0.1.0-SNAPSHOT-standalone.jar clojure.main -m undertow-test.core localhost 5500 
```
