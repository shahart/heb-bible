#ifndef UTF8_HPP
#define UTF8_HPP

#include <string_view>

int utf8_char_len(unsigned char c);
std::string_view utf8_first_char(std::string_view s);
std::string_view utf8_last_char(std::string_view s);
bool utf8_char_eq(std::string_view a, std::string_view b);

#endif
