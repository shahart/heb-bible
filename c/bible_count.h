#ifndef BIBLE_COUNT_H
#define BIBLE_COUNT_H

#include <stddef.h>

size_t count_by_name(char *const verses[], size_t verse_count, const char *name, int contains_name);

#endif
