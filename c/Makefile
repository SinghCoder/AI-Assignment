CC=gcc
CFLAGS=-O3 -fPIC -Wall \
		-I /opt/java/include \
		-I /usr/lib/jvm/java-6-sun-1.6.0.16/include \
		-I /usr/lib/jvm/java-6-sun-1.6.0.16/include/linux \
		-I /usr/lib/jvm/java-6-sun-1.6.0.12/include \
		-I /usr/lib/jvm/java-6-sun-1.6.0.12/include/linux \
		-I /opt/java/include/linux
LDFLAGS=-shared
LIBFILE=libstatmatrix.so

.SILENT : clean

all : libstatematrix.so

libstatematrix.so : jni_statematrix.o statematrix.o
	$(CC) $(LDFLAGS) -o $@ $^

jni_statematrix.o : jni_statematrix.c jni_statematrix.h statematrix.h

statematrix.o : statematrix.c statematrix.h

.o :
	$(CC) -c $(CFLAGS) $< -o $@

clean :
	rm -f *.o *.so
