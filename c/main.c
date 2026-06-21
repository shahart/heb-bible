#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <zlib.h>
#include "utf8.h"

#define PORT 3000
#define MAX_LINE 8192
#define INITIAL_CAP 200000
#define BUF_SIZE 65536

typedef struct {
    char **verses;
    size_t count;
    size_t cap;
} Bible;

static Bible *bible;

static Bible *load_bible(const char *path) {
    Bible *b = malloc(sizeof(Bible));
    b->count = 0;
    b->cap = INITIAL_CAP;
    b->verses = malloc(b->cap * sizeof(char *));

    gzFile f = gzopen(path, "r");
    if (!f) {
        fprintf(stderr, "Cannot open %s\n", path);
        free(b->verses);
        free(b);
        return NULL;
    }

    char line[MAX_LINE];
    while (gzgets(f, line, sizeof(line))) {
        char *comma = strchr(line, ',');
        if (!comma) continue;
        char *text = comma + 1;
        size_t len = strlen(text);
        while (len > 0 && (text[len - 1] == '\n' || text[len - 1] == '\r'))
            text[--len] = '\0';

        if (b->count >= b->cap) {
            b->cap *= 2;
            b->verses = realloc(b->verses, b->cap * sizeof(char *));
        }
        b->verses[b->count++] = strdup(text);

        // printf("%s\n", strdup(text));
    }
    gzclose(f);
    fprintf(stderr, "%zu psukim\n", b->count);
    return b;
}

static size_t count_by_name(const char *name) {
    if (!name || !*name) return 0;
    int name_first_len, name_last_len;
    const char *name_first = utf8_first_char(name, &name_first_len);
    const char *name_last = utf8_last_char(name, &name_last_len);

    size_t count = 0;
    for (size_t i = 0; i < bible->count; i++) {
        int v_first_len, v_last_len;
        const char *v_first = utf8_first_char(bible->verses[i], &v_first_len);
        const char *v_last = utf8_last_char(bible->verses[i], &v_last_len);
        if (utf8_char_eq(v_first, v_first_len, name_first) &&
            utf8_char_eq(v_last, v_last_len, name_last)) {
            count++;
            printf("%s\n", bible->verses[i]);
        }
    }
    return count;
}

static int hex_val(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    return -1;
}

static char *url_decode(const char *src) {
    size_t len = strlen(src);
    char *dst = malloc(len + 1);
    char *out = dst;
    for (const char *p = src; *p; p++) {
        if (*p == '%' && hex_val(p[1]) >= 0 && hex_val(p[2]) >= 0) {
            *out++ = (hex_val(p[1]) << 4) | hex_val(p[2]);
            p += 2;
        } else if (*p == '+') {
            *out++ = ' ';
        } else {
            *out++ = *p;
        }
    }
    *out = '\0';
    return dst;
}

static void handle_client(int client_fd) {
    char buf[BUF_SIZE];
    ssize_t n = read(client_fd, buf, sizeof(buf) - 1);
    if (n <= 0) { close(client_fd); return; }
    buf[n] = '\0';

    char method[16], path[1024];
    if (sscanf(buf, "%15s %1023s", method, path) != 2) {
        close(client_fd);
        return;
    }

    const char *resp_body;
    char body_buf[256];
    int status = 200;
    char content_type[64] = "application/json";

    if (strcmp(method, "GET") != 0) {
        status = 405;
        resp_body = "{\"error\":\"method not allowed\"}";
    } else if (strcmp(path, "/psukim") == 0) {
        snprintf(body_buf, sizeof(body_buf), "{\"count\":%zu}", bible->count);
        resp_body = body_buf;
    } else if (strncmp(path, "/psukim/", 8) == 0) {
        char *decoded = url_decode(path + 8);
        size_t c = count_by_name(decoded);
        snprintf(body_buf, sizeof(body_buf), "{\"count\":%zu}", c);
        resp_body = body_buf;
        free(decoded);
    } else {
        status = 404;
        resp_body = "{\"error\":\"not found\"}";
    }

    char resp[BUF_SIZE];
    int rlen = snprintf(resp, sizeof(resp),
        "HTTP/1.1 %d %s\r\n"
        "Content-Type: %s\r\n"
        "Content-Length: %zu\r\n"
        "Connection: close\r\n"
        "\r\n"
        "%s",
        status, status == 200 ? "OK" : status == 404 ? "Not Found" : "Method Not Allowed",
        content_type, strlen(resp_body), resp_body);

    write(client_fd, resp, rlen);
    close(client_fd);
}

int main(int argc, char **argv) {
    const char *data_path = argc > 1 ? argv[1] : "../bible.txt.gz";
    bible = load_bible(data_path);
    if (!bible) return 1;

    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) { perror("socket"); return 1; }

    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        perror("bind"); return 1;
    }
    if (listen(server_fd, 10) < 0) {
        perror("listen"); return 1;
    }

    printf("Listening on http://0.0.0.0:%d\n", PORT);

    struct sockaddr_in client_addr;
    socklen_t client_len = sizeof(client_addr);
    while (1) {
        int client_fd = accept(server_fd, (struct sockaddr *)&client_addr, &client_len);
        if (client_fd < 0) { perror("accept"); continue; }
        handle_client(client_fd);
    }

    close(server_fd);
    return 0;
}
