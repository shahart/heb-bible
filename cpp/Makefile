.PHONY: all clean run test

CXX = c++
CXXFLAGS = -std=c++17 -Wall -Wextra -O2
LDLIBS = -lz

all: heb-bible

heb-bible: main.cpp utf8.cpp utf8.hpp
	$(CXX) $(CXXFLAGS) -o $@ main.cpp utf8.cpp $(LDLIBS)

test_utf8: test_utf8.cpp utf8.cpp utf8.hpp
	$(CXX) $(CXXFLAGS) -o $@ test_utf8.cpp utf8.cpp

run: heb-bible
	./heb-bible

test: test_utf8
	./test_utf8

clean:
	rm -f heb-bible test_utf8
