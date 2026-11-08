# Spring Batch 6: skipped processor exception fails the step

Minimal reproducer for a Spring Batch 6 regression: in a fault-tolerant chunk step, a
skippable exception thrown by a `@Transactional` (REQUIRED) service called from the
processor is skipped, but the step then fails with `SilentlyRolledBackException`
instead of continuing with the next chunk.

Issue: _link to be added_

## Run

```
./mvnw test
```

`OrderBatchJobTest` creates orders with order numbers 2, 3, 4, 5 and 6, then starts the
job through `POST /api/batch/process-orders`. The service throws for odd order numbers;
all exceptions are skippable (chunk size 1, unlimited skips).

| | Expected | Actual |
|---|---|---|
| Job status | `COMPLETED` | `FAILED` |
| Processed orders | 2, 4, 6 | 6 |
| Skips logged | 2 | 1 |

```
ERROR o.s.b.core.step.item.ChunkOrientedStep : Chunk write completed but the transaction was independently marked rollback-only
ChunkOrientedStep$SilentlyRolledBackException: The chunk write completed without an exception, but the transaction is rollback-only
```

## Where to look

- Step configuration: [`OrderJobConfig`](src/main/java/com/example/batch/job/OrderJobConfig.java)
- Transactional service called by the processor: [`OrderProcessingService`](src/main/java/com/example/batch/order/OrderProcessingService.java)
- Test: [`OrderBatchJobTest`](src/test/java/com/example/batch/OrderBatchJobTest.java)

## Versions

Spring Batch 6.0.6, Spring Boot 4.1.2, Hibernate / `JpaTransactionManager`, H2, Java 25.
