#include <stdio.h>
#include <string.h>
#include "utf8.h"

static int failed = 0;

static void test(const char *label, int cond) {
    if (!cond) {
        fprintf(stderr, "FAIL: %s\n", label);
        failed = 1;
    } else {
        printf("PASS: %s\n", label);
    }
}

static void test_first_char(const char *input, const char *expected) {
    int len;
    const char *p = utf8_first_char(input, &len);
    int ok = (int)strlen(expected) == len && strncmp(p, expected, len) == 0;
    char label[256];
    snprintf(label, sizeof(label), "utf8_first_char(\"%s\") == \"%s\"", input, expected);
    test(label, ok);
}

static void test_last_char(const char *input, const char *expected) {
    int len;
    const char *p = utf8_last_char(input, &len);
    int ok = p != NULL && (int)strlen(expected) == len && strncmp(p, expected, len) == 0;
    char label[256];
    snprintf(label, sizeof(label), "utf8_last_char(\"%s\") == \"%s\"", input, expected);
    test(label, ok);
}

int main(void) {
    /* ASCII strings */
    test_first_char("hello", "h");
    test_last_char("hello", "o");
    test_first_char("a", "a");
    test_last_char("a", "a");

    /* Hebrew: שחר */
    test_first_char("שחר", "ש");
    test_last_char("שחר", "ר");

    /* Single Hebrew character */
    test_first_char("ש", "ש");
    test_last_char("ש", "ש");

    /* Hebrew: בראשית */
    test_first_char("בראשית", "ב");
    test_last_char("בראשית", "ת");

    /* Mixed */
    test_first_char("abcשחרxyz", "a");
    test_last_char("abcשחרxyz", "z");

    if (failed) {
        fprintf(stderr, "\nSome tests FAILED\n");
        return 1;
    }
    printf("\nAll tests PASSED\n");
    return 0;
}
