
# /home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-actors/jars/scala-actors-2.11.0-SNAPSHOT.jar
# /home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/jline/jars/jline-2.11.0-SNAPSHOT.jar
# /home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-compiler/jars/scala-compiler-2.11.0-SNAPSHOT.jar
# /home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-library/jars/scala-library-2.11.0-SNAPSHOT.jar
# /home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-reflect/jars/scala-reflect-2.11.0-SNAPSHOT.jar

LIBS=$(wildcard /home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/*/jars/*-2.11.0-SNAPSHOT.jar)
empty:=
space:= $(empty) $(empty)
LIB_PATH:=$(subst $(space),:,$(LIBS))

test: compile run

compile: congeal-main-jar
	java -Xmx256M -Xms32M \
	-Xbootclasspath/a:$(LIB_PATH) \
	-Dscala.usejavacp=true \
	scala.tools.nsc.Main \
	-deprecation \
	-cp congeal-main/target/scala-2.11/congeal-main_2.11-0.0.0.jar \
	Test1.scala

run: congeal-main-jar
	java -Xmx256M -Xms32M \
	-Xbootclasspath/a:$(LIB_PATH) \
	-cp .:congeal-main/target/scala-2.11/congeal-main_2.11-0.0.0.jar \
	Test

congeal-main-jar: ; sbt package

clean:
	rm -rf *.class
