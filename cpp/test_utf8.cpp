#include "utf8.hpp"

#include <cstdio>
#include <string_view>

namespace {

int failed = 0;

void test(const char *label, bool cond) {
    if (!cond) {
        std::fprintf(stderr, "FAIL: %s\n", label);
        failed = 1;
    } else {
        std::printf("PASS: %s\n", label);
    }
}

void test_first_char(std::string_view input, std::string_view expected) {
    char label[256];
    std::snprintf(label, sizeof(label), "utf8_first_char(\"%.*s\") == \"%.*s\"",
                  static_cast<int>(input.size()), input.data(),
                  static_cast<int>(expected.size()), expected.data());
    test(label, utf8_first_char(input) == expected);
}

void test_last_char(std::string_view input, std::string_view expected) {
    char label[256];
    std::snprintf(label, sizeof(label), "utf8_last_char(\"%.*s\") == \"%.*s\"",
                  static_cast<int>(input.size()), input.data(),
                  static_cast<int>(expected.size()), expected.data());
    test(label, utf8_last_char(input) == expected);
}

} // namespace

int main() {
    test_first_char("hello", "h");
    test_last_char("hello", "o");
    test_first_char("a", "a");
    test_last_char("a", "a");

    test_first_char("שחר", "ש");
    test_last_char("שחר", "ר");

    test_first_char("ש", "ש");
    test_last_char("ש", "ש");

    test_first_char("בראשית", "ב");
    test_last_char("בראשית", "ת");

    test_first_char("abcשחרxyz", "a");
    test_last_char("abcשחרxyz", "z");

    if (failed) {
        std::fprintf(stderr, "\nSome tests FAILED\n");
        return 1;
    }

    std::printf("\nAll tests PASSED\n");
    return 0;
}
