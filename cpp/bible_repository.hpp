#ifndef BIBLE_REPOSITORY_HPP
#define BIBLE_REPOSITORY_HPP

#include "utf8.hpp"

#include <array>
#include <iostream>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>
#include <zlib.h>

class BibleRepository {
public:
    explicit BibleRepository(const char *path) {
        gzFile file = gzopen(path, "r");
        if (!file) throw std::runtime_error(std::string("Cannot open ") + path);

        std::array<char, 8192> line{};
        while (gzgets(file, line.data(), static_cast<int>(line.size()))) {
            std::string_view row(line.data());
            const size_t comma = row.find(',');
            if (comma == std::string_view::npos) continue;
            row.remove_prefix(comma + 1);
            while (!row.empty() && (row.back() == '\n' || row.back() == '\r')) row.remove_suffix(1);
            verses_.emplace_back(row);
        }
        gzclose(file);
    }

    size_t count() const { return verses_.size(); }

    size_t count_by_name(std::string_view name) const {
        if (name.empty()) return 0;
        const auto name_first = utf8_first_char(name);
        const auto name_last = utf8_last_char(name);
        size_t matches = 0;
        for (const auto &verse : verses_) {
            const std::string_view verse_view(verse);
            if (utf8_char_eq(utf8_first_char(verse_view), name_first) &&
                utf8_char_eq(utf8_last_char(verse_view), name_last)) {
                ++matches;
                std::cout << verse << '\n';
            }
        }
        return matches;
    }

    size_t count_by_name(std::string_view name, bool contains_name) const {
        if (!contains_name) return count_by_name(name);
        if (name.empty()) return 0;

        const auto name_first = utf8_first_char(name);
        const auto name_last = utf8_last_char(name);
        size_t matches = 0;
        for (const auto &verse : verses_) {
            const std::string_view verse_view(verse);
            const bool contains = verse_view.find(name) != std::string_view::npos;
            const bool boundary_match = utf8_char_eq(utf8_first_char(verse_view), name_first) &&
                                        utf8_char_eq(utf8_last_char(verse_view), name_last);
            if (contains || boundary_match) ++matches;
        }
        return matches;
    }

private:
    std::vector<std::string> verses_;
};

#endif
