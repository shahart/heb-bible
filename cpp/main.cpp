#include "httplib.h"
#include "bible_repository.hpp"

#include <exception>
#include <iostream>
#include <stdexcept>
#include <string>

namespace {

constexpr int PORT = 3000;
constexpr auto JSON = "application/json";

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

        const auto contains = req.get_param_value("containsName");
        const bool contains_name = contains == "true" || contains == "1" ||
                                   contains == "t" || contains == "TRUE" ||
                                   contains == "True" || contains == "T";
        json(res, count_json(bible.count_by_name(name->second, contains_name)));
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
