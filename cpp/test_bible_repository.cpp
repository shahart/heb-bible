#include "bible_repository.hpp"

#include <cstdio>
#include <cstdlib>
#include <string>
#include <unistd.h>
#include <zlib.h>

namespace {
int failed = 0;

void check(const char *label, bool condition) {
    if (!condition) {
        std::fprintf(stderr, "FAIL: %s\n", label);
        failed = 1;
    } else {
        std::printf("PASS: %s\n", label);
    }
}

class Fixture {
public:
    Fixture() {
        char pattern[] = "/tmp/heb-bible-test-XXXXXX";
        const int fd = mkstemp(pattern);
        if (fd < 0) std::abort();
        close(fd);
        path_ = pattern;
        gzFile file = gzopen(path_.c_str(), "wb");
        if (!file) std::abort();
        gzputs(file, "1,אבג\n2,אxyzג\r\n3,אבxד\n4,hello world\n5,say hello\n6,xhelloy\n7,\ninvalid row\n");
        gzclose(file);
    }
    ~Fixture() { std::remove(path_.c_str()); }
    const char *path() const { return path_.c_str(); }

private:
    std::string path_;
};
} // namespace

int main() {
    Fixture fixture;
    BibleRepository bible(fixture.path());
    check("loads rows containing a comma", bible.count() == 7);
    check("matches verses by Hebrew first and last characters", bible.count_by_name("אanythingג") == 2);
    check("does not match when only the first character agrees", bible.count_by_name("אxyzד") == 1);
    check("matches ASCII first and last characters", bible.count_by_name("hd") == 1);
    check("containsName counts substring matches", bible.count_by_name("hell", true) == 3);
    check("containsName also includes boundary matches", bible.count_by_name("אxyzד", true) == 1);
    check("containsName false keeps boundary matching", bible.count_by_name("אanythingג", false) == 2);
    check("empty name has no matches", bible.count_by_name("") == 0);
    check("no matching boundary characters returns zero", bible.count_by_name("xyz") == 0);

    if (failed) return 1;
    std::puts("All repository tests PASSED");
    return 0;
}
