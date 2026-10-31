.PHONY: all clean run test

CXX = c++
CXXFLAGS = -std=c++17 -Wall -Wextra -O2 -pthread
LDLIBS = -lz

all: heb-bible

heb-bible: main.cpp utf8.cpp utf8.hpp httplib.h
	$(CXX) $(CXXFLAGS) -o $@ main.cpp utf8.cpp $(LDLIBS)

test_utf8: test_utf8.cpp utf8.cpp utf8.hpp
	$(CXX) $(CXXFLAGS) -o $@ test_utf8.cpp utf8.cpp

test_bible_repository: test_bible_repository.cpp bible_repository.hpp utf8.cpp utf8.hpp
	$(CXX) $(CXXFLAGS) -o $@ test_bible_repository.cpp utf8.cpp $(LDLIBS)

run: heb-bible
	./heb-bible

test: test_utf8 test_bible_repository
	./test_utf8
	./test_bible_repository

clean:
	rm -f heb-bible test_utf8 test_bible_repository
