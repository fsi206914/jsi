JFLAGS = -d ./bin/
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java


CLASSES = \
		edu/liang/Arithmetic.java\
		edu/liang/GenericPoint.java\
		edu/liang/Point.java\
		edu/liang/Rectangle.java\
		edu/liang/rtree/Node.java\
		edu/liang/rtree/RTree.java\
		edu/liang/rtree/RTreeTestMain.java\
		edu/liang/rtree/RTreeTest.java\
		edu/liang/rtree/Node.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	rm -rf ../bin/*

compile:
	$(JC) $(JFLAGS) -cp ./lib/junit-4.11.jar:./lib/slf4j-api-1.7.5.jar:./lib/slf4j-simple-1.7.5.jar:. $(CLASSES)

rtreeMain:
	java  -cp ./bin/:./lib/slf4j-api-1.7.5.jar:./lib/slf4j-simple-1.7.5.jar edu.liang.rtree.RTreeTestMain
