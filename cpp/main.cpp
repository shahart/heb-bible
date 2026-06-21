#include "utf8.hpp"

#include <zlib.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

#include <array>
#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <sstream>
#include <string>
#include <string_view>
#include <vector>

namespace {

constexpr int PORT = 3000;
constexpr size_t MAX_LINE = 8192;
constexpr size_t BUF_SIZE = 65536;

class Bible {
public:
    static Bible load(const char *path) {
        Bible bible;
        gzFile file = gzopen(path, "r");
        if (!file) {
            throw std::runtime_error(std::string("Cannot open ") + path);
        }

        std::array<char, MAX_LINE> line{};
        while (gzgets(file, line.data(), static_cast<int>(line.size()))) {
            char *comma = std::strchr(line.data(), ',');
            if (!comma) continue;

            char *text = comma + 1;
            size_t len = std::strlen(text);
            while (len > 0 && (text[len - 1] == '\n' || text[len - 1] == '\r')) {
                text[--len] = '\0';
            }

            bible.verses_.emplace_back(text);
        }

        gzclose(file);
        std::cerr << bible.verses_.size() << " psukim\n";
        return bible;
    }

    size_t count() const {
        return verses_.size();
    }

    size_t count_by_name(std::string_view name) const {
        if (name.empty()) return 0;

        const std::string_view name_first = utf8_first_char(name);
        const std::string_view name_last = utf8_last_char(name);

        size_t matches = 0;
        for (const std::string &verse : verses_) {
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

int hex_val(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    return -1;
}

std::string url_decode(std::string_view src) {
    std::string dst;
    dst.reserve(src.size());

    for (size_t i = 0; i < src.size(); ++i) {
        if (src[i] == '%' && i + 2 < src.size() &&
            hex_val(src[i + 1]) >= 0 && hex_val(src[i + 2]) >= 0) {
            dst.push_back(static_cast<char>((hex_val(src[i + 1]) << 4) | hex_val(src[i + 2])));
            i += 2;
        } else if (src[i] == '+') {
            dst.push_back(' ');
        } else {
            dst.push_back(src[i]);
        }
    }

    return dst;
}

void write_all(int fd, std::string_view data) {
    while (!data.empty()) {
        ssize_t written = write(fd, data.data(), data.size());
        if (written < 0) {
            if (errno == EINTR) continue;
            return;
        }
        data.remove_prefix(static_cast<size_t>(written));
    }
}

void handle_client(int client_fd, const Bible &bible) {
    std::array<char, BUF_SIZE> buf{};
    ssize_t n = read(client_fd, buf.data(), buf.size() - 1);
    if (n <= 0) {
        close(client_fd);
        return;
    }
    buf[static_cast<size_t>(n)] = '\0';

    std::istringstream request(std::string(buf.data()));
    std::string method;
    std::string path;
    if (!(request >> method >> path)) {
        close(client_fd);
        return;
    }

    int status = 200;
    std::string reason = "OK";
    std::string body;

    if (method != "GET") {
        status = 405;
        reason = "Method Not Allowed";
        body = "{\"error\":\"method not allowed\"}";
    } else if (path == "/psukim") {
        body = "{\"count\":" + std::to_string(bible.count()) + "}";
    } else if (path.rfind("/psukim/", 0) == 0) {
        const std::string decoded = url_decode(std::string_view(path).substr(8));
        body = "{\"count\":" + std::to_string(bible.count_by_name(decoded)) + "}";
    } else {
        status = 404;
        reason = "Not Found";
        body = "{\"error\":\"not found\"}";
    }

    std::ostringstream response;
    response << "HTTP/1.1 " << status << ' ' << reason << "\r\n"
             << "Content-Type: application/json\r\n"
             << "Content-Length: " << body.size() << "\r\n"
             << "Connection: close\r\n"
             << "\r\n"
             << body;

    const std::string response_text = response.str();
    write_all(client_fd, response_text);
    close(client_fd);
}

} // namespace

int main(int argc, char **argv) {
    const char *data_path = argc > 1 ? argv[1] : "../bible.txt.gz";

    Bible bible;
    try {
        bible = Bible::load(data_path);
    } catch (const std::exception &e) {
        std::cerr << e.what() << '\n';
        return 1;
    }

    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        perror("socket");
        return 1;
    }

    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(PORT);

    if (bind(server_fd, reinterpret_cast<sockaddr *>(&addr), sizeof(addr)) < 0) {
        perror("bind");
        close(server_fd);
        return 1;
    }

    if (listen(server_fd, 10) < 0) {
        perror("listen");
        close(server_fd);
        return 1;
    }

    std::cout << "Listening on http://0.0.0.0:" << PORT << '\n';

    while (true) {
        sockaddr_in client_addr{};
        socklen_t client_len = sizeof(client_addr);
        int client_fd = accept(server_fd, reinterpret_cast<sockaddr *>(&client_addr), &client_len);
        if (client_fd < 0) {
            perror("accept");
            continue;
        }
        handle_client(client_fd, bible);
    }

    close(server_fd);
    return 0;
}
