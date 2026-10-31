.PHONY: all clean run test

CC = cc
CFLAGS = -Wall -Wextra -O2
LDLIBS = -lz

all: heb-bible

heb-bible: main.c bible_count.c bible_count.h utf8.c utf8.h
	$(CC) $(CFLAGS) -o $@ main.c bible_count.c utf8.c $(LDLIBS)

test_utf8: test_utf8.c utf8.c utf8.h
	$(CC) $(CFLAGS) -o $@ test_utf8.c utf8.c

test_bible_count: test_bible_count.c bible_count.c bible_count.h utf8.c utf8.h
	$(CC) $(CFLAGS) -o $@ test_bible_count.c bible_count.c utf8.c

run: heb-bible
	./heb-bible

test: test_utf8 test_bible_count
	./test_utf8
	./test_bible_count

clean:
	rm -f heb-bible test_utf8 test_bible_count
