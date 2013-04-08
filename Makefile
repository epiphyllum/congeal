
test: compile run

compile: congeal-main-classes
	bin/ivy-scalac \
	-deprecation \
	-cp congeal-main/target/scala-2.11/classes \
	Test1.scala

run: congeal-main-classes
	bin/ivy-scala \
	-cp .:congeal-main/target/scala-2.11/classes \
	Test

congeal-main-classes:
	sbt -J-Xms128m -J-Xmx1g -J-XX:PermSize=64M -J-XX:MaxPermSize=512M "project congeal-main" compile

# FIX: should run sbt clean as well
clean:
	rm -rf *.class
