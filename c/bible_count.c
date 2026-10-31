#include "bible_count.h"
#include "utf8.h"

#include <string.h>

size_t count_by_name(char *const verses[], size_t verse_count, const char *name, int contains_name) {
    if (!name || !*name) return 0;
    int name_first_len, name_last_len;
    const char *name_first = utf8_first_char(name, &name_first_len);
    const char *name_last = utf8_last_char(name, &name_last_len);

    size_t count = 0;
    for (size_t i = 0; i < verse_count; i++) {
        int v_first_len, v_last_len;
        const char *v_first = utf8_first_char(verses[i], &v_first_len);
        const char *v_last = utf8_last_char(verses[i], &v_last_len);
        int boundary_match = utf8_char_eq(v_first, v_first_len, name_first) &&
                             utf8_char_eq(v_last, v_last_len, name_last);
        if (boundary_match || (contains_name && strstr(verses[i], name) != NULL)) ++count;
    }
    return count;
}
