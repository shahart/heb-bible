#include "bible_count.h"

#include <stdio.h>

static int failed = 0;

static void check(const char *label, size_t actual, size_t expected) {
    if (actual != expected) {
        fprintf(stderr, "FAIL: %s (got %zu, want %zu)\n", label, actual, expected);
        failed = 1;
    } else {
        printf("PASS: %s\n", label);
    }
}

int main(void) {
    char *verses[] = {"אבג", "אxyzג", "אבxד", "hello world", "say hello", "xhelloy"};
    const size_t count = sizeof(verses) / sizeof(verses[0]);

    check("default matches first and last characters",
          count_by_name(verses, count, "אanythingג", 0), 2);
    check("default does not count an interior substring alone",
          count_by_name(verses, count, "hell", 0), 0);
    check("containsName matches substring occurrences",
          count_by_name(verses, count, "hell", 1), 3);
    check("containsName also matches boundaries",
          count_by_name(verses, count, "אxyzד", 1), 1);
    check("overlapping substring and boundary match counts once",
          count_by_name(verses, count, "hello world", 1), 1);
    check("empty name has no matches", count_by_name(verses, count, "", 1), 0);

    return failed ? 1 : 0;
}
