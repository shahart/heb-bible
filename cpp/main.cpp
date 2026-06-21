#include "httplib.h"
#include "utf8.hpp"

#include <zlib.h>

#include <array>
#include <exception>
#include <iostream>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

namespace {

constexpr int PORT = 3000;
constexpr size_t MAX_LINE = 8192;
constexpr auto JSON = "application/json";

class BibleRepository {
public:
    explicit BibleRepository(const char *path) {
        gzFile file = gzopen(path, "r");
        if (!file) {
            throw std::runtime_error(std::string("Cannot open ") + path);
        }

        std::array<char, MAX_LINE> line{};
        while (gzgets(file, line.data(), static_cast<int>(line.size()))) {
            std::string_view row(line.data());
            const size_t comma = row.find(',');
            if (comma == std::string_view::npos) continue;

            row.remove_prefix(comma + 1);
            while (!row.empty() && (row.back() == '\n' || row.back() == '\r')) {
                row.remove_suffix(1);
            }

            verses_.emplace_back(row);
        }

        gzclose(file);
        std::cerr << verses_.size() << " psukim\n";
    }

    size_t count() const {
        return verses_.size();
    }

    size_t count_by_name(std::string_view name) const {
        if (name.empty()) return 0;

        const std::string_view name_first = utf8_first_char(name);
        const std::string_view name_last = utf8_last_char(name);

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

private:
    std::vector<std::string> verses_;
};

std::string count_json(size_t count) {
    return "{\"count\":" + std::to_string(count) + "}";
}

std::string error_json(std::string_view message) {
    return "{\"error\":\"" + std::string(message) + "\"}";
}

void json(httplib::Response &res, std::string body, int status = 200) {
    res.status = status;
    res.set_content(std::move(body), JSON);
}

void configure_routes(httplib::Server &app, const BibleRepository &bible) {
    app.Get("/psukim", [&bible](const httplib::Request &, httplib::Response &res) {
        json(res, count_json(bible.count()));
    });

    app.Get("/psukim/:name", [&bible](const httplib::Request &req, httplib::Response &res) {
        const auto name = req.path_params.find("name");
        if (name == req.path_params.end() || name->second.empty()) {
            json(res, error_json("missing name"), 400);
            return;
        }

        json(res, count_json(bible.count_by_name(name->second)));
    });

    app.set_error_handler([](const httplib::Request &, httplib::Response &res) {
        if (res.status == 404) {
            json(res, error_json("not found"), 404);
            return;
        }
        json(res, error_json("request failed"), res.status);
    });

    app.set_exception_handler([](const httplib::Request &, httplib::Response &res, std::exception_ptr ep) {
        try {
            if (ep) std::rethrow_exception(ep);
        } catch (const std::exception &e) {
            std::cerr << "Request failed: " << e.what() << '\n';
        }
        json(res, error_json("internal server error"), 500);
    });
}

} // namespace

int main(int argc, char **argv) {
    const char *data_path = argc > 1 ? argv[1] : "../bible.txt.gz";

    try {
        BibleRepository bible(data_path);
        httplib::Server app;
        configure_routes(app, bible);

        std::cout << "Listening on http://0.0.0.0:" << PORT << '\n';
        if (!app.listen("0.0.0.0", PORT)) {
            std::cerr << "Failed to listen on port " << PORT << '\n';
            return 1;
        }
    } catch (const std::exception &e) {
        std::cerr << e.what() << '\n';
        return 1;
    }

    return 0;
}
