#include "utf8.h"
#include <string.h>

int utf8_char_len(unsigned char c) {
    if (c < 0x80) return 1;
    else if ((c & 0xE0) == 0xC0) return 2;
    else if ((c & 0xF0) == 0xE0) return 3;
    else if ((c & 0xF8) == 0xF0) return 4;
    return 1;
}

const char *utf8_first_char(const char *s, int *len) {
    *len = utf8_char_len((unsigned char)*s);
    return s;
}

const char *utf8_last_char(const char *s, int *len) {
    size_t slen = strlen(s);
    if (slen == 0) return NULL;
    const char *p = s + slen - 1;
    while (p > s && ((unsigned char)*p & 0xC0) == 0x80) p--;
    *len = utf8_char_len((unsigned char)*p);
    return p;
}

int utf8_char_eq(const char *a, int a_len, const char *b) {
    int b_len = utf8_char_len((unsigned char)*b);
    return a_len == b_len && strncmp(a, b, a_len) == 0;
}
