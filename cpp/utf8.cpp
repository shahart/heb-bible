#include "utf8.hpp"

int utf8_char_len(unsigned char c) {
    if (c < 0x80) return 1;
    if ((c & 0xE0) == 0xC0) return 2;
    if ((c & 0xF0) == 0xE0) return 3;
    if ((c & 0xF8) == 0xF0) return 4;
    return 1;
}

std::string_view utf8_first_char(std::string_view s) {
    if (s.empty()) return {};
    const auto len = static_cast<size_t>(utf8_char_len(static_cast<unsigned char>(s.front())));
    return s.substr(0, len);
}

std::string_view utf8_last_char(std::string_view s) {
    if (s.empty()) return {};

    size_t pos = s.size() - 1;
    while (pos > 0 && (static_cast<unsigned char>(s[pos]) & 0xC0) == 0x80) {
        --pos;
    }

    const auto len = static_cast<size_t>(utf8_char_len(static_cast<unsigned char>(s[pos])));
    return s.substr(pos, len);
}

bool utf8_char_eq(std::string_view a, std::string_view b) {
    return a == b;
}
