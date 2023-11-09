# SynchronizedQueueWithMultipleProducerandConsumer
Supplied one thread, called the producer, which kept inserting strings into the queue as long as there were fewer than ten elements in it. When the queue got too full, the thread waited. Supplied a second thread, called the consumer, that kept removing and printing strings from the queue as long as the queue was not empty.
