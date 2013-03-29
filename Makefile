

test:
	scala -cp /home/johnny/.ivy2/cache/junit/junit/jars/junit-4.11.jar:/home/johnny/.ivy2/cache/org.hamcrest/hamcrest-core/jars/hamcrest-core-1.3.jar:congeal-test/target/scala-2.11/test-classes:congeal-main/target/scala-2.11/classes \
	org.junit.runner.JUnitCore \
	congeal.ApiTest
