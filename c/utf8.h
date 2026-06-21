#ifndef UTF8_H
#define UTF8_H

int utf8_char_len(unsigned char c);
const char *utf8_first_char(const char *s, int *len);
const char *utf8_last_char(const char *s, int *len);
int utf8_char_eq(const char *a, int a_len, const char *b);

#endif
